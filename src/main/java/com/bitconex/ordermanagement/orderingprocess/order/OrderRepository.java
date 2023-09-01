package com.bitconex.ordermanagement.orderingprocess.order;

import com.bitconex.ordermanagement.administration.user.User;
import com.bitconex.ordermanagement.orderingprocess.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o JOIN FETCH o.orderItems WHERE o.user = :user")
    List<Order> findOrdersByUser(User user);
}
