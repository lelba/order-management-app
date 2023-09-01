package com.bitconex.ordermanagement.orderingprocess.order;

import com.bitconex.ordermanagement.administration.product.Product;
import com.bitconex.ordermanagement.administration.product.ProductRepository;
import com.bitconex.ordermanagement.administration.product.ProductService;
import com.bitconex.ordermanagement.administration.user.User;
import com.bitconex.ordermanagement.orderingprocess.order.Order;
import com.bitconex.ordermanagement.orderingprocess.order.OrderDTO;
import com.bitconex.ordermanagement.orderingprocess.order.OrderRepository;
import com.bitconex.ordermanagement.orderingprocess.orderitem.OrderItem;
import com.bitconex.ordermanagement.orderingprocess.orderitem.OrderItemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;


    public OrderService(OrderRepository orderRepository, ProductService productService, ProductRepository productRepository, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.productRepository = productRepository;
        this.objectMapper = objectMapper;
    }

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public void addNewOrder(User user) {
        Order order = createAndAddProductsToOrder(user);
        if (confirmOrder()) {
            orderRepository.save(order);
            printNewOrder(order);
            System.out.println("Order confirmed and saved!");
        } else {
            System.out.println("Order cancelled!");
        }
    }

    public boolean confirmOrder() {
        System.out.println("Do you want to confirm the order? (y/n): ");
        Scanner choice = new Scanner(System.in);
        return choice.next().equals("y");
    }

    private void printNewOrder(Order order) {

        List<OrderItem> orderItems = order.getOrderItems();
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
        System.out.printf("Total Price:       || %.2f  || %n", totalPrice);
        System.out.println("--------------------------------");
    }

    public void deleteOrder(String userName) {
    }

    @Transactional
    public Order createAndAddProductsToOrder(User user) {
        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setRegisterDate(LocalDateTime.now());
        newOrder.setTotalPrice(0.0);

        Scanner scanner = new Scanner(System.in);
        List<Product> availableProducts = productService.getProducts();
        List<OrderItem> orderItems = new ArrayList<>();
        for (Product product : availableProducts) {
            System.out.printf("Add product '%s' to the order? (y/n): ", product.getName());
            String choice = scanner.nextLine();
            if ("y".equalsIgnoreCase(choice)) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(newOrder);
                orderItem.setProduct(product);

                orderItems.add(orderItem);
                newOrder.setTotalPrice(newOrder.getTotalPrice() + product.getPrice()); // Ažuriranje ukupne cijene
                product.setQuantity(product.getQuantity() - 1);
                productRepository.save(product);
            }
        }
        newOrder.setOrderItems(orderItems);
        return newOrder;
    }


    public void printOrdersInCSVFormat() {

    }

    public void printAllOrdersForCustomer(User user) {
        List<Order> orders = orderRepository.findOrdersByUser(user);
        List<OrderDTO> orderDTOs = convertToOrderDTOList(orders);
        try {
            String jsonOrders = objectMapper.writeValueAsString(orderDTOs);
            System.out.println(jsonOrders);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<OrderDTO> convertToOrderDTOList(List<Order> orders) {
        return orders.stream()
                .map(this::convertToOrderDTO)
                .collect(Collectors.toList());
    }

    private OrderDTO convertToOrderDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
    //    orderDTO.setUser(order.getUser());
        orderDTO.setRegisterDate(order.getRegisterDate());
        orderDTO.setTotalPrice(order.getTotalPrice());
        orderDTO.setOrderItemDTOList(convertToOrderItemDTOList(order.getOrderItems()));
        return orderDTO;
    }

    private List<OrderItemDTO> convertToOrderItemDTOList(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(this::convertToOrderItemDTO)
                .collect(Collectors.toList());
    }

    private OrderItemDTO convertToOrderItemDTO(OrderItem orderItem) {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setId(orderItem.getOrderItem_id());
        orderItemDTO.setProduct(orderItem.getProduct());
   //     orderItemDTO.setOrder(orderItem.getOrder());
        return orderItemDTO;
    }
}
