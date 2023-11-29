package by.vlad.elibrary.controller;

import by.vlad.elibrary.config.security.JwtService;
import by.vlad.elibrary.controller.impl.OrderControllerImpl;
import by.vlad.elibrary.exception.InvalidRequestDataException;
import by.vlad.elibrary.exception.handler.RestExceptionHandler;
import by.vlad.elibrary.model.dto.request.OrderRequestDto;
import by.vlad.elibrary.model.dto.response.BookResponseDto;
import by.vlad.elibrary.model.dto.response.ClientResponseDto;
import by.vlad.elibrary.model.dto.response.OrderResponseDto;
import by.vlad.elibrary.service.ClientService;
import by.vlad.elibrary.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.stream.Stream;

import static by.vlad.elibrary.exception.util.ExceptionMessage.BOOK_IS_ALREADY_IN_ORDER;
import static by.vlad.elibrary.exception.util.ExceptionMessage.BOOK_IS_NOT_AVAILABLE_IN_CURRENT_ORDER;
import static by.vlad.elibrary.exception.util.ExceptionMessage.BOOK_NOT_FOUND;
import static by.vlad.elibrary.exception.util.ExceptionMessage.CLIENT_NOT_FOUND;
import static by.vlad.elibrary.exception.util.ExceptionMessage.MISMATCH_CLIENT_AND_ORDER_OWNER;
import static by.vlad.elibrary.exception.util.ExceptionMessage.ORDER_IS_ALREADY_EMPTY;
import static by.vlad.elibrary.exception.util.ExceptionMessage.ORDER_NOT_FOUND;
import static by.vlad.elibrary.exception.util.ExceptionMessage.WRONG_ORDER_STATUS;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderControllerImpl.class)
public class OrderControllerImplTest {

    @Autowired
    private OrderControllerImpl orderController;

    @MockBean
    private OrderService orderService;

    @MockBean
    private ClientService clientService;

    @MockBean
    private JwtService jwtService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static Stream<Arguments> addBookProvideParams() {
        return Stream.of(
                Arguments.of(new InvalidRequestDataException(ORDER_NOT_FOUND)),
                Arguments.of(new InvalidRequestDataException(WRONG_ORDER_STATUS)),
                Arguments.of(new InvalidRequestDataException(BOOK_NOT_FOUND)),
                Arguments.of(new InvalidRequestDataException(BOOK_IS_ALREADY_IN_ORDER))
        );
    }

    private static Stream<Arguments> removeBookFromOrderParametrized() {
        return Stream.of(
                Arguments.of(new InvalidRequestDataException(ORDER_NOT_FOUND)),
                Arguments.of(new InvalidRequestDataException(WRONG_ORDER_STATUS)),
                Arguments.of(new InvalidRequestDataException(BOOK_NOT_FOUND)),
                Arguments.of(new InvalidRequestDataException(ORDER_IS_ALREADY_EMPTY)),
                Arguments.of(new InvalidRequestDataException(BOOK_IS_NOT_AVAILABLE_IN_CURRENT_ORDER))
        );
    }

    private static Stream<Arguments> reservedAndReturnOrderParametrized(){
        return Stream.of(
                Arguments.of(new InvalidRequestDataException(ORDER_NOT_FOUND)),
                Arguments.of(new InvalidRequestDataException(WRONG_ORDER_STATUS)),
                Arguments.of(new InvalidRequestDataException(MISMATCH_CLIENT_AND_ORDER_OWNER))
        );
    }

