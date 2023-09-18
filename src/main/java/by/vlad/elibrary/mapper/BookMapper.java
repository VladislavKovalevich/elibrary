package by.vlad.elibrary.mapper;

import by.vlad.elibrary.model.dto.request.BookDataRequestDto;
import by.vlad.elibrary.model.dto.response.BookResponseDto;
import by.vlad.elibrary.model.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.time.Year;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class BookMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "author", source = "book.author.name")
    @Mapping(target = "genre", source = "book.genre.name")
    @Mapping(target = "publisher", source = "book.publisher.name")
    @Mapping(target = "releaseYear", source = "releaseYear", qualifiedByName = "convertYear")
    public abstract BookResponseDto fromBookToDto(Book book);

    public abstract List<BookResponseDto> fromBooksToDtos(List<Book> books);

    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "author.id", source = "authorId")
    @Mapping(target = "genre.id", source = "genreId")
    @Mapping(target = "publisher.id", source = "publisherId")
    @Mapping(target = "releaseYear", source = "releaseYear", qualifiedByName = "convertToYear")
    public abstract Book fromDtoToBook(BookDataRequestDto dto);

    @Named("convertYear")
    public String convertYear(Year year){
        return year.toString();
    }

    @Named("convertToYear")
    public Year convertToYear(String year){
        return Year.parse(year); //бросает исключение в случае ошибки парсинга
    }
}
