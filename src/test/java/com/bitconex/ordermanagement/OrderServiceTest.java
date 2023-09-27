package com.bitconex.ordermanagement;

import com.bitconex.ordermanagement.administration.product.Product;
import com.bitconex.ordermanagement.administration.product.ProductService;
import com.bitconex.ordermanagement.administration.user.*;
import com.bitconex.ordermanagement.orderingprocess.order.Order;
import com.bitconex.ordermanagement.orderingprocess.order.OrderDTO;
import com.bitconex.ordermanagement.orderingprocess.order.OrderRepository;
import com.bitconex.ordermanagement.orderingprocess.order.OrderService;
import com.bitconex.ordermanagement.orderingprocess.orderitem.OrderItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @InjectMocks
    private OrderService cut;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ProductService productService;
    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetOrders() {
        Order order = new Order();
        order.setId(1L);
        List<Order> orders = new ArrayList<>();
        orders.add(order);

        when(orderRepository.findAll()).thenReturn(orders);
        List<OrderDTO> result = cut.getOrders();

        assertEquals(1, result.size());
    }

    @Test
    public void testGetOrders_NoOrders() {
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());
        assertThrows(IllegalArgumentException.class, () -> {
           cut.getOrders();
       });
    }

    @Test
    public void testCreateOrder() {
        User user = createUser();
        Product product = createProduct();
        List<Product> products = new ArrayList<>();
        products.add(product);
        Order expectedOrder = new Order();
        expectedOrder.setUser(user);
        expectedOrder.setRegisterDate(LocalDateTime.now());
        expectedOrder.setTotalPrice(100.0);
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(expectedOrder);
        orderItem.setProduct(product);
        orderItems.add(orderItem);
        expectedOrder.setOrderItems(orderItems);
        product.setQuantity(4);

        doNothing().when(productService).updateProductQuantityAndActive(product);

        Order orderResult = cut.createOrder(user, products);

        verify(productService, times(products.size())).updateProductQuantityAndActive(product);
        assertEquals(expectedOrder.getTotalPrice(), orderResult.getTotalPrice());
        assertEquals(expectedOrder.getUser(), orderResult.getUser());
    }
    @Test
    public void testCreateAndSaveOrder() {
        User user = createUser();
        List<Product> products = Arrays.asList(createProduct(), createProduct(), createProduct());

        Order expectedOrder = new Order();
        expectedOrder.setUser(user);
        expectedOrder.setRegisterDate(LocalDateTime.now());
        expectedOrder.setTotalPrice(300.0);
        List<OrderItem> orderItems = new ArrayList<>();
        for (Product product : products) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(expectedOrder);
            orderItem.setProduct(product);
            orderItems.add(orderItem);
        }
        expectedOrder.setOrderItems(orderItems);

        for (Product product : products) {
            doNothing().when(productService).updateProductQuantityAndActive(product);
        }

        Order orderResult = cut.createAndSaveOrder(user, products);

        verify(orderRepository, times(1)).save(any());
        assertEquals(expectedOrder.getTotalPrice(), orderResult.getTotalPrice());
        assertEquals(expectedOrder.getUser(), orderResult.getUser());
    }

    @Test
    public void testPrintNewOrder() {
        User user = createUser();
        Product product = createProduct();
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItems.add(orderItem);

        Order order = new Order();
        order.setOrderItems(orderItems);

        //sistemski izlaz na privremeni bafer
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        cut.printNewOrder(order);

        // Reset sistemski izlaz
        System.setOut(System.out);

        String expectedOutput = "-------------------------------\r\n" +
                "Product Name       | Price    |\r\n" +
                "-------------------------------\r\n" +
                "Name               | 100.0    |\r\n" +
                "--------------------------------\r\n" +
                "Total Price:       || 100.00 || \r\n" +
                "--------------------------------";
        assertEquals(expectedOutput.trim(), outputStream.toString().trim());
    }

    @Test
    public void testPrintNewOrder_NoOrderItems() {
        Order order = new Order();
        assertThrows(IllegalArgumentException.class, () -> {
            cut.printNewOrder(order);
        });
    }

    @Test
    public void testPrintAllOrdersForCustomer_WithOrders() throws Exception {
        User user = createUser();
        List<Order> orders = new ArrayList<>();
        Order order = new Order();
        order.setUser(user);
        orders.add(order);

        when(orderRepository.findOrdersByUser(user)).thenReturn(orders);

        cut.printAllOrdersForCustomer(user);

        verify(orderRepository, times(1)).findOrdersByUser(user);
        verify(objectMapper, times(1)).writeValueAsString(anyList());
    }

    @Test
    public void testPrintAllOrdersForCustomer_WithNoOrders() throws Exception {
        User user = createUser();
        when(orderRepository.findOrdersByUser(user)).thenReturn(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () -> {
            cut.printAllOrdersForCustomer(user);
        });

        verify(orderRepository, times(1)).findOrdersByUser(user);
        verify(objectMapper, times(0)).writeValueAsString(anyList());
    }

    private User createUser() {
        User user = new User();
        user.setUserName("customer");
        user.setPassword("password");
        user.setEmail("customer@bitconex.de");
        user.setActive(true);
        user.setRole(UserRole.CUSTOMER);
        user.setName("Cust");
        user.setSurname("Omer");
        user.setId(1L);
        user.setDateOfBirth(new Date(1999, 10, 10));
        Address address = new Address();
        address.setAddressId(1L);
        address.setUser(user);
        address.setStreet("Street");
        address.setCountry("Country");
        address.setHouseNumber(1234L);
        address.setPlace("Place");
        return user;
    }

    private Product createProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Name");
        product.setQuantity(5);
        product.setActive(true);
        product.setPrice(100.0);
        product.setValidFrom(new Date());
        product.setValidTo(new Date(125, 10, 10));
        return product;
    }

}
