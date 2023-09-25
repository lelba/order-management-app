package com.bitconex.ordermanagement.administration.product;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping()
    public ResponseEntity<List<ProductDTO>> getProducts(){
        return ResponseEntity.ok(productService.getProducts());
    }

    @PostMapping("/add")
    public void addNewProduct(@RequestBody Product product) {
        productService.addNewProduct(product);
    }

    @Transactional
    @DeleteMapping("/{name}")
    public void deleteProduct(@PathVariable String name) {
        productService.deleteProductByName_setNotActive(name);
    }
}
