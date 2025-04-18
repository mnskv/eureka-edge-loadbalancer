package com.demo.products.controller;

import com.demo.products.dto.ProductDTO;
import com.demo.products.dto.ProductsContactInfoDto;
import com.demo.products.entity.Product;
import com.demo.products.service.client.ProductService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final String buildVersion;
    private final ProductsContactInfoDto productsContactInfoDto;

    @Autowired
    public ProductController(ProductService productService, @Value("${build.version}") String buildVersion, ProductsContactInfoDto productsContactInfoDto) {
        this.productService = productService;
        this.buildVersion = buildVersion;
        this.productsContactInfoDto = productsContactInfoDto;
    }


    @PostMapping("/create")
    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackCreateOrUpdateProduct")
    public ResponseEntity<Product> createOrUpdateProduct(@RequestBody Product product) {
        log.info("Received request to create or update product: {}", product);
        Product savedProduct = productService.saveOrUpdateProduct(product);
        log.info("Product saved or updated successfully: {}", savedProduct);
        return ResponseEntity.ok(savedProduct);
    }

    @GetMapping("/get/{id}")
    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackGetProductById")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        log.info("Received request to get product by id: {}", id);
        Product product = productService.getProductById(id);
        if (product == null) {
            log.warn("Product with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Product retrieved successfully: {}", product);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/get-products")
    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackGetAllProducts")
    public ResponseEntity<List<Product>> getAllProducts() {
        log.info("Received request to get all products");
        List<Product> products = productService.getAllProducts();
        log.info("Retrieved {} products", products.size());
        return ResponseEntity.ok(products);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable String id) {
        log.info("Received request to delete product by id: {}", id);
        productService.deleteProductById(id);
        log.info("Product with id {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> getProductWithReviews(@PathVariable String productId) {
        log.info("Received request to get product with reviews for productId: {}", productId);
        ProductDTO response = productService.getProductWithReviews(productId);
        if (response == null) {
            log.warn("No product found with reviews for productId: {}", productId);
            return ResponseEntity.notFound().build();
        }
        log.info("Product with reviews retrieved successfully for productId: {}", productId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/contact-info")
    public ResponseEntity<ProductsContactInfoDto> getContactInfo() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productsContactInfoDto);
    }

    @GetMapping("/build-info")
    public ResponseEntity<String> getBuildInfo(@RequestHeader("test-correlation-id") String corelationId) {
        log.info("corelation-id - {}",corelationId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(buildVersion);
    }
    public ResponseEntity<Product> fallbackCreateOrUpdateProduct(Product product, Throwable throwable) {
        log.error("Error saving or updating product: {}", product, throwable);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Product("p12","Fallback product", 0.0)); // Returning an empty Product or a custom error response
    }


    public ResponseEntity<Product> fallbackGetProductById(String id, Throwable throwable) {
        log.error("Fallback for getProductById due to error: {}", throwable.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

    public ResponseEntity<List<Product>> fallbackGetAllProducts(Throwable throwable) {
        log.error("Error occurred while fetching products", throwable);

        // Return a detailed error message in the response body
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonList(new Product("p12","Fallback product", 0.0)));
    }



}
