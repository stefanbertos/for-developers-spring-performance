package com.example.demo.product.domain;

import com.example.demo.product.api.ProductRequest;
import com.example.demo.product.api.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        // Setup test product
        product = new Product(
                "Test Product",
                "Test Description",
                new BigDecimal("99.99"),
                "Test Category",
                "https://example.com/test.jpg",
                true
        );
        // Use reflection to set the ID field
        try {
            var field = Product.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(product, 1L);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set product ID", e);
        }

        // Setup test product request
        productRequest = new ProductRequest(
                "Test Product",
                "Test Description",
                new BigDecimal("99.99"),
                "Test Category",
                "https://example.com/test.jpg",
                true
        );
    }

    @Test
    void getAllProducts_ShouldReturnPageOfProducts() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);
        when(productRepository.findAll(pageable)).thenReturn(productPage);

        // Act
        Page<ProductResponse> result = productService.getAllProducts(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).name()).isEqualTo(product.getName());
        verify(productRepository).findAll(pageable);
    }

    @Test
    void getProductsByCategory_ShouldReturnPageOfProductsInCategory() {
        // Arrange
        String category = "Test Category";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);
        when(productRepository.findByCategory(category, pageable)).thenReturn(productPage);

        // Act
        Page<ProductResponse> result = productService.getProductsByCategory(category, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).category()).isEqualTo(category);
        verify(productRepository).findByCategory(category, pageable);
    }

    @Test
    void getProductsByName_ShouldReturnPageOfProductsWithNameContaining() {
        // Arrange
        String name = "Test";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);
        when(productRepository.findByNameContainingIgnoreCase(name, pageable)).thenReturn(productPage);

        // Act
        Page<ProductResponse> result = productService.getProductsByName(name, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).name()).contains(name);
        verify(productRepository).findByNameContainingIgnoreCase(name, pageable);
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        Long id = 1L;
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        // Act
        ProductResponse result = productService.getProductById(id);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(id);
        verify(productRepository).findById(id);
    }

    @Test
    void getProductById_WhenProductDoesNotExist_ShouldThrowException() {
        // Arrange
        Long id = 999L;
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.getProductById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Product not found with ID: " + id);
        verify(productRepository).findById(id);
    }

    @Test
    void createProduct_WhenNameIsUnique_ShouldCreateProduct() {
        // Arrange
        when(productRepository.findByNameIgnoreCase(productRequest.name())).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductResponse result = productService.createProduct(productRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(productRequest.name());
        verify(productRepository).findByNameIgnoreCase(productRequest.name());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_WhenNameExists_ShouldThrowException() {
        // Arrange
        when(productRepository.findByNameIgnoreCase(productRequest.name())).thenReturn(Optional.of(product));

        // Act & Assert
        assertThatThrownBy(() -> productService.createProduct(productRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product with name '" + productRequest.name() + "' already exists");
        verify(productRepository).findByNameIgnoreCase(productRequest.name());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateProduct_WhenProductExistsAndNameIsUnique_ShouldUpdateProduct() {
        // Arrange
        Long id = 1L;
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.findByNameIgnoreCase(productRequest.name())).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductResponse result = productService.updateProduct(id, productRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(id);
        verify(productRepository).findById(id);
        verify(productRepository).findByNameIgnoreCase(productRequest.name());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_WhenProductDoesNotExist_ShouldThrowException() {
        // Arrange
        Long id = 999L;
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.updateProduct(id, productRequest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Product not found with ID: " + id);
        verify(productRepository).findById(id);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateProduct_WhenNameExistsForDifferentProduct_ShouldThrowException() {
        // Arrange
        Long id = 1L;
        Product existingProduct = new Product(
                productRequest.name(),
                "Another Description",
                new BigDecimal("199.99"),
                "Another Category",
                "https://example.com/another.jpg",
                true
        );
        // Use reflection to set the ID field to a different ID
        try {
            var field = Product.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(existingProduct, 2L);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set product ID", e);
        }

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.findByNameIgnoreCase(productRequest.name())).thenReturn(Optional.of(existingProduct));

        // Act & Assert
        assertThatThrownBy(() -> productService.updateProduct(id, productRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Another product with name '" + productRequest.name() + "' already exists");
        verify(productRepository).findById(id);
        verify(productRepository).findByNameIgnoreCase(productRequest.name());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_WhenProductExists_ShouldDeleteProduct() {
        // Arrange
        Long id = 1L;
        when(productRepository.existsById(id)).thenReturn(true);
        doNothing().when(productRepository).deleteById(id);

        // Act
        productService.deleteProduct(id);

        // Assert
        verify(productRepository).existsById(id);
        verify(productRepository).deleteById(id);
    }

    @Test
    void deleteProduct_WhenProductDoesNotExist_ShouldThrowException() {
        // Arrange
        Long id = 999L;
        when(productRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> productService.deleteProduct(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Product not found with ID: " + id);
        verify(productRepository).existsById(id);
        verify(productRepository, never()).deleteById(any());
    }
}