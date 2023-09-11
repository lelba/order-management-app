package com.bitconex.ordermanagement.orderingprocess.orderitem;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Modifying
    @Transactional
   @Query("DELETE FROM OrderItem p WHERE p.orderItem_id =?1")
    void deleteOrderItemByOrderItem_id(Long id);
}
