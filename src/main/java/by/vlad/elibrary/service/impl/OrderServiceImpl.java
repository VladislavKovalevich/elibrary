package by.vlad.elibrary.service.impl;

import by.vlad.elibrary.exception.InvalidRequestDataException;
import by.vlad.elibrary.mapper.OrderMapper;
import by.vlad.elibrary.model.dto.request.OrderRequestDto;
import by.vlad.elibrary.model.dto.response.OrderResponseDto;
import by.vlad.elibrary.model.entity.Book;
import by.vlad.elibrary.model.entity.Client;
import by.vlad.elibrary.model.entity.Order;
import by.vlad.elibrary.model.entity.OrderStatus;
import by.vlad.elibrary.repository.BookRepository;
import by.vlad.elibrary.repository.ClientRepository;
import by.vlad.elibrary.repository.OrderRepository;
import by.vlad.elibrary.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static by.vlad.elibrary.exception.util.ExceptionMessage.BOOK_IS_ALREADY_IN_ORDER;
import static by.vlad.elibrary.exception.util.ExceptionMessage.BOOK_IS_NOT_AVAILABLE_IN_CURRENT_ORDER;
import static by.vlad.elibrary.exception.util.ExceptionMessage.BOOK_NOT_FOUND;
import static by.vlad.elibrary.exception.util.ExceptionMessage.CLIENT_NOT_FOUND;
import static by.vlad.elibrary.exception.util.ExceptionMessage.MISMATCH_CLIENT_AND_ORDER_OWNER;
import static by.vlad.elibrary.exception.util.ExceptionMessage.ORDER_IS_ALREADY_EMPTY;
import static by.vlad.elibrary.exception.util.ExceptionMessage.ORDER_NOT_FOUND;
import static by.vlad.elibrary.exception.util.ExceptionMessage.WRONG_ORDER_STATUS;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final BookRepository bookRepository;

    private final ClientRepository clientRepository;

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    @Override
    public OrderResponseDto returnOrderDetailsById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new InvalidRequestDataException(ORDER_NOT_FOUND));

        return orderMapper.fromEntityToDto(order);
    }

    @Override
    public List<OrderResponseDto> returnOrdersByUserId(Long userId) {
        if (!clientRepository.existsById(userId)) {
            throw new InvalidRequestDataException(CLIENT_NOT_FOUND);
        }

        List<Order> orders = orderRepository.findOrdersByClientId(userId);
        return orderMapper.fromEntitiesToDtos(orders);
    }

    @Override
    public List<OrderResponseDto> returnAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orderMapper.fromEntitiesToDtos(orders);
    }

    @Override
    public OrderResponseDto createNewOrder(OrderRequestDto dto) {
        Order order = new Order();

        order.setName(dto.getName());
        order.setStatus(OrderStatus.CREATED);

        String clientEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Client client = clientRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new InvalidRequestDataException(CLIENT_NOT_FOUND));

        order.setClient(client);

        Order savedOrder = orderRepository.save(order);

        return orderMapper.fromEntityToDto(savedOrder);
    }

    @Override
    public OrderResponseDto addBookToOrder(OrderRequestDto dto) {
        Order order = orderRepository.findById(dto.getId())
                .orElseThrow(() -> new InvalidRequestDataException(ORDER_NOT_FOUND));

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new InvalidRequestDataException(WRONG_ORDER_STATUS);
        }

        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new InvalidRequestDataException(BOOK_NOT_FOUND));

        List<Book> books;

        if (order.getBooks() == null) {
            books = new ArrayList<>();
        } else {
            books = order.getBooks();

            if (books.contains(book)) {
                throw new InvalidRequestDataException(BOOK_IS_ALREADY_IN_ORDER);
            }
        }

        books.add(book);
        order.setBooks(books);

        Order updatedOrder = orderRepository.save(order);

        return orderMapper.fromEntityToDto(updatedOrder);
    }

    @Override
    public OrderResponseDto removeBookFromOrder(OrderRequestDto dto) {
        Order order = orderRepository.findById(dto.getId())
                .orElseThrow(() -> new InvalidRequestDataException(ORDER_NOT_FOUND));

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new InvalidRequestDataException(WRONG_ORDER_STATUS);
        }

        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new InvalidRequestDataException(BOOK_NOT_FOUND));

        if (order.getBooks() == null) {
            throw new InvalidRequestDataException(ORDER_IS_ALREADY_EMPTY);
        } else {
            if (!order.getBooks().remove(book)) {
                throw new InvalidRequestDataException(BOOK_IS_NOT_AVAILABLE_IN_CURRENT_ORDER);
            }
        }

        Order updatedOrder = orderRepository.save(order);

        return orderMapper.fromEntityToDto(updatedOrder);
    }

    @Override
    public OrderResponseDto reserveOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new InvalidRequestDataException(ORDER_NOT_FOUND));

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new InvalidRequestDataException(WRONG_ORDER_STATUS);
        }

        String currentClientEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!currentClientEmail.equals(order.getClient().getEmail())) {
            throw new InvalidRequestDataException(MISMATCH_CLIENT_AND_ORDER_OWNER);
        }

        order.setStatus(OrderStatus.RESERVED);
        Order updatedOrder = orderRepository.save(order);

        return orderMapper.fromEntityToDto(updatedOrder);
    }

    @Override
    public OrderResponseDto acceptOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new InvalidRequestDataException(ORDER_NOT_FOUND));

        if (order.getStatus() != OrderStatus.RESERVED) {
            throw new InvalidRequestDataException(WRONG_ORDER_STATUS);
        }

        order.setStatus(OrderStatus.ACCEPTED);
        order.setAcceptedDate(LocalDate.now());

        List<Long> bookIds = new ArrayList<>();

        for (Book book : order.getBooks()) {
            if (book.getCopiesNumber() == 0) {
                bookIds.add(book.getId());
            }
        }

        if (!bookIds.isEmpty()) {
            throw new InvalidRequestDataException("Books with this ids " + bookIds + " have not enough copies for applying this order");
            //TODO создать отдельный тип исключения для передачи точных данных об отсутствующих книгах
        }

        List<Book> books = order.getBooks().stream()
                .peek(book -> book.setCopiesNumber(book.getCopiesNumber() - 1))
                .toList();

        order.setBooks(books);

        Order updatedOrder = orderRepository.save(order);

        return orderMapper.fromEntityToDto(updatedOrder);
    }

    @Override
    public OrderResponseDto rejectOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new InvalidRequestDataException(ORDER_NOT_FOUND));

        if (order.getStatus() != OrderStatus.RESERVED) {
            throw new InvalidRequestDataException(WRONG_ORDER_STATUS);
        }

        order.setStatus(OrderStatus.REJECTED);

        Order updatedOrder = orderRepository.save(order);

        return orderMapper.fromEntityToDto(updatedOrder);
    }

    @Override
    public OrderResponseDto returnOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new InvalidRequestDataException(ORDER_NOT_FOUND));

        if (order.getStatus() != OrderStatus.ACCEPTED) {
            throw new InvalidRequestDataException(WRONG_ORDER_STATUS);
        }

        String currentClientEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!currentClientEmail.equals(order.getClient().getEmail())) {
            throw new InvalidRequestDataException(MISMATCH_CLIENT_AND_ORDER_OWNER);
        }

        LocalDate returnedDate = LocalDate.now();

        order.setReturnedDate(returnedDate);

        if (ChronoUnit.DAYS.between(order.getAcceptedDate(), returnedDate) > 20) {
            order.setStatus(OrderStatus.OVERDUE);

            LocalDate startDate = order.getReturnedDate().minusDays(30);

            Integer overdueCount = orderRepository.countOrdersByStatusAndClientEmailAndReturnedDateBetween(
                    OrderStatus.OVERDUE,
                    currentClientEmail,
                    startDate,
                    order.getReturnedDate()
            );

            if (overdueCount > 3){
                order.getClient().setIsNonLocked(false);
            }
        } else {
            order.setStatus(OrderStatus.RETURNED);
        }

        List<Book> books = order.getBooks().stream()
                .peek(book -> book.setCopiesNumber(book.getCopiesNumber() + 1))
                .toList();

        order.setBooks(books);

        Order updatedOrder = orderRepository.save(order);

        return orderMapper.fromEntityToDto(updatedOrder);
    }
}
