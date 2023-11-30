package by.vlad.elibrary.controller.impl;

import by.vlad.elibrary.controller.OrderController;
import by.vlad.elibrary.model.dto.request.OrderRequestDto;
import by.vlad.elibrary.model.dto.response.OrderResponseDto;
import by.vlad.elibrary.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderControllerImpl implements OrderController {

    private final OrderService orderService;

    @Override
    public ResponseEntity<List<OrderResponseDto>> returnAllOrders() {
        return new ResponseEntity<>(orderService.returnAllOrders(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<OrderResponseDto> createOrder(OrderRequestDto dto) {
        return new ResponseEntity<>(orderService.createNewOrder(dto), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<OrderResponseDto> addBookToOrder(OrderRequestDto dto) {
        return new ResponseEntity<>(orderService.addBookToOrder(dto), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<OrderResponseDto> removeBookFromOrder(OrderRequestDto dto) {
        return new ResponseEntity<>(orderService.removeBookFromOrder(dto), HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<OrderResponseDto> reserveOrder(Long id) {
        return new ResponseEntity<>(orderService.reserveOrder(id), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<OrderResponseDto> acceptOrder(Long id) {
        return new ResponseEntity<>(orderService.acceptOrder(id), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<OrderResponseDto> rejectOrder(Long id) {
        return new ResponseEntity<>(orderService.rejectOrder(id), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<OrderResponseDto> returnOrder(Long id) {
        return new ResponseEntity<>(orderService.returnOrder(id), HttpStatus.OK);
    }
}
