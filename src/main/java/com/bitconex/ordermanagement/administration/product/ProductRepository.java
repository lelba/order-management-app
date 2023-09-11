package com.bitconex.ordermanagement.administration.product;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.name =?1")
    Optional<Product> findProductByName(String name);

    @Modifying
    @Query("DELETE FROM Product p WHERE p.quantity = 0")
    void deleteProductsOutOfStock();

    @Query("SELECT p FROM Product p WHERE p.validTo < CURRENT_DATE")
    List<Product> findExpiredProducts();

    @Modifying
    @Transactional
    @Query("DELETE FROM Product p WHERE p.validTo < CURRENT_DATE")
    void deleteExpiredProducts();

   @Query("SELECT p FROM Product p WHERE p.active = true")
    List<Product> findProductByActiveTrue();

    List<Product> findAllByActiveIsTrueAndValidToIsAfter(Date localDate);
}
