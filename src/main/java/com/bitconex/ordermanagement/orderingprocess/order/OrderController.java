package com.bitconex.ordermanagement.orderingprocess.order;

import com.bitconex.ordermanagement.administration.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderExportService orderExportService;

    @Autowired
    public OrderController(OrderService orderService, OrderExportService orderExportService) {
        this.orderService = orderService;
        this.orderExportService = orderExportService;
    }

    @GetMapping("/getOrders")
    public List<OrderDTO> getOrders() {
        return orderService.getOrders();
    }

    @PostMapping("/addNewOrder")
    public void addNewOrder(@RequestBody User user) {
        orderService.addNewOrder(user);
    }

}
