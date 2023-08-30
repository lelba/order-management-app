package com.bitconex.ordermanagement.orderingprocess;

import com.bitconex.ordermanagement.administration.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findOrdersByUser(User user);
}
