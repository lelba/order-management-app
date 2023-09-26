package com.bitconex.ordermanagement.orderingprocess.order;

import com.bitconex.ordermanagement.administration.product.Product;
import com.bitconex.ordermanagement.administration.product.ProductDTO;
import com.bitconex.ordermanagement.administration.product.ProductRepository;
import com.bitconex.ordermanagement.administration.product.ProductService;
import com.bitconex.ordermanagement.administration.user.User;
import com.bitconex.ordermanagement.administration.user.UserRole;
import com.bitconex.ordermanagement.administration.user.UserService;
import com.bitconex.ordermanagement.orderingprocess.orderitem.OrderItem;
import com.bitconex.ordermanagement.orderingprocess.orderitem.OrderItemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public OrderService(OrderRepository orderRepository, ProductService productService, UserService userService, ProductRepository productRepository, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.userService = userService;
        this.productRepository = productRepository;
        this.objectMapper = objectMapper;
    }

    public List<OrderDTO> getOrders() {
        List<Order> orders = orderRepository.findAll();
        List<OrderDTO> orderDTOS = convertToOrderDTOList(orders);
        return orderDTOS;
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
        System.out.printf("Total Price:       || %.2f || %n", totalPrice);
        System.out.println("--------------------------------");
    }

    @Transactional
    public Order createAndAddProductsToOrder(User user) {
        Order newOrder = createNewOrder(user);

        Scanner scanner = new Scanner(System.in);
        List<Product> availableProducts = productRepository.findAllByActiveIsTrueAndValidToIsAfter(new Date());
        List<OrderItem> orderItems = new ArrayList<>();
        double totalPrice = 0.0;
        for (Product product : availableProducts) {
            System.out.printf("Add product '%s' to the order? (y/n): ", product.getName());
            System.out.printf("Price: '%s': ", product.getPrice());
            String choice = scanner.next();
            if ("y".equalsIgnoreCase(choice)) {
                OrderItem orderItem =createOrderItem(newOrder, product);
                orderItems.add(orderItem);
                totalPrice += product.getPrice();
                productService.updateProductQuantityAndActive(product);
            }
        }
        newOrder.setTotalPrice(totalPrice);
        newOrder.setOrderItems(orderItems);
        return newOrder;
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

                    // Konvertujte proizvod u ProductDTO i postavite ga u OrderItemDTO
                    ProductDTO productDTO = productService.convertToProductDTO(orderItem.getProduct());
                    orderItemDTO.setProduct(productDTO);

                    return orderItemDTO;
                })
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersDTO() {
        List<Order> orders = orderRepository.findAll();
        List<OrderDTO> orderDTOs = new ArrayList<>();

        for (Order order : orders) {
            OrderDTO orderDTO = convertToOrderDTO(order);
            orderDTOs.add(orderDTO);
        }

        return orderDTOs;
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
        orderRepository.save(newOrder);

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

}
