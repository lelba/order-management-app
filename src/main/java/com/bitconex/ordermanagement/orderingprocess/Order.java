package com.bitconex.ordermanagement.orderingprocess;

import com.bitconex.ordermanagement.administration.product.Product;
import com.bitconex.ordermanagement.administration.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "T_ORDER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @SequenceGenerator(name = "order_seq", sequenceName = "order_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    @Column(
            name = "ID",
            updatable = false
    )
    private Long id;

    @OneToMany(mappedBy = "order")
    private List<Product> products = new ArrayList<>();

    @ManyToOne
    private User user;

    private LocalDateTime registerDate;

    private Double totalPrice;


}
