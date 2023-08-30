package com.bitconex.ordermanagement.orderingprocess;

import com.bitconex.ordermanagement.administration.product.Product;
import com.bitconex.ordermanagement.administration.product.ProductRepository;
import com.bitconex.ordermanagement.administration.product.ProductService;
import com.bitconex.ordermanagement.administration.user.User;
import com.bitconex.ordermanagement.orderingprocess.orderitem.OrderItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;


    public OrderService(OrderRepository orderRepository, ProductService productService, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.productRepository = productRepository;

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


    public void printOrdersInJsonFormat() {

    }

    public void printAllOrdersForCustomer(User user) {

    }
}
