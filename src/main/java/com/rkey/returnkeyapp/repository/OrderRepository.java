package com.rkey.returnkeyapp.repository;

import com.rkey.returnkeyapp.domain.Order;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Order entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByReturnsIdAndId(Long returnsId, Long id);
}
