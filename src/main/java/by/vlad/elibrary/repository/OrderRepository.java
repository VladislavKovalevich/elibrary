package by.vlad.elibrary.repository;

import by.vlad.elibrary.model.entity.Order;
import by.vlad.elibrary.model.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findOrdersByClientId(Long clientId);

    List<Order> findOrdersByStatusIn(List<OrderStatus> statuses);

    List<Order> findOrderByClientIdAndStatus(Long id, OrderStatus status);

    List<Order> findOrderByClientIdAndNameLike(Long id, String regex);
}
