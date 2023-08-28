package com.bitconex.ordermanagement.orderingprocess;

import com.bitconex.ordermanagement.administration.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
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
    @GetMapping("/export-csv")
    public ResponseEntity<String> exportOrdersToCsv(@RequestParam String filePath) {
        orderService.exportOrdersToCsv(filePath);
        return ResponseEntity.ok("Orders exported to CSV.");
    }
}
