package com.bitconex.ordermanagement.administration.product;

import com.bitconex.ordermanagement.orderingprocess.order.Order;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name = "\"T_PRODUCT\"")
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
    private Double price;
    @Column(
            name = "VALID_FROM",
            nullable = false
    )
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date validFrom;
    @Column(
            name = "VALID_TO",
            nullable = false
    )
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date validTo;
    @Column(
            name = "QUANTITY",
            nullable = false
    )
    private int quantity;

    @ManyToOne
    private Order order;

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
