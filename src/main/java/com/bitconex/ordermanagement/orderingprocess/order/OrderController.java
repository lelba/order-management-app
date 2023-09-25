package com.bitconex.ordermanagement.orderingprocess.order;

import com.bitconex.ordermanagement.administration.product.Product;
import com.bitconex.ordermanagement.administration.product.ProductDTO;
import com.bitconex.ordermanagement.administration.product.ProductRepository;
import com.bitconex.ordermanagement.administration.product.ProductService;
import com.bitconex.ordermanagement.administration.user.User;
import com.bitconex.ordermanagement.administration.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/orders")

public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    @Autowired
    public OrderController(OrderService orderService, UserRepository userRepository, ProductRepository productRepository, ProductService productService) {
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.productService = productService;
    }

    @GetMapping()
    public ResponseEntity<List<OrderDTO>> getOrders() {
        return ResponseEntity.ok(orderService.getOrders());
    }

    @PostMapping()
    public void addNewOrder(@RequestBody User user) {
        orderService.addNewOrder(user);
    }

    @PostMapping("/add")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody List<Long> productIds, Principal principal) {
        User user = userRepository.findUserByUserName(principal.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        List<Product> products = productRepository.findAllById(productIds);

        if (products.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        Order order = orderService.createOrder(user, products);

        if (order == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        List<ProductDTO> dto = new ArrayList<>();
        for(Product product : products) {
            ProductDTO productDTO = productService.convertToProductDTO(product);
            dto.add(productDTO);
        }
        OrderResponse orderResponse = new OrderResponse(dto, order.getTotalPrice());
        return ResponseEntity.ok(orderResponse);
    }

}
