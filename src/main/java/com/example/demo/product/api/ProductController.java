package com.example.demo.product.api;

import com.example.demo.product.domain.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product Catalog", description = "Product catalog management API")
class ProductController {

	private static final Logger log = LoggerFactory.getLogger(ProductController.class);

	private static final String PRODUCT_NOT_FOUND_WITH_ID = "Product not found with ID: {}";

	private final ProductService productService;

	ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
	@Operation(summary = "Get all products", description = "Returns a paginated list of all products")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieved products",
			content = @Content(schema = @Schema(implementation = PageResponse.class))) })
	ResponseEntity<PageResponse<ProductResponse>> getAllProducts(
			@Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Page size") @RequestParam(defaultValue = "100") int size,
			@Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sort,
			@Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {

		log.debug("REST request to get all products, page: {}, size: {}", page, size);
		Pageable pageable = createPageable(page, size, sort, direction);
		Page<ProductResponse> productPage = productService.getAllProducts(pageable);
		return ResponseEntity.ok(PageResponse.from(productPage));
	}

	@GetMapping("/category/{category}")
	@Operation(summary = "Get products by category",
			description = "Returns a paginated list of products in the specified category")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieved products",
			content = @Content(schema = @Schema(implementation = PageResponse.class))) })
	ResponseEntity<PageResponse<ProductResponse>> getProductsByCategory(
			@Parameter(description = "Category name") @PathVariable String category,
			@Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Page size") @RequestParam(defaultValue = "100") int size,
			@Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sort,
			@Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {

		log.debug("REST request to get products by category: {}, page: {}, size: {}", category, page, size);
		Pageable pageable = createPageable(page, size, sort, direction);
		Page<ProductResponse> productPage = productService.getProductsByCategory(category, pageable);
		return ResponseEntity.ok(PageResponse.from(productPage));
	}

	@GetMapping("/search")
	@Operation(summary = "Search products by name",
			description = "Returns a paginated list of products with names containing the search term")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieved products",
			content = @Content(schema = @Schema(implementation = PageResponse.class))) })
	ResponseEntity<PageResponse<ProductResponse>> searchProductsByName(
			@Parameter(description = "Search term") @RequestParam String name,
			@Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Page size") @RequestParam(defaultValue = "100") int size,
			@Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sort,
			@Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {

		log.debug("REST request to search products by name: {}, page: {}, size: {}", name, page, size);
		Pageable pageable = createPageable(page, size, sort, direction);
		Page<ProductResponse> productPage = productService.getProductsByName(name, pageable);
		return ResponseEntity.ok(PageResponse.from(productPage));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get product by ID", description = "Returns a single product by its ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved product",
					content = @Content(schema = @Schema(implementation = ProductResponse.class))),
			@ApiResponse(responseCode = "404", description = "Product not found") })
	ResponseEntity<ProductResponse> getProductById(@Parameter(description = "Product ID") @PathVariable Long id) {

		log.debug("REST request to get product by ID: {}", id);
		try {
			ProductResponse product = productService.getProductById(id);
			return ResponseEntity.ok(product);
		}
		catch (EntityNotFoundException entityNotFoundException) {
			log.warn(PRODUCT_NOT_FOUND_WITH_ID, id);
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping
	@Operation(summary = "Create a new product", description = "Creates a new product and returns the created product")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Product created successfully",
					content = @Content(schema = @Schema(implementation = ProductResponse.class))),
			@ApiResponse(responseCode = "400",
					description = "Invalid input or product with same name already exists") })
	ResponseEntity<ProductResponse> createProduct(
			@Parameter(description = "Product to create") @Valid @RequestBody ProductRequest request) {

		log.debug("REST request to create product: {}", request);
		try {
			ProductResponse createdProduct = productService.createProduct(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
		}
		catch (IllegalArgumentException e) {
			log.warn("Failed to create product: {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update an existing product",
			description = "Updates an existing product and returns the updated product")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Product updated successfully",
					content = @Content(schema = @Schema(implementation = ProductResponse.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input or product with same name already exists"),
			@ApiResponse(responseCode = "404", description = "Product not found") })
	ResponseEntity<ProductResponse> updateProduct(@Parameter(description = "Product ID") @PathVariable Long id,
			@Parameter(description = "Updated product") @Valid @RequestBody ProductRequest request) {

		log.debug("REST request to update product with ID: {}, request: {}", id, request);
		try {
			ProductResponse updatedProduct = productService.updateProduct(id, request);
			return ResponseEntity.ok(updatedProduct);
		}
		catch (EntityNotFoundException entityNotFoundException) {
			log.warn(PRODUCT_NOT_FOUND_WITH_ID, id);
			return ResponseEntity.notFound().build();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			log.warn("Failed to update product: {}", illegalArgumentException.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a product", description = "Deletes a product by its ID")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Product not found") })
	ResponseEntity<Void> deleteProduct(@Parameter(description = "Product ID") @PathVariable Long id) {

		log.debug("REST request to delete product with ID: {}", id);
		try {
			productService.deleteProduct(id);
			return ResponseEntity.noContent().build();
		}
		catch (EntityNotFoundException entityNotFoundException) {
			log.warn(PRODUCT_NOT_FOUND_WITH_ID, id);
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Creates a Pageable object for pagination and sorting.
	 */
	private Pageable createPageable(int page, int size, String sort, String direction) {
		Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
		return PageRequest.of(page, size, Sort.by(sortDirection, sort));
	}

}