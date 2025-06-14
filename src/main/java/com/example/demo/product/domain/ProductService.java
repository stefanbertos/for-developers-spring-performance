package com.example.demo.product.domain;

import com.example.demo.product.api.ProductRequest;
import com.example.demo.product.api.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for product operations.
 */
@Service
public class ProductService {

	private static final Logger log = LoggerFactory.getLogger(ProductService.class);

	private final ProductRepository productRepository;

	private final CurrencyExchangeService currencyExchangeService;

	public ProductService(ProductRepository productRepository, CurrencyExchangeService currencyExchangeService) {
		this.productRepository = productRepository;
		this.currencyExchangeService = currencyExchangeService;
	}

	/**
	 * Get all products with pagination.
	 * @param pageable pagination information
	 * @return page of product responses
	 */
	@Transactional(readOnly = true)
	public Page<ProductResponse> getAllProducts(Pageable pageable) {
		if (log.isDebugEnabled()) {
			log.debug("Getting all products with pagination: {}", pageable);
		}
		return productRepository.findAll(pageable)
			.map(product -> ProductResponse.fromEntity(product, currencyExchangeService));
	}

	/**
	 * Get products by category with pagination.
	 * @param category category to filter by
	 * @param pageable pagination information
	 * @return page of product responses
	 */
	@Transactional(readOnly = true)
	public Page<ProductResponse> getProductsByCategory(String category, Pageable pageable) {
		if (log.isDebugEnabled()) {
			log.debug("Getting products by category: {} with pagination: {}", category, pageable);
		}
		return productRepository.findByCategory(category, pageable)
			.map(product -> ProductResponse.fromEntity(product, currencyExchangeService));
	}

	/**
	 * Get products by name containing the given string with pagination.
	 * @param name name substring to search for
	 * @param pageable pagination information
	 * @return page of product responses
	 */
	@Transactional(readOnly = true)
	public Page<ProductResponse> getProductsByName(String name, Pageable pageable) {
		if (log.isDebugEnabled()) {
			log.debug("Getting products by name containing: {} with pagination: {}", name, pageable);
		}
		return productRepository.findByNameContainingIgnoreCase(name, pageable)
			.map(product -> ProductResponse.fromEntity(product, currencyExchangeService));
	}

	/**
	 * Get a product by ID.
	 * @param id product ID
	 * @return product response
	 * @throws EntityNotFoundException if product not found
	 */
	@Transactional(readOnly = true)
	public ProductResponse getProductById(Long id) {
		if (log.isDebugEnabled()) {
			log.debug("Getting product by ID: {}", id);
		}
		return productRepository.findById(id)
			.map(product -> ProductResponse.fromEntity(product, currencyExchangeService))
			.orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + id));
	}

	/**
	 * Create a new product.
	 * @param request product request
	 * @return created product response
	 */
	@Transactional
	public ProductResponse createProduct(ProductRequest request) {
		if (log.isDebugEnabled()) {
			log.debug("Creating product: {}", request);
		}

		// Check if product with same name already exists
		Optional<Product> existingProduct = productRepository.findByNameIgnoreCase(request.name());
		if (existingProduct.isPresent()) {
			log.warn("Product with name '{}' already exists", request.name());
			throw new IllegalArgumentException("Product with name '" + request.name() + "' already exists");
		}

		Product product = new Product(request.name(), request.description(), request.price(), request.category(),
				request.imageUrl(), request.available());

		Product savedProduct = productRepository.save(product);
		log.info("Created product with ID: {}", savedProduct.getId());
		return ProductResponse.fromEntity(savedProduct, currencyExchangeService);
	}

	/**
	 * Update an existing product.
	 * @param id product ID
	 * @param request product request
	 * @return updated product response
	 * @throws EntityNotFoundException if product not found
	 */
	@Transactional
	public ProductResponse updateProduct(Long id, ProductRequest request) {
		if (log.isDebugEnabled()) {
			log.debug("Updating product with ID: {}, request: {}", id, request);
		}

		Product product = productRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + id));

		// Check if another product with the same name exists
		Optional<Product> existingProduct = productRepository.findByNameIgnoreCase(request.name());
		if (existingProduct.isPresent() && !existingProduct.get().getId().equals(id)) {
			log.warn("Another product with name '{}' already exists", request.name());
			throw new IllegalArgumentException("Another product with name '" + request.name() + "' already exists");
		}

		product.setName(request.name());
		product.setDescription(request.description());
		product.setPrice(request.price());
		product.setCategory(request.category());
		product.setImageUrl(request.imageUrl());
		product.setAvailable(request.available());

		Product updatedProduct = productRepository.save(product);
		log.info("Updated product with ID: {}", updatedProduct.getId());
		return ProductResponse.fromEntity(updatedProduct, currencyExchangeService);
	}

	/**
	 * Delete a product by ID.
	 * @param id product ID
	 * @throws EntityNotFoundException if product not found
	 */
	@Transactional
	public void deleteProduct(Long id) {
		if (log.isDebugEnabled()) {
			log.debug("Deleting product with ID: {}", id);
		}

		if (!productRepository.existsById(id)) {
			log.warn("Product not found with ID: {}", id);
			throw new EntityNotFoundException("Product not found with ID: " + id);
		}

		productRepository.deleteById(id);
		log.info("Deleted product with ID: {}", id);
	}

}
