package com.bitconex.ordermanagement.orderingprocess.order;

import com.bitconex.ordermanagement.administration.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderExportService orderExportService;

    @Autowired
    public OrderController(OrderService orderService, OrderExportService orderExportService) {
        this.orderService = orderService;
        this.orderExportService = orderExportService;
    }

    @GetMapping
    public List<Order> getOrders() {
        return orderService.getOrders();
    }

    @PostMapping
    public void addNewOrder(User user) {
        orderService.addNewOrder(user);
    }

    @DeleteMapping
    public void deleteOrder(String userName) {
        orderService.deleteOrder(userName);
    }
}
