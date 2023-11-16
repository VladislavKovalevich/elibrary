package by.vlad.elibrary.repository;

import by.vlad.elibrary.model.entity.Order;
import by.vlad.elibrary.model.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findOrdersByClientId(Long clientId);

    List<Order> findOrdersByStatusIn(List<OrderStatus> statuses);

    Integer countOrdersByStatusAndClientEmailAndReturnedDateBetween(
            OrderStatus orderStatus,
            String email,
            LocalDate start,
            LocalDate end
    );

    Optional<Order> findTopByClientIdAndStatusOrderByReturnedDateDesc(Long aLong, OrderStatus status);

    List<Order> findOrderByClientIdAndStatus(Long id, OrderStatus status);

    List<Order> findOrderByClientIdAndNameLike(Long id, String regex);
}
