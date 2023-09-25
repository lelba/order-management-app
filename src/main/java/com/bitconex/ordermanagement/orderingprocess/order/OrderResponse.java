package com.bitconex.ordermanagement.orderingprocess.order;

import com.bitconex.ordermanagement.administration.product.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private List<ProductDTO> orderedProducts;
    private double totalPrice;
}
