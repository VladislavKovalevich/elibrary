package by.vlad.elibrary.service;

import by.vlad.elibrary.exception.InvalidRequestDataException;
import by.vlad.elibrary.mapper.OrderMapper;
import by.vlad.elibrary.model.dto.request.OrderRequestDto;
import by.vlad.elibrary.model.dto.response.ClientResponseDto;
import by.vlad.elibrary.model.dto.response.OrderResponseDto;
import by.vlad.elibrary.model.entity.Book;
import by.vlad.elibrary.model.entity.Client;
import by.vlad.elibrary.model.entity.Order;
import by.vlad.elibrary.model.entity.OrderStatus;
import by.vlad.elibrary.repository.BookRepository;
import by.vlad.elibrary.repository.ClientRepository;
import by.vlad.elibrary.repository.OrderRepository;
import by.vlad.elibrary.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static by.vlad.elibrary.exception.util.ExceptionMessage.BOOK_IS_ALREADY_IN_ORDER;
import static by.vlad.elibrary.exception.util.ExceptionMessage.BOOK_IS_NOT_AVAILABLE_IN_CURRENT_ORDER;
import static by.vlad.elibrary.exception.util.ExceptionMessage.BOOK_NOT_FOUND;
import static by.vlad.elibrary.exception.util.ExceptionMessage.CLIENT_NOT_FOUND;
import static by.vlad.elibrary.exception.util.ExceptionMessage.MISMATCH_CLIENT_AND_ORDER_OWNER;
import static by.vlad.elibrary.exception.util.ExceptionMessage.ORDER_IS_ALREADY_EMPTY;
import static by.vlad.elibrary.exception.util.ExceptionMessage.ORDER_NOT_FOUND;
import static by.vlad.elibrary.exception.util.ExceptionMessage.WRONG_ORDER_STATUS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    private static final String CLIENT_EMAIL = "test@gmail.com";
    private static final String WRONG_CLIENT_EMAIL = "wrong@gmail.com";

    private void mockAuthContext() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(CLIENT_EMAIL);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void returnAllOrdersShouldReturnValidList() {
        List<Order> orders = getOrders();

        when(orderRepository.findAll()).thenReturn(orders);
        when(orderMapper.fromEntitiesToDtos(orders)).thenReturn(getOrderDtos());

        List<OrderResponseDto> list = orderService.returnAllOrders();

        assertThat(list).isNotNull();
        assertThat(list.size()).isEqualTo(1);

        verify(orderRepository, times(1)).findAll();
        verify(orderMapper, times(1)).fromEntitiesToDtos(orders);
    }

    @Test
    public void returnOrdersByUserIdShouldReturnValidList() {
        List<Order> orders = List.of();

        when(clientRepository.existsById(1L)).thenReturn(true);
        when(orderRepository.findOrdersByClientId(1L)).thenReturn(orders);
        when(orderMapper.fromEntitiesToDtos(orders)).thenReturn(List.of());

        List<OrderResponseDto> list = orderService.returnOrdersByUserId(1L);

        assertThat(list).isNotNull();
        assertThat(list).isEmpty();

        verify(clientRepository, times(1)).existsById(1L);
        verify(orderRepository, times(1)).findOrdersByClientId(1L);
        verify(orderMapper, times(1)).fromEntitiesToDtos(orders);
    }

    @Test
    public void returnOrdersByUserIdShouldReturnErrorIfClientNotExists() {
        when(clientRepository.existsById(1L)).thenReturn(false);

        assertThrows(InvalidRequestDataException.class, () -> orderService.returnOrdersByUserId(1L), CLIENT_NOT_FOUND);

        verify(clientRepository, times(1)).existsById(1L);
    }

    @Test
    public void createNewOrderShouldReturnValidOrder() {
        OrderRequestDto dto = OrderRequestDto.builder().name("Order#1").build();
        Client client = Client.builder().id(1L).build();

        Order savedOrder = Order.builder()
                .id(1L)
                .name("Order#1")
                .status(OrderStatus.CREATED)
                .client(client)
                .build();

        OrderResponseDto responseDto = OrderResponseDto.builder()
                .id(savedOrder.getId())
                .name(savedOrder.getName())
                .status(savedOrder.getStatus().toString())
                .build();

        mockAuthContext();

        when(clientRepository.findByEmail(CLIENT_EMAIL)).thenReturn(Optional.of(client));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.fromEntityToDto(savedOrder)).thenReturn(responseDto);

        OrderResponseDto result = orderService.createNewOrder(dto);

        assertThat(result.getId()).isEqualTo(responseDto.getId());
        assertThat(result.getName()).isEqualTo(responseDto.getName());
        assertThat(result.getStatus()).isEqualTo(responseDto.getStatus());

        verify(clientRepository, times(1)).findByEmail(any());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderMapper, times(1)).fromEntityToDto(savedOrder);
    }

    @Test
    public void createNewOrderShouldReturnErrorIfClientNotExists() {
        OrderRequestDto dto = OrderRequestDto.builder().name("Order#1").build();

        mockAuthContext();

        when(clientRepository.findByEmail(CLIENT_EMAIL)).thenReturn(Optional.empty());

        assertThrows(InvalidRequestDataException.class, () -> orderService.createNewOrder(dto), CLIENT_NOT_FOUND);

        verify(clientRepository, times(1)).findByEmail(anyString());
    }

    @Test
    public void addBookToOrderShouldReturnValidOrder() {
        OrderRequestDto dto = OrderRequestDto.builder()
                .id(1L)
                .bookId(1L)
                .build();

        Order order = Order.builder().id(1L).status(OrderStatus.CREATED).build();

        Order savedOrder = Order.builder().build();

        Book book = Book.builder().id(1L).title("Book#1").build();

        OrderResponseDto orderResponseDto = OrderResponseDto.builder().build();

        when(orderRepository.findById(dto.getId())).thenReturn(Optional.of(order));
        when(bookRepository.findById(dto.getBookId())).thenReturn(Optional.of(book));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.fromEntityToDto(any(Order.class))).thenReturn(orderResponseDto);

        orderService.addBookToOrder(dto);

        assertThat(order.getBooks()).hasSize(1);

        verify(orderRepository, times(1)).findById(dto.getId());
        verify(bookRepository, times(1)).findById(dto.getBookId());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderMapper, times(1)).fromEntityToDto(any(Order.class));
    }

    @Test
    public void addBookToOrderShouldReturnValidOrderIfBooksNotEqual() {
        OrderRequestDto dto = OrderRequestDto.builder()
                .id(1L)
                .bookId(1L)
                .build();

        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.CREATED)
                .books(new ArrayList<>(List.of(Book.builder().id(2L).title("Book#2").build())))
                .build();

        Order savedOrder = Order.builder().build();

        Book book = Book.builder().id(1L).title("Book#1").build();

        OrderResponseDto orderResponseDto = OrderResponseDto.builder().build();

        when(orderRepository.findById(dto.getId())).thenReturn(Optional.of(order));
        when(bookRepository.findById(dto.getBookId())).thenReturn(Optional.of(book));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.fromEntityToDto(any(Order.class))).thenReturn(orderResponseDto);

        orderService.addBookToOrder(dto);

        assertThat(order.getBooks()).hasSize(2);

        verify(orderRepository, times(1)).findById(dto.getId());
        verify(bookRepository, times(1)).findById(dto.getBookId());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderMapper, times(1)).fromEntityToDto(any(Order.class));
    }

    @Test
    public void addBookToOrderShouldReturnErrorIfBooksEqual() {
        OrderRequestDto dto = OrderRequestDto.builder()
                .id(1L)
                .bookId(1L)
                .build();

        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.CREATED)
                .books(new ArrayList<>(List.of(Book.builder().id(1L).title("Book#1").build())))
                .build();

        Book book = Book.builder().id(1L).title("Book#1").build();

        when(orderRepository.findById(dto.getId())).thenReturn(Optional.of(order));
        when(bookRepository.findById(dto.getBookId())).thenReturn(Optional.of(book));

        assertThrows(InvalidRequestDataException.class, () -> orderService.addBookToOrder(dto), BOOK_IS_ALREADY_IN_ORDER);

        verify(orderRepository, times(1)).findById(dto.getId());
        verify(bookRepository, times(1)).findById(dto.getBookId());
    }

    @Test
    public void addBookToOrderShouldReturnErrorIfOrderNotExists() {
        OrderRequestDto dto = OrderRequestDto.builder()
                .id(1L)
                .bookId(1L)
                .build();

        when(orderRepository.findById(dto.getId())).thenReturn(Optional.empty());

        assertThrows(InvalidRequestDataException.class, () -> orderService.addBookToOrder(dto), ORDER_NOT_FOUND);

        verify(orderRepository, times(1)).findById(dto.getId());
    }

    @Test
    public void addBookToOrderShouldReturnErrorIfBookNotExists() {
        OrderRequestDto dto = OrderRequestDto.builder()
                .id(1L)
                .bookId(1L)
                .build();

        Order order = Order.builder().status(OrderStatus.CREATED).build();

        when(orderRepository.findById(dto.getId())).thenReturn(Optional.of(order));
        when(bookRepository.findById(dto.getId())).thenReturn(Optional.empty());

        assertThrows(InvalidRequestDataException.class, () -> orderService.addBookToOrder(dto), BOOK_NOT_FOUND);

        verify(orderRepository, times(1)).findById(dto.getId());
        verify(bookRepository, times(1)).findById(dto.getBookId());
    }

    @Test
    public void addBookToOrderShouldReturnErrorIfWrongOrderStatus() {
        OrderRequestDto dto = OrderRequestDto.builder()
                .id(1L)
                .bookId(1L)
                .build();

        Order order = Order.builder().status(OrderStatus.RETURNED).build();

        when(orderRepository.findById(dto.getId())).thenReturn(Optional.of(order));

        assertThrows(InvalidRequestDataException.class, () -> orderService.addBookToOrder(dto), WRONG_ORDER_STATUS);

        verify(orderRepository, times(1)).findById(dto.getId());
    }

    @Test
    public void removeBookFromOrderShouldReturnValidOrder() {
        OrderRequestDto dto = OrderRequestDto.builder()
                .id(1L)
                .bookId(1L)
                .build();

        Order order = Order.builder()
                .status(OrderStatus.CREATED)
                .books(new ArrayList<>(List.of(Book.builder().id(1L).build())))
                .build();

        Book book = Book.builder().id(1L).build();

        OrderResponseDto responseDto = OrderResponseDto.builder()
                .build();

        when(orderRepository.findById(dto.getId())).thenReturn(Optional.of(order));
        when(bookRepository.findById(dto.getBookId())).thenReturn(Optional.of(book));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.fromEntityToDto(any(Order.class))).thenReturn(responseDto);

        orderService.removeBookFromOrder(dto);

        assertThat(order.getBooks()).isEmpty();

        verify(orderRepository, times(1)).findById(dto.getId());
        verify(bookRepository, times(1)).findById(dto.getBookId());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderMapper, times(1)).fromEntityToDto(any(Order.class));
    }

    @Test
    public void removeBookFromOrderShouldReturnErrorIfBookNotInOrder() {
        OrderRequestDto dto = OrderRequestDto.builder()
                .id(1L)
                .bookId(1L)
                .build();

        Order order = Order.builder()
                .status(OrderStatus.CREATED)
                .books(new ArrayList<>(List.of(Book.builder().id(2L).build())))
                .build();

        Book book = Book.builder().id(1L).build();

        when(orderRepository.findById(dto.getId())).thenReturn(Optional.of(order));
        when(bookRepository.findById(dto.getBookId())).thenReturn(Optional.of(book));

        assertThrows(InvalidRequestDataException.class, () -> orderService.removeBookFromOrder(dto),
                BOOK_IS_NOT_AVAILABLE_IN_CURRENT_ORDER);

        verify(orderRepository, times(1)).findById(dto.getId());
        verify(bookRepository, times(1)).findById(dto.getBookId());
    }

    @Test
    public void removeBookFromOrderShouldReturnErrorIfOrderIsEmpty() {
        OrderRequestDto dto = OrderRequestDto.builder()
                .id(1L)
                .bookId(1L)
                .build();

        Order order = Order.builder()
                .status(OrderStatus.CREATED)
                .build();

        Book book = Book.builder().id(1L).build();

        when(orderRepository.findById(dto.getId())).thenReturn(Optional.of(order));
        when(bookRepository.findById(dto.getBookId())).thenReturn(Optional.of(book));

        assertThrows(InvalidRequestDataException.class, () -> orderService.removeBookFromOrder(dto),
                ORDER_IS_ALREADY_EMPTY);

        verify(orderRepository, times(1)).findById(dto.getId());
        verify(bookRepository, times(1)).findById(dto.getBookId());
    }

    @Test
    public void removeBookFromOrderShouldReturnErrorIfBookNotFound() {
        OrderRequestDto dto = OrderRequestDto.builder()
                .id(1L)
                .bookId(1L)
                .build();

        Order order = Order.builder()
                .status(OrderStatus.CREATED)
                .build();

        when(orderRepository.findById(dto.getId())).thenReturn(Optional.of(order));
        when(bookRepository.findById(dto.getBookId())).thenReturn(Optional.empty());

        assertThrows(InvalidRequestDataException.class, () -> orderService.removeBookFromOrder(dto), BOOK_NOT_FOUND);

        verify(orderRepository, times(1)).findById(dto.getId());
        verify(bookRepository, times(1)).findById(dto.getBookId());
    }

    @Test
    public void removeBookFromOrderShouldReturnErrorIfOrderStatusIsWrong() {
        OrderRequestDto dto = OrderRequestDto.builder()
                .id(1L)
                .bookId(1L)
                .build();

        Order order = Order.builder()
                .status(OrderStatus.RESERVED)
                .build();

        when(orderRepository.findById(dto.getId())).thenReturn(Optional.of(order));

        assertThrows(InvalidRequestDataException.class, () -> orderService.removeBookFromOrder(dto), WRONG_ORDER_STATUS);

        verify(orderRepository, times(1)).findById(dto.getId());
    }

    @Test
    public void removeBookFromOrderShouldReturnErrorIfOrderNotExists() {
        OrderRequestDto dto = OrderRequestDto.builder()
                .id(1L)
                .bookId(1L)
                .build();

        when(orderRepository.findById(dto.getId())).thenReturn(Optional.empty());

        assertThrows(InvalidRequestDataException.class, () -> orderService.removeBookFromOrder(dto), ORDER_NOT_FOUND);

        verify(orderRepository, times(1)).findById(dto.getId());
    }

    @Test
    public void reserveOrderShouldReturnValidStatus() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.CREATED)
                .client(Client.builder().email(CLIENT_EMAIL).build())
                .build();

        OrderResponseDto dto = OrderResponseDto.builder()
                .id(order.getId())
                .status(order.getStatus().toString())
                .build();

        mockAuthContext();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.fromEntityToDto(order)).thenReturn(dto);

        orderService.reserveOrder(1L);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.RESERVED);

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderMapper, times(1)).fromEntityToDto(order);
    }

    @Test
    public void reserveOrderShouldReturnErrorIfClientIsWrong() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.CREATED)
                .client(Client.builder().email(WRONG_CLIENT_EMAIL).build())
                .build();

        mockAuthContext();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidRequestDataException.class, () -> orderService.reserveOrder(1L),
                MISMATCH_CLIENT_AND_ORDER_OWNER);

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void reserveOrderShouldReturnErrorIfOrderStatusIsWrong() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.RESERVED)
                .client(Client.builder().email(WRONG_CLIENT_EMAIL).build())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidRequestDataException.class, () -> orderService.reserveOrder(1L),
                WRONG_ORDER_STATUS);

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void reserveOrderShouldReturnErrorIfOrderNotExists() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(InvalidRequestDataException.class, () -> orderService.reserveOrder(1L), ORDER_NOT_FOUND);

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void acceptOrderShouldReturnValidOrder() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.RESERVED)
                .client(Client.builder().email(CLIENT_EMAIL).build())
                .books(new ArrayList<>(List.of(Book.builder().id(1L).copiesNumber(2).build())))
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.fromEntityToDto(order)).thenReturn(any(OrderResponseDto.class));

        orderService.acceptOrder(1L);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        assertThat(order.getAcceptedDate()).isNotNull();

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(order);
        verify(orderMapper, times(1)).fromEntityToDto(order);
    }

    @Test
    public void acceptOrderShouldReturnErrorIfBookCopiesIsEmpty() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.RESERVED)
                .client(Client.builder().email(CLIENT_EMAIL).build())
                .books(new ArrayList<>(List.of(Book.builder().id(1L).copiesNumber(0).build())))
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidRequestDataException.class, () -> orderService.acceptOrder(1L));

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void acceptOrderShouldReturnErrorIfWrongOrderStatus() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.CREATED)
                .client(Client.builder().email(CLIENT_EMAIL).build())
                .books(new ArrayList<>(List.of(Book.builder().id(1L).copiesNumber(2).build())))
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidRequestDataException.class, () -> orderService.acceptOrder(1L), WRONG_ORDER_STATUS);

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void acceptOrderShouldReturnErrorIfOrderNotExists() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(InvalidRequestDataException.class, () -> orderService.acceptOrder(1L), ORDER_NOT_FOUND);

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void rejectOrderShouldReturnValidOrder() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.RESERVED)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.fromEntityToDto(order)).thenReturn(any(OrderResponseDto.class));

        orderService.rejectOrder(1L);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.REJECTED);

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(order);
        verify(orderMapper, times(1)).fromEntityToDto(order);
    }

    @Test
    public void rejectOrderShouldReturnErrorIfWrongOrderStatus() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.ACCEPTED)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidRequestDataException.class, () -> orderService.rejectOrder(1L), WRONG_ORDER_STATUS);

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void rejectOrderShouldReturnErrorIfOrderNotExists() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(InvalidRequestDataException.class, () -> orderService.rejectOrder(1L), ORDER_NOT_FOUND);

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void returnOrderShouldReturnValidOrder(){
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.ACCEPTED)
                .client(Client.builder().email(CLIENT_EMAIL).isNonLocked(true).build())
                .books(new ArrayList<>(List.of(Book.builder().id(1L).copiesNumber(2).build())))
                .acceptedDate(LocalDate.now().minusDays(15))
                .build();

        mockAuthContext();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.fromEntityToDto(order)).thenReturn(any(OrderResponseDto.class));

        orderService.returnOrder(1L);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.RETURNED);
        assertThat(order.getReturnedDate()).isNotNull();

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(order);
        verify(orderMapper, times(1)).fromEntityToDto(order);
    }

    @Test
    public void returnOrderShouldReturnOverdueOrder(){
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.ACCEPTED)
                .client(Client.builder().email(CLIENT_EMAIL).isNonLocked(true).build())
                .books(new ArrayList<>(List.of(Book.builder().id(1L).copiesNumber(2).build())))
                .acceptedDate(LocalDate.now().minusDays(25))
                .build();

        mockAuthContext();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        when(orderRepository.countOrdersByStatusAndClientEmailAndReturnedDateBetween(eq(OrderStatus.OVERDUE),
                eq(CLIENT_EMAIL), any(), any()))
                .thenReturn(2);

        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.fromEntityToDto(order)).thenReturn(any(OrderResponseDto.class));

        orderService.returnOrder(1L);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.OVERDUE);
        assertThat(order.getClient().getIsNonLocked()).isTrue();
        assertThat(order.getReturnedDate()).isNotNull();

        verify(orderRepository, times(1)).findById(1L);

        verify(orderRepository, times(1))
                .countOrdersByStatusAndClientEmailAndReturnedDateBetween(eq(OrderStatus.OVERDUE),
                        eq(CLIENT_EMAIL), any(), any());

        verify(orderRepository, times(1)).save(order);
        verify(orderMapper, times(1)).fromEntityToDto(order);
    }

    @Test
    public void returnOrderShouldReturnOverdueOrderAndBlockedClient(){
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.ACCEPTED)
                .client(Client.builder().email(CLIENT_EMAIL).isNonLocked(true).build())
                .books(new ArrayList<>(List.of(Book.builder().id(1L).copiesNumber(2).build())))
                .acceptedDate(LocalDate.now().minusDays(25))
                .build();

        mockAuthContext();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        when(orderRepository.countOrdersByStatusAndClientEmailAndReturnedDateBetween(eq(OrderStatus.OVERDUE),
                eq(CLIENT_EMAIL), any(), any()))
                .thenReturn(4);

        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.fromEntityToDto(order)).thenReturn(any(OrderResponseDto.class));

        orderService.returnOrder(1L);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.OVERDUE);
        assertThat(order.getClient().getIsNonLocked()).isFalse();
        assertThat(order.getReturnedDate()).isNotNull();

        verify(orderRepository, times(1)).findById(1L);

        verify(orderRepository, times(1))
                .countOrdersByStatusAndClientEmailAndReturnedDateBetween(eq(OrderStatus.OVERDUE),
                        eq(CLIENT_EMAIL), any(), any());

        verify(orderRepository, times(1)).save(order);
        verify(orderMapper, times(1)).fromEntityToDto(order);
    }

    @Test
    public void returnOrderShouldReturnErrorIfClientMismatch(){
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.ACCEPTED)
                .client(Client.builder().email(WRONG_CLIENT_EMAIL).isNonLocked(true).build())
                .books(new ArrayList<>(List.of(Book.builder().id(1L).copiesNumber(2).build())))
                .acceptedDate(LocalDate.now().minusDays(25))
                .build();

        mockAuthContext();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidRequestDataException.class, () -> orderService.returnOrder(1L), MISMATCH_CLIENT_AND_ORDER_OWNER);

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void returnOrderShouldReturnErrorIfWrongOrderStatus(){
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.CREATED)
                .client(Client.builder().email(WRONG_CLIENT_EMAIL).isNonLocked(true).build())
                .books(new ArrayList<>(List.of(Book.builder().id(1L).copiesNumber(2).build())))
                .acceptedDate(LocalDate.now().minusDays(25))
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidRequestDataException.class, () -> orderService.returnOrder(1L), WRONG_ORDER_STATUS);

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void returnOrderShouldReturnErrorIfOrderNotExists(){
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(InvalidRequestDataException.class, () -> orderService.returnOrder(1L), ORDER_NOT_FOUND);

        verify(orderRepository, times(1)).findById(1L);
    }

    private List<Order> getOrders() {
        return List.of(
                Order.builder()
                        .id(1L)
                        .name("Order#1")
                        .client(Client.builder().name("client#1").build())
                        .status(OrderStatus.CREATED)
                        .build()
        );
    }

    private List<OrderResponseDto> getOrderDtos() {
        return List.of(
                OrderResponseDto.builder()
                        .name("Order#1")
                        .status("CREATED")
                        .client(ClientResponseDto.builder().name("client#1").build())
                        .build()
        );
    }
}
