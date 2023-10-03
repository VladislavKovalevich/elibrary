package by.vlad.elibrary.service;

import by.vlad.elibrary.model.dto.request.OrderRequestDto;
import by.vlad.elibrary.model.dto.response.OrderResponseDto;

import java.util.List;

public interface OrderService {

    OrderResponseDto returnOrderDetailsById(Long id);

    List<OrderResponseDto> returnOrdersByUserId(Long userId);

    List<OrderResponseDto> returnAllOrders();

    OrderResponseDto createNewOrder(OrderRequestDto dto);

    OrderResponseDto addBookToOrder(OrderRequestDto dto);

    OrderResponseDto removeBookFromOrder(OrderRequestDto dto);

    OrderResponseDto reserveOrder(Long id);

    OrderResponseDto acceptOrder(Long id);

    OrderResponseDto rejectOrder(Long id);

    OrderResponseDto returnOrder(Long id);
}