    private static Stream<Arguments> acceptAndRejectOrderParametrized(){
        return Stream.of(
                Arguments.of(new InvalidRequestDataException(ORDER_NOT_FOUND)),
                Arguments.of(new InvalidRequestDataException(WRONG_ORDER_STATUS))
        );
    }

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(new RestExceptionHandler())
                .build();
    }

    @Test
    public void returnAllOrdersShouldReturnValidList() throws Exception {
        when(orderService.returnAllOrders()).thenReturn(getOrderResponseDtoList());

        mockMvc.perform(get("/order/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(orderService, times(1)).returnAllOrders();
    }

    @Test
    public void createNewOrderShouldReturnValidOrder() throws Exception {
        OrderRequestDto dto = getOrderRequestDto();

        when(orderService.createNewOrder(dto)).thenReturn(getOrderResponseDtoList().get(0));

        mockMvc.perform(post("/order/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.id").isNotEmpty());

        verify(orderService, times(1)).createNewOrder(dto);
    }

    @Test
    public void createNewTestShouldReturnErrorIfClientNotExists() throws Exception {
        OrderRequestDto dto = getOrderRequestDto();

        when(orderService.createNewOrder(dto)).thenThrow(new InvalidRequestDataException(CLIENT_NOT_FOUND));

        mockMvc.perform(post("/order/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(CLIENT_NOT_FOUND));

        verify(orderService, times(1)).createNewOrder(dto);
    }

    @Test
    public void addBookToOrderShouldReturnValidOrder() throws Exception {
        OrderRequestDto dto = getOrderRequestDto();

        when(orderService.addBookToOrder(dto)).thenReturn(getOrderResponseDtoList().get(1));

        mockMvc.perform(patch("/order/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books").isNotEmpty());

        verify(orderService, times(1)).addBookToOrder(dto);
    }

    @ParameterizedTest
    @MethodSource("addBookProvideParams")
    public void addBookToOrderParametrized(RuntimeException exception) throws Exception {
        OrderRequestDto dto = getOrderRequestDto();

        when(orderService.addBookToOrder(dto)).thenThrow(exception);

        mockMvc.perform(patch("/order/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(exception.getMessage()));

        verify(orderService, times(1)).addBookToOrder(dto);
    }

    @Test
    public void removeBookFromOrderShouldReturnValidOrder() throws Exception {
        OrderRequestDto dto = getOrderRequestDto();

        when(orderService.removeBookFromOrder(dto)).thenReturn(getOrderResponseDtoList().get(0));

        mockMvc.perform(delete("/order/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).removeBookFromOrder(dto);
    }

    @ParameterizedTest
    @MethodSource("removeBookFromOrderParametrized")
    public void removeBookFromOrderShouldReturnErrorParametrized(RuntimeException exception) throws Exception {
        OrderRequestDto dto = getOrderRequestDto();

        when(orderService.removeBookFromOrder(dto)).thenThrow(exception);

        mockMvc.perform(delete("/order/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(exception.getMessage()));

        verify(orderService, times(1)).removeBookFromOrder(dto);
    }

    @Test
    public void reserveOrderShouldReturnValidOrder() throws Exception {
        OrderResponseDto orderResponseDto = getOrderResponseDtoList().get(1);
        when(orderService.reserveOrder(1L)).thenReturn(orderResponseDto);

        mockMvc.perform(patch("/order/reserve/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESERVED"));

        verify(orderService, times(1)).reserveOrder(1L);
    }

    @ParameterizedTest
    @MethodSource("reservedAndReturnOrderParametrized")
    public void reservedOrderShouldReturnErrorParametrized(RuntimeException exception) throws Exception {
        when(orderService.reserveOrder(1L)).thenThrow(exception);

        mockMvc.perform(patch("/order/reserve/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(exception.getMessage()));

        verify(orderService, times(1)).reserveOrder(1L);
    }

    @Test
    public void acceptOrderShouldReturnValidOrder() throws Exception {
        OrderResponseDto orderResponseDto = getOrderResponseDtoList().get(1);
        orderResponseDto.setStatus("ACCEPTED");

        when(orderService.acceptOrder(1L)).thenReturn(orderResponseDto);

        mockMvc.perform(patch("/order/accept/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        verify(orderService, times(1)).acceptOrder(1L);
    }

    @ParameterizedTest
    @MethodSource("acceptAndRejectOrderParametrized")
    public void acceptOrderShouldReturnErrorParametrized(RuntimeException exception) throws Exception {
        when(orderService.acceptOrder(1L)).thenThrow(exception);

        mockMvc.perform(patch("/order/accept/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(exception.getMessage()));

        verify(orderService, times(1)).acceptOrder(1L);
    }

    @Test
    public void rejectOrderShouldReturnValidOrder() throws Exception {
        OrderResponseDto orderResponseDto = getOrderResponseDtoList().get(1);
        orderResponseDto.setStatus("REJECT");

        when(orderService.rejectOrder(1L)).thenReturn(orderResponseDto);

        mockMvc.perform(patch("/order/reject/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECT"));

        verify(orderService, times(1)).rejectOrder(1L);
    }

    @ParameterizedTest
    @MethodSource("acceptAndRejectOrderParametrized")
    public void rejectOrderShouldReturnErrorParametrized(RuntimeException exception) throws Exception {
        when(orderService.rejectOrder(1L)).thenThrow(exception);

        mockMvc.perform(patch("/order/reject/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(exception.getMessage()));

        verify(orderService, times(1)).rejectOrder(1L);
    }

    @Test
    public void returnOrderShouldReturnValidOrder() throws Exception {
        OrderResponseDto orderResponseDto = getOrderResponseDtoList().get(1);
        orderResponseDto.setStatus("RETURN");

        when(orderService.returnOrder(1L)).thenReturn(orderResponseDto);

        mockMvc.perform(patch("/order/return/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETURN"));

        verify(orderService, times(1)).returnOrder(1L);
    }

    @ParameterizedTest
    @MethodSource("reservedAndReturnOrderParametrized")
    public void returnOrderShouldReturnErrorParametrized(RuntimeException exception) throws Exception {
        when(orderService.returnOrder(1L)).thenThrow(exception);

        mockMvc.perform(patch("/order/return/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(exception.getMessage()));

        verify(orderService, times(1)).returnOrder(1L);
    }

    private List<OrderResponseDto> getOrderResponseDtoList() {
        return List.of(
                OrderResponseDto.builder()
                        .id(1L)
                        .name("Order#1")
                        .books(List.of(BookResponseDto.builder().build()))
                        .client(ClientResponseDto.builder().build())
                        .status("CREATED")
                        .build(),
                OrderResponseDto.builder()
                        .id(2L)
                        .books(List.of(BookResponseDto.builder().id("1").build()))
                        .client(ClientResponseDto.builder().build())
                        .status("RESERVED")
                        .build()
        );
    }

    private OrderRequestDto getOrderRequestDto() {
        return OrderRequestDto.builder()
                .name("Order#1")
                .build();
    }

}
