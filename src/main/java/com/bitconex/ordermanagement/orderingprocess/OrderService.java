package com.bitconex.ordermanagement.orderingprocess;

import com.bitconex.ordermanagement.administration.product.Product;
import com.bitconex.ordermanagement.administration.product.ProductService;
import com.bitconex.ordermanagement.administration.user.User;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
    }

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    public void addNewOrder(User user) {

        Order order = createAndAddProductsToOrder(user);
        orderRepository.save(order);
    }

    public void deleteOrder(String userName) {
    }

    public Order createAndAddProductsToOrder(User user) {
        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setRegisterDate(LocalDateTime.now());
        newOrder.setTotalPrice(0.0);

        Scanner scanner = new Scanner(System.in);

        List<Product> availableProducts = productService.getProducts();
        for (Product product : availableProducts) {
            System.out.printf("Add product '%s' to the order? (y/n): ", product.getName());
            String choice = scanner.nextLine();
            if ("y".equalsIgnoreCase(choice)) {
                newOrder.getProducts().add(product);
                newOrder.setTotalPrice(newOrder.getTotalPrice() + product.getPrice()); // Ažuriranje ukupne cijene
            }
        }
        return newOrder;
    }

    public void exportOrdersToCsv(String directoryPath) {
        String fileName = "orders.csv";
        String filePath = Paths.get(directoryPath, fileName).toString();
        List<Order> orders = orderRepository.findAll();

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("Order ID,User ID,Register Date,Total Price");

            for (Order order : orders) {
                writer.printf("%d,%d,%s,%.2f%n",
                        order.getId(),
                        order.getUser().getId(),
                        order.getRegisterDate(),
                        order.getTotalPrice());
            }

            System.out.println("Orders exported to CSV: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
