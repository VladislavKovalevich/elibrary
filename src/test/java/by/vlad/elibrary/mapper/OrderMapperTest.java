package by.vlad.elibrary.mapper;

import by.vlad.elibrary.model.dto.request.OrderRequestDto;
import by.vlad.elibrary.model.dto.response.BookResponseDto;
import by.vlad.elibrary.model.dto.response.ClientResponseDto;
import by.vlad.elibrary.model.dto.response.OrderResponseDto;
import by.vlad.elibrary.model.entity.Author;
import by.vlad.elibrary.model.entity.Book;
import by.vlad.elibrary.model.entity.Client;
import by.vlad.elibrary.model.entity.Genre;
import by.vlad.elibrary.model.entity.Order;
import by.vlad.elibrary.model.entity.OrderStatus;
import by.vlad.elibrary.model.entity.Publisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderMapperTest {

    @InjectMocks
    private OrderMapperImpl orderMapper;

    @Mock
    private BookMapperImpl bookMapper;

    @Mock
    private ClientMapperImpl clientMapper;

    private final OrderResponseDto expectedOrderResponseDto;

    private final Order expectedOrder;

    private final OrderRequestDto expectedOrderRequestDto;

    private final List<Order> expectedOrders;

    private final List<OrderResponseDto> expectedOrderResponseDtos;

    public OrderMapperTest() {
        expectedOrder = Order.builder()
                .id(1L)
                .status(OrderStatus.CREATED)
                .name("order")
                .client(Client.builder()
                        .id(1L)
                        .name("name")
                        .surname("surname")
                        .build())
                .books(List.of(Book.builder()
                        .id(1L)
                        .title("title")
                        .description("description")
                        .copiesNumber(23)
                        .numberOfPages(234)
                        .releaseYear(Year.of(1923))
                        .author(Author.builder()
                                .name("name")
                                .build())
                        .publisher(Publisher.builder()
                                .name("name")
                                .build())
                        .genre(Genre.builder()
                                .name("name")
                                .build())
                        .build()))
                .build();

        expectedOrderResponseDto = OrderResponseDto.builder()
                .id(1L)
                .name("order")
                .status(String.valueOf(OrderStatus.CREATED))
                .client(ClientResponseDto.builder()
                        .name("name")
                        .surname("surname")
                        .build())
                .books(List.of(BookResponseDto.builder()
                        .id("1")
                        .title("title")
                        .description("description")
                        .copiesNumber("23")
                        .numberOfPages("234")
                        .releaseYear("1923")
                        .author("name")
                        .publisher("name")
                        .genre("name")
                        .build()))
                .build();

        expectedOrderRequestDto = OrderRequestDto.builder()
                .id(1L)
                .bookId(1L)
                .name("order")
                .orderStatus(String.valueOf(OrderStatus.CREATED))
                .build();

        expectedOrders = List.of(expectedOrder);

        expectedOrderResponseDtos = List.of(expectedOrderResponseDto);
    }

    @Test
    public void fromEntityToDtoShouldReturnValidDto(){
        when(bookMapper.fromBooksToDtos(expectedOrder.getBooks()))
                .thenReturn(expectedOrderResponseDto.getBooks());
        when(clientMapper.fromEntityToDto(expectedOrder.getClient()))
                .thenReturn(expectedOrderResponseDto.getClient());

        OrderResponseDto actualOrderResponseDto = orderMapper.fromEntityToDto(expectedOrder);

        assertThat(actualOrderResponseDto).isEqualTo(expectedOrderResponseDto);

        verify(clientMapper, times(1)).fromEntityToDto(expectedOrder.getClient());
        verify(bookMapper, times(1)).fromBooksToDtos(expectedOrder.getBooks());
    }

    @Test
    public void fromEntityToDtoShouldReturnNullIfEntityIsNull(){
        OrderResponseDto actualOrderResponseDto = orderMapper.fromEntityToDto(null);

        assertThat(actualOrderResponseDto).isNull();
    }

    @Test
    public void fromDtoToEntityShouldReturnValidEntity(){
        Order actualOrder = orderMapper.fromDtoToEntity(expectedOrderRequestDto);

        assertThat(actualOrder.getId()).isEqualTo(expectedOrder.getId());
        assertThat(actualOrder.getName()).isEqualTo(expectedOrder.getName());
    }

    @Test
    public void fromDtoToEntityShouldReturnNullIfDtoIsNull(){
        Order actualOrder = orderMapper.fromDtoToEntity(null);

        assertThat(actualOrder).isNull();
    }

    @Test
    public void fromEntitiesToDtosShouldReturnValidList(){
        when(bookMapper.fromBooksToDtos(expectedOrder.getBooks()))
                .thenReturn(expectedOrderResponseDto.getBooks());
        when(clientMapper.fromEntityToDto(expectedOrder.getClient()))
                .thenReturn(expectedOrderResponseDto.getClient());

        List<OrderResponseDto> actualOrderResponseDtos = orderMapper.fromEntitiesToDtos(expectedOrders);

        assertThat(actualOrderResponseDtos).isEqualTo(expectedOrderResponseDtos);

        verify(clientMapper, times(1)).fromEntityToDto(expectedOrder.getClient());
        verify(bookMapper, times(1)).fromBooksToDtos(expectedOrder.getBooks());
    }

    @Test
    public void fromEntitiesToDtosShouldReturnNullIfListIsNull(){
        List<OrderResponseDto> actualOrderResponseDtos = orderMapper.fromEntitiesToDtos(null);

        assertThat(actualOrderResponseDtos).isNull();
    }
}
