package com.bitconex.ordermanagement.orderingprocess.orderitem;

import com.bitconex.ordermanagement.administration.product.Product;
import com.bitconex.ordermanagement.orderingprocess.order.Order;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "\"T_ORDER_ITEM\"")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class OrderItem {

    @Id
    @SequenceGenerator(name = "order_item_seq", sequenceName = "order_item_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_item_seq")
    @Column(
            updatable = false
    )
    private Long orderItem_id;

    @ManyToOne
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_ID")
    private Product product;
}
