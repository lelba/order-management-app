package com.bitconex.ordermanagement;

import com.bitconex.ordermanagement.administration.product.*;
import com.bitconex.ordermanagement.orderingprocess.order.Order;
import com.bitconex.ordermanagement.orderingprocess.order.OrderRepository;
import com.bitconex.ordermanagement.orderingprocess.orderitem.OrderItem;
import com.bitconex.ordermanagement.orderingprocess.orderitem.OrderItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private ProductService cut;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    public void testGetProductsWithValidData() {

        Date currentDate = new Date();
        List<Product> products = new ArrayList<>();
        Product product1 = new ProductBuilder()
                .withId(1L)
                .withName("Product1")
                .withPrice(100.0)
                .withValidFrom(currentDate)
                .withValidTo(new Date(125, 12, 12))
                .withQuantity(5)
                .isActive(true).build();
        products.add(product1);
        Product product2 = new ProductBuilder()
                .withId(2L)
                .withName("Product2")
                .withPrice(100.0)
                .withValidFrom(currentDate)
                .withValidTo(new Date(125, 12, 12))
                .withQuantity(5)
                .isActive(true).build();
        products.add(product2);

        when(productRepository.findAllByActiveIsTrueAndValidToIsAfter(any())).thenReturn(products);

        List<ProductDTO> productsDTO = cut.getProducts();

        assertEquals(2, productsDTO.size());
        assertEquals("Product1", productsDTO.get(0).getName());
        assertEquals("Product2", productsDTO.get(1).getName());

    }

    @Test
    public void testGetProductsWhenNoAvailableProductsExist() {
        Date currentDate = new Date();
        List<Product> products = new ArrayList<>();

        when(productRepository.findAllByActiveIsTrueAndValidToIsAfter(currentDate)).thenReturn(products);

        assertThrows(IllegalArgumentException.class, () -> {
            cut.getProducts();
        });
    }

    @Test
    public void testPrintAllProducts() {

        List<Product> products = new ArrayList<>();
        Date currentDate = new Date();

        Product product1 = new ProductBuilder()
                .withId(1L)
                .withName("Product1")
                .withPrice(100.0)
                .withValidFrom(currentDate)
                .withValidTo(new Date(125, 12, 12))
                .withQuantity(5)
                .isActive(true)
                .build();

        Product product2 = new ProductBuilder()
                .withId(2L)
                .withName("Product2")
                .withPrice(150.0)
                .withValidFrom(currentDate)
                .withValidTo(new Date(125, 12, 15))
                .withQuantity(3)
                .isActive(true)
                .build();

        products.add(product1);
        products.add(product2);

        when(productRepository.findAllByActiveIsTrueAndValidToIsAfter(any())).thenReturn(products);

        cut.printAllProducts();

        String expectedOutput = "----------------------------------------------------------------------\r\n" +
                "ID    | Name       | Price    | Valid from   | Valid to     | Quantity\r\n" +
                "----------------------------------------------------------------------\r\n" +
                "1     | Product1   | 100.0    | " + new SimpleDateFormat("yyyy-MM-dd").format(currentDate) + "   | 2026-01-12   | 5\r\n" +
                "2     | Product2   | 150.0    | " + new SimpleDateFormat("yyyy-MM-dd").format(currentDate) + "   | 2026-01-15   | 3\r\n" +
                "----------------------------------------------------------------------";

        assertEquals(expectedOutput, outputStreamCaptor.toString().trim());
    }

    @Test
    public void testPrintAllProducts_NoProducts() {
        when(productRepository.findAllByActiveIsTrueAndValidToIsAfter(any())).thenReturn(Collections.emptyList());
        assertThrows(IllegalArgumentException.class, () -> {
            cut.printAllProducts();
        });
    }

    @Test
    public void testUpdateProductQuantityAndActive_DecreaseQuantityAndDeactivate() {
        Product product = new ProductBuilder()
                .withId(1L)
                .withName("Product1")
                .withPrice(100.0)
                .withValidFrom(new Date())
                .withValidTo(new Date(100, 12, 12))
                .withQuantity(0)
                .isActive(true)
                .build();

        cut.updateProductQuantityAndActive(product);

        assertFalse(product.isActive());
        assertEquals(-1, product.getQuantity());
    }

    @Test
    public void testUpdateProductQuantityAndActive_DeactivateWhenValidToPassed() {
        Product product = new Product();
        product.setQuantity(5);
        product.setValidTo(new Date(100, 1, 1));

        cut.updateProductQuantityAndActive(product);

        assertFalse(product.isActive());
        assertEquals(4, product.getQuantity());
    }

    @Test
    public void testAddNewProduct() {
        Product newProduct = new ProductBuilder()
                .withName("Product1")
                .withPrice(100.0)
                .withValidFrom(new Date())
                .withValidTo(new Date(2025, 12, 12))
                .withQuantity(5)
                .isActive(true)
                .build();

        when(productRepository.findProductByName("Product1")).thenReturn(Optional.empty());
        cut.addNewProduct(newProduct);

        assertTrue(newProduct.isActive());
        verify(productRepository, times(1)).save(newProduct);
    }

    @Test
    public void testAddNewProductWhenProductExists() {
        Product existingProduct = new ProductBuilder()
                .withName("Product1")
                .withPrice(100.0)
                .withValidFrom(new Date())
                .withValidTo(new Date(2025, 12, 12))
                .withQuantity(5)
                .isActive(true)
                .build();

        when(productRepository.findProductByName("Product1")).thenReturn(Optional.of(existingProduct));

        assertThrows(IllegalStateException.class, () -> cut.addNewProduct(existingProduct));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    public void testDeleteProduct_SetNotActive() {
        String productName = "Product1";
        Product inactiveProduct = new ProductBuilder()
                .withName(productName)
                .withPrice(100.0)
                .withValidFrom(new Date())
                .withValidTo(new Date(125, 12, 12))
                .withQuantity(5)
                .isActive(true)
                .build();

        when(productRepository.findProductByName(productName)).thenReturn(Optional.of(inactiveProduct));

        cut.deleteProductByName_setNotActive(productName);

        assertFalse(inactiveProduct.isActive());
        verify(productRepository, times(1)).save(inactiveProduct);
    }

    @Test
    public void testDeleteActiveProductWithOrders() {
        // Priprema podataka za test
        String productName = "Product1";
        Product activeProduct = new ProductBuilder()
                .withName(productName)
                .withPrice(100.0)
                .withValidFrom(new Date())
                .withValidTo(new Date(125, 12, 12))
                .withQuantity(5)
                .isActive(false)
                .build();

        Order order1 = new Order();
        List<OrderItem> orderItems = new ArrayList<>();

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderItem_id(1L);

        orderItems.add(orderItem);

        orderItem.setOrder(order1);
        activeProduct.setOrderItems(orderItems);
        when(productRepository.findProductByName(productName)).thenReturn(Optional.of(activeProduct));

        cut.deleteProductByName_setNotActive(productName);

        verify(orderItemRepository, times(1)).deleteOrderItemByOrderItem_id(orderItem.getOrderItem_id());
        verify(orderRepository, times(1)).delete(order1);

        assertFalse(activeProduct.isActive());

        verify(productRepository, times(1)).delete(activeProduct);
    }

    @Test
    public void testDeleteNonExistentProduct() {
        when(productRepository.findProductByName("NonExistentProduct")).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> {
            cut.deleteProductByName_setNotActive("NonExistentProduct");
        });
    }
}

