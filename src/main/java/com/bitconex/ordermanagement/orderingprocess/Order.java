package com.bitconex.ordermanagement.orderingprocess;

import com.bitconex.ordermanagement.administration.product.Product;
import com.bitconex.ordermanagement.administration.user.User;
import com.bitconex.ordermanagement.orderingprocess.orderitem.OrderItem;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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


    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToOne
    private User user;

    private LocalDateTime registerDate;

    private Double totalPrice;


}
