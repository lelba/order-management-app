package com.bitconex.ordermanagement.orderingprocess.order;

import com.bitconex.ordermanagement.administration.product.Product;
import com.bitconex.ordermanagement.administration.product.ProductDTO;
import com.bitconex.ordermanagement.administration.product.ProductService;
import com.bitconex.ordermanagement.administration.user.User;
import com.bitconex.ordermanagement.administration.user.UserService;
import com.bitconex.ordermanagement.orderingprocess.orderitem.OrderItem;
import com.bitconex.ordermanagement.orderingprocess.orderitem.OrderItemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final UserService userService;

    private final ObjectMapper objectMapper;

    @Autowired
    public OrderService(OrderRepository orderRepository, ProductService productService, UserService userService, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    public List<OrderDTO> getOrders() {
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            throw new IllegalArgumentException("There is no orders!");
        }
        List<OrderDTO> orderDTOS = convertToOrderDTOList(orders);
        return orderDTOS;
    }

    public void printNewOrder(Order order) {

        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems.isEmpty()) {
            throw new IllegalArgumentException("There is no order items!");
        }
        System.out.println("-------------------------------");
        System.out.println("Product Name       | Price    |");
        System.out.println("-------------------------------");

        double totalPrice = 0.0;
        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            System.out.printf("%-18s | %-8s |%n",
                    product.getName(),
                    product.getPrice());
            totalPrice += product.getPrice();
        }
        System.out.println("--------------------------------");
        System.out.printf("Total Price:       || %.2f || %n", totalPrice);
        System.out.println("--------------------------------");
    }

    public void printAllOrdersForCustomer(User user) {
        List<Order> orders = orderRepository.findOrdersByUser(user);
        if (orders.isEmpty()) {
            throw new IllegalArgumentException("There is no orders to print!");
        }
        List<OrderDTO> orderDTOs = convertToOrderDTOList(orders);
        try {
            String jsonOrders = objectMapper.writeValueAsString(orderDTOs);
            System.out.println(jsonOrders);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Order createAndSaveOrder(User user, List<Product> products) {
        Order order = createOrder(user, products);
        orderRepository.save(order);
        return order;
    }

    @Transactional
    public Order createOrder(User user, List<Product> products) {
        Order newOrder = createNewOrder(user);
        List<OrderItem> orderItems = new ArrayList<>();
        double totalPrice = 0.0;
        for (Product product : products) {
            OrderItem orderItem = createOrderItem(newOrder, product);
            orderItems.add(orderItem);
            totalPrice += product.getPrice();
            productService.updateProductQuantityAndActive(product);
        }
        newOrder.setTotalPrice(totalPrice);
        newOrder.setOrderItems(orderItems);
     //   orderRepository.save(newOrder);
        return newOrder;
    }

    private Order createNewOrder(User user) {
        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setRegisterDate(LocalDateTime.now());
        newOrder.setTotalPrice(0.0);
        return newOrder;
    }

    private OrderItem createOrderItem(Order order, Product product) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        return orderItem;
    }

    private List<OrderDTO> convertToOrderDTOList(List<Order> orders) {
        return orders.stream()
                .map(this::convertToOrderDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO convertToOrderDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setRegisterDate(order.getRegisterDate());
        orderDTO.setTotalPrice(order.getTotalPrice());
        orderDTO.setOrderItemDTOList(convertToOrderItemDTOList(order.getOrderItems()));
        orderDTO.setUser(userService.convertToCustomerDTO(order.getUser()));
        return orderDTO;
    }


    private List<OrderItemDTO> convertToOrderItemDTOList(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem -> {
                    OrderItemDTO orderItemDTO = new OrderItemDTO();
                    orderItemDTO.setId(orderItem.getOrderItem_id());

                    // Konvertujem proizvod u ProductDTO i postavite ga u OrderItemDTO
                    ProductDTO productDTO = productService.convertToProductDTO(orderItem.getProduct());
                    orderItemDTO.setProduct(productDTO);

                    return orderItemDTO;
                })
                .collect(Collectors.toList());
    }
}
