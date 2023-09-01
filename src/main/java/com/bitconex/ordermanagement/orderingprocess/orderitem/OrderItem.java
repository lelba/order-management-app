package com.bitconex.ordermanagement.orderingprocess.orderitem;

import com.bitconex.ordermanagement.administration.product.Product;
import com.bitconex.ordermanagement.orderingprocess.order.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "\"T_ORDER_ITEM\"")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderItem {

    @Id
    @SequenceGenerator(name = "product_seq", sequenceName = "product_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    @Column(
            updatable = false
    )
    private Long orderItem_id;

    @ManyToOne
    private Order order;

    @ManyToOne
    private Product product;
}
