package by.vlad.elibrary.mapper;

import by.vlad.elibrary.model.dto.request.OrderRequestDto;
import by.vlad.elibrary.model.dto.response.BookResponseDto;
import by.vlad.elibrary.model.dto.response.ClientResponseDto;
import by.vlad.elibrary.model.dto.response.OrderResponseDto;
import by.vlad.elibrary.model.entity.Book;
import by.vlad.elibrary.model.entity.Client;
import by.vlad.elibrary.model.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        imports = {BookMapper.class, ClientMapper.class})
public abstract class OrderMapper {

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private BookMapper bookMapper;

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "books", source = "books", qualifiedByName = "getBookResponseDtos")
    @Mapping(target = "client", source = "client", qualifiedByName = "getClientResponseDto")
    public abstract OrderResponseDto fromEntityToDto(Order order);

    public abstract List<OrderResponseDto> fromEntitiesToDtos(List<Order> orders);

    public abstract Order fromDtoToEntity(OrderRequestDto dto);

    @Named("getClientResponseDto")
    public ClientResponseDto getClientResponseDto(Client client) {
        return clientMapper.fromEntityToDto(client);
    }

    @Named("getBookResponseDtos")
    public List<BookResponseDto> getBookResponseDtos(List<Book> books) {
        return bookMapper.fromBooksToDtos(books);
    }

}
