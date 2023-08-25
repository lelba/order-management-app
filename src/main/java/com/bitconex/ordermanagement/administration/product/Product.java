package com.bitconex.ordermanagement.administration.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "T_PRODUCT")
public class Product {

    @Id
    @SequenceGenerator(name = "product_seq", sequenceName = "product_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    @Column(
            name = "ID",
            updatable = false
    )
    private Long id;
    @Column(
            name = "NAME",
            nullable = false
    )
    private String name;
    @Column(
            name = "PRICE",
            nullable = false
    )
    private Long price;
    @Column(
            name = "VALID_FROM",
            nullable = false
    )
    private Date validFrom;
    @Column(
            name = "VALID_TO",
            nullable = false
    )
    private Date validTo;
    @Column(
            name = "QUANTITY",
            nullable = false
    )
    private int quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return getQuantity() == product.getQuantity() && Objects.equals(getName(), product.getName()) && Objects.equals(getPrice(), product.getPrice()) && Objects.equals(getValidFrom(), product.getValidFrom()) && Objects.equals(getValidTo(), product.getValidTo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getPrice(), getValidFrom(), getValidTo(), getQuantity());
    }
}
