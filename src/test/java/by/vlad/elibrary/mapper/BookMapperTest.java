package by.vlad.elibrary.mapper;

import by.vlad.elibrary.model.dto.request.BookDataRequestDto;
import by.vlad.elibrary.model.dto.response.BookResponseDto;
import by.vlad.elibrary.model.entity.Author;
import by.vlad.elibrary.model.entity.Book;
import by.vlad.elibrary.model.entity.Genre;
import by.vlad.elibrary.model.entity.Publisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class BookMapperTest {

    @InjectMocks
    private BookMapperImpl bookMapper;

    private final Book expectedBook;

    private final BookResponseDto expectedBookResponseDto;

    private final BookDataRequestDto expectedBookDataRequestDto;

    private final List<Book> expectedBookList;

    private final List<BookResponseDto> expectedBookResponseDtos;

    public BookMapperTest(){
        expectedBook = Book.builder()
                .id(1L)
                .title("title")
                .description("description")
                .copiesNumber(23)
                .releaseYear(Year.of(1923))
                .numberOfPages(234)
                .author(Author.builder().id(1L).name("name").build())
                .genre(Genre.builder().id(1L).name("name").build())
                .publisher(Publisher.builder().id(1L).name("name").build())
                .build();

        expectedBookResponseDto = BookResponseDto.builder()
                .id("1")
                .title("title")
                .description("description")
                .copiesNumber("23")
                .numberOfPages("234")
                .releaseYear("1923")
                .author("name")
                .publisher("name")
                .genre("name")
                .build();

        expectedBookDataRequestDto = BookDataRequestDto.builder()
                .id(1L)
                .title("title")
                .description("description")
                .copiesNumber("23")
                .numberOfPages("234")
                .releaseYear("1923")
                .authorId(1L)
                .genreId(1L)
                .publisherId(1L)
                .build();

        expectedBookList = List.of(expectedBook);

        expectedBookResponseDtos = List.of(expectedBookResponseDto);
    }

    @Test
    public void fromBookToDtoShouldReturnValidDto(){
        BookResponseDto actualResponseDto = bookMapper.fromBookToDto(expectedBook);

        assertThat(actualResponseDto).isEqualTo(expectedBookResponseDto);
    }

    @Test
    public void fromBookToDtoShouldReturnNullIfEntityNull(){
        BookResponseDto actualResponseDto = bookMapper.fromBookToDto(null);

        assertThat(actualResponseDto).isNull();
    }

    @Test
    public void fromBooksToDtosShouldReturnValidList(){
        List<BookResponseDto> actualBookResponseDtos = bookMapper.fromBooksToDtos(expectedBookList);

        assertThat(actualBookResponseDtos).isEqualTo(expectedBookResponseDtos);
    }

    @Test
    public void fromBooksToDtosShouldReturnNullIfListIsNull(){
        List<BookResponseDto> actualBookResponseDtos = bookMapper.fromBooksToDtos(null);

        assertThat(actualBookResponseDtos).isNull();
    }

    @Test
    public void fromDtoToBookShouldReturnValidEntity(){
        Book actualBook = bookMapper.fromDtoToBook(expectedBookDataRequestDto);

        assertThat(actualBook.getId()).isEqualTo(expectedBook.getId());
        assertThat(actualBook.getTitle()).isEqualTo(expectedBook.getTitle());
        assertThat(actualBook.getDescription()).isEqualTo(expectedBook.getDescription());
        assertThat(actualBook.getCopiesNumber()).isEqualTo(expectedBook.getCopiesNumber());
        assertThat(actualBook.getNumberOfPages()).isEqualTo(expectedBook.getNumberOfPages());
        assertThat(actualBook.getReleaseYear()).isEqualTo(expectedBook.getReleaseYear());
        assertThat(actualBook.getAuthor().getId()).isEqualTo(expectedBook.getAuthor().getId());
        assertThat(actualBook.getGenre().getId()).isEqualTo(expectedBook.getGenre().getId());
        assertThat(actualBook.getPublisher().getId()).isEqualTo(expectedBook.getPublisher().getId());
    }

    @Test
    public void fromDtoToBookShouldReturnNullIfDtoIsNull(){
        Book actualBook = bookMapper.fromDtoToBook(null);

        assertThat(actualBook).isNull();
    }
}
