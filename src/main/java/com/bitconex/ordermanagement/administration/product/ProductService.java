package com.bitconex.ordermanagement.administration.product;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;


@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public void addNewProduct(Product newProduct) {
        Product product = productRepository.findProductByName(newProduct.getName()).orElse(null);
        if (product != null) {
            throw new IllegalStateException("Product already existing!");
            } else
                productRepository.save(newProduct);
    }

    public void deleteProductByName(String name) {
        Product product = productRepository.findProductByName(name).orElse(null);
        if(product == null) {
            throw new IllegalStateException("There is no product with that name!");
        } else productRepository.delete(product);
    }



    public void printAllProducts() {
        List<Product> products = productRepository.findAll();
        System.out.println("----------------------------------------------------------------------");
        System.out.println("ID    | Name       | Price    | Valid from   | Valid to     | Quantity");
        System.out.println("----------------------------------------------------------------------");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (Product product : products) {
            System.out.printf("%-5s | %-10s | %-8s | %-12s | %-12s | %s%n",
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    dateFormat.format(product.getValidFrom()),
                    dateFormat.format(product.getValidTo()),
                    product.getQuantity());
        }
        System.out.println("----------------------------------------------------------------------");
    }


    @Transactional
    public void deleteProductsOutOfStock() {
        productRepository.deleteProductsOutOfStock();
    }

    public void deleteProductsNoLongerAvailable() {
        productRepository.deleteExpiredProducts();
    }
}


