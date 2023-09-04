package com.bitconex.ordermanagement.orderingprocess.orderitem;

import com.bitconex.ordermanagement.administration.product.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDTO {

    private Long id;
    private Product product;
}
