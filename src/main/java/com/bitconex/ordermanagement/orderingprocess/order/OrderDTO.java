package com.bitconex.ordermanagement.orderingprocess.order;

import com.bitconex.ordermanagement.administration.user.CustomerDTO;
import com.bitconex.ordermanagement.orderingprocess.orderitem.OrderItemDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderDTO {

    private Long id;
    private List<OrderItemDTO> orderItemDTOList;
    private CustomerDTO user;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime registerDate;
    private Double totalPrice;
}
