package com.bitconex.ordermanagement.administration.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getProducts(){
        return productService.getProducts();
    }

    @PostMapping
    public void addNewProduct(Product product) {
        productService.addNewProduct(product);
    }

    @DeleteMapping
    public void deleteProduct(String name) {
        productService.deleteProductByName(name);
    }
}
