package com.bitconex.ordermanagement.administration.product;

import com.bitconex.ordermanagement.orderingprocess.order.Order;
import com.bitconex.ordermanagement.orderingprocess.order.OrderRepository;
import com.bitconex.ordermanagement.orderingprocess.orderitem.OrderItem;
import com.bitconex.ordermanagement.orderingprocess.orderitem.OrderItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    private final OrderRepository orderRepository;
    @Autowired
    public ProductService(ProductRepository productRepository, OrderItemRepository orderItemRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
    }

    public List<ProductDTO> getProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOS = new ArrayList<>();
        for(Product product : products) {
            ProductDTO productDTO = convertToProductDTO(product);
            productDTOS.add(productDTO);
        }
        return productDTOS;
    }

    public ProductDTO convertToProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setPrice(product.getPrice());
        productDTO.setValidTo(product.getValidTo());
        productDTO.setValidFrom(product.getValidFrom());
        productDTO.setQuantity(product.getQuantity());
        return productDTO;
    }

    public void addNewProduct(Product newProduct) {
        Product product = productRepository.findProductByName(newProduct.getName()).orElse(null);
        if (product != null) {
            throw new IllegalStateException("Product already existing!");
            } else
                newProduct.setActive(true);
                productRepository.save(newProduct);
    }

    @Transactional
    //soft delete
    public void deleteProductByName_setNotActive(String name) {
        Product product = productRepository.findProductByName(name).orElse(null);
        if(product == null) {
            throw new IllegalStateException("There is no product with that name!");
        } else {
            if (product.isActive()) {
                product.setActive(false);
                productRepository.save(product);
            } else {  //trebam implementirat kaskadno brisanje
                System.out.println("Do you want for sure to delete product? ");
                List<OrderItem> orderItems = product.getOrderItems();
//                for (OrderItem orderItem : orderItems) {
//                    Order order = orderItem.getOrder();
//                    orderItemRepository.deleteOrderItemByOrderItem_id(orderItem.getOrderItem_id());
//
//                    // Ako više nema drugih stavki u narudžbini, obrišite i samu narudžbinu
//                    if (order.getOrderItems().isEmpty()) {
//                        orderRepository.delete(order);
//                    }
//                }
                productRepository.delete(product); //ako je false a ima ga jos na orderima?
            }
        }
    }



    public void printAllProducts() {
        List<Product> products = productRepository.findAllByActiveIsTrueAndValidToIsAfter(new Date());
        System.out.println("----------------------------------------------------------------------");
        System.out.println("ID    | Name       | Price    | Valid from   | Valid to     | Quantity");
        System.out.println("----------------------------------------------------------------------");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (Product product : products) {
            System.out.printf("%-5s | %-10s | %-8s | %-12s | %-12s | %s%n",
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    dateFormat.format(product.getValidFrom()),
                    dateFormat.format(product.getValidTo()),
                    product.getQuantity());
        }
        System.out.println("----------------------------------------------------------------------");
    }


    @Transactional
    public void deleteProductsOutOfStock() {
        productRepository.deleteProductsOutOfStock();
    }

    public void deleteProductsNoLongerAvailable() {
        productRepository.deleteExpiredProducts();
    }
}


