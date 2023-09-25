package com.bitconex.ordermanagement.administration.product;

import com.bitconex.ordermanagement.orderingprocess.orderitem.OrderItem;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
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
    @Column(
            name = "ACTIVE",
            columnDefinition = "boolean default true"
    )
    private boolean active;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> orderItems = new ArrayList<>();
}
