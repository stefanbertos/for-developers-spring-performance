package com.example.demo.product.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void productApiEndToEndTest() throws Exception {
        // Create a product
        ProductRequest createRequest = new ProductRequest(
                "Integration Test Product",
                "This is a product created during integration testing",
                new BigDecimal("123.45"),
                "Test Category",
                "https://example.com/integration-test.jpg",
                true
        );

        MvcResult createResult = mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(createRequest.name()))
                .andExpect(jsonPath("$.description").value(createRequest.description()))
                .andExpect(jsonPath("$.price").value(createRequest.price().doubleValue()))
                .andExpect(jsonPath("$.category").value(createRequest.category()))
                .andExpect(jsonPath("$.imageUrl").value(createRequest.imageUrl()))
                .andExpect(jsonPath("$.available").value(createRequest.available()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty())
                .andReturn();

        // Extract the created product ID
        String createResponseJson = createResult.getResponse().getContentAsString();
        ProductResponse createdProduct = objectMapper.readValue(createResponseJson, ProductResponse.class);
        Long productId = createdProduct.id();

        // Get the product by ID
        mockMvc.perform(get("/api/v1/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value(createRequest.name()));

        // Get all products and verify our product is in the list
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[*].id", hasItem(productId.intValue())));

        // Search for products by name
        mockMvc.perform(get("/api/v1/products/search")
                .param("name", "Integration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[*].id", hasItem(productId.intValue())));

        // Get products by category
        mockMvc.perform(get("/api/v1/products/category/{category}", "Test Category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[*].id", hasItem(productId.intValue())));

        // Update the product
        ProductRequest updateRequest = new ProductRequest(
                "Updated Integration Test Product",
                "This product has been updated during integration testing",
                new BigDecimal("199.99"),
                "Updated Test Category",
                "https://example.com/updated-integration-test.jpg",
                false
        );

        mockMvc.perform(put("/api/v1/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value(updateRequest.name()))
                .andExpect(jsonPath("$.description").value(updateRequest.description()))
                .andExpect(jsonPath("$.price").value(updateRequest.price().doubleValue()))
                .andExpect(jsonPath("$.category").value(updateRequest.category()))
                .andExpect(jsonPath("$.imageUrl").value(updateRequest.imageUrl()))
                .andExpect(jsonPath("$.available").value(updateRequest.available()));

        // Delete the product
        mockMvc.perform(delete("/api/v1/products/{id}", productId))
                .andExpect(status().isNoContent());

        // Verify the product is deleted
        mockMvc.perform(get("/api/v1/products/{id}", productId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProduct_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Create a product with invalid data (missing required fields)
        ProductRequest invalidRequest = new ProductRequest(
                "", // Empty name (invalid)
                "Sh", // Description too short (invalid)
                BigDecimal.ZERO, // Zero price (invalid)
                null, // Null category (will be set to default)
                null, // Null imageUrl (will be set to default)
                null  // Null available (will be set to default)
        );

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Validation failed"))
                .andExpect(jsonPath("$.errors.name").isNotEmpty())
                .andExpect(jsonPath("$.errors.description").isNotEmpty())
                .andExpect(jsonPath("$.errors.price").isNotEmpty());
    }

    @Test
    void getProduct_WithInvalidId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/products/{id}", 999999))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProduct_WithDuplicateName_ShouldReturnBadRequest() throws Exception {
        // Create a product
        ProductRequest createRequest = new ProductRequest(
                "Duplicate Name Test Product",
                "This is a product created to test duplicate name validation",
                new BigDecimal("123.45"),
                "Test Category",
                "https://example.com/duplicate-test.jpg",
                true
        );

        // Create the product first time
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        // Try to create another product with the same name
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }
}
