package by.vlad.elibrary.service;

import by.vlad.elibrary.exception.NotFoundException;
import by.vlad.elibrary.mapper.BookMapper;
import by.vlad.elibrary.model.dto.request.BookDataRequestDto;
import by.vlad.elibrary.model.dto.response.BookResponseDto;
import by.vlad.elibrary.model.entity.Book;
import by.vlad.elibrary.repository.AuthorRepository;
import by.vlad.elibrary.repository.BookRepository;
import by.vlad.elibrary.repository.GenreRepository;
import by.vlad.elibrary.repository.PublisherRepository;
import by.vlad.elibrary.service.impl.BookServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static by.vlad.elibrary.exception.util.ExceptionMessage.AUTHOR;
import static by.vlad.elibrary.exception.util.ExceptionMessage.BOOK_NOT_FOUND;
import static by.vlad.elibrary.exception.util.ExceptionMessage.GENRE;
import static by.vlad.elibrary.exception.util.ExceptionMessage.PUBLISHER;
import static by.vlad.elibrary.exception.util.ExceptionMessage._NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private BookMapper bookMapper;

    @Test
    public void returnBookByIdShouldReturnValidBook(){
        when(bookRepository.findById(1L)).thenReturn(Optional.of(getBook()));
        when(bookMapper.fromBookToDto(any(Book.class))).thenReturn(getBookResponseDto());

        BookResponseDto bookResponseDto = bookService.returnBookById(1L);

        assertThat(bookResponseDto.getTitle()).isEqualTo(getBookResponseDto().getTitle());
        assertThat(bookResponseDto.getDescription()).isEqualTo(getBookResponseDto().getDescription());

        verify(bookRepository, times(1)).findById(1L);
        verify(bookMapper, times(1)).fromBookToDto(any(Book.class));
    }

    @Test
    public void returnBookByIdShouldReturnErrorIfIdIsInvalid(){
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.returnBookById(1L), BOOK_NOT_FOUND);

        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    public void returnBooksShouldReturnBookList(){
        when(bookRepository.findAll()).thenReturn(List.of(getBook()));
        when(bookMapper.fromBooksToDtos(anyList())).thenReturn(List.of(getBookResponseDto()));

        List<BookResponseDto> books = bookService.returnBooks();

        assertThat(books).isNotEmpty();

        verify(bookRepository, times(1)).findAll();
        verify(bookMapper, times(1)).fromBooksToDtos(anyList());
    }

    @Test
    public void createNewBookShouldReturnNewBook(){
        BookDataRequestDto dto = getBookRequestDto();
        Book book = getBook();
        Book savedBook = getSavedBook();

        when(genreRepository.existsById(dto.getGenreId())).thenReturn(true);
        when(publisherRepository.existsById(dto.getPublisherId())).thenReturn(true);
        when(authorRepository.existsById(dto.getAuthorId())).thenReturn(true);

        when(bookMapper.fromDtoToBook(dto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.fromBookToDto(savedBook)).thenReturn(getBookResponseDto());

        BookResponseDto actualDto = bookService.createNewBook(dto);

        assertThat(actualDto.getTitle()).isEqualTo(dto.getTitle());
        assertThat(actualDto.getDescription()).isEqualTo(dto.getDescription());

        verify(bookMapper, times(1)).fromDtoToBook(dto);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).fromBookToDto(savedBook);

        verify(genreRepository, times(1)).existsById(dto.getGenreId());
        verify(publisherRepository, times(1)).existsById(dto.getPublisherId());
        verify(authorRepository, times(1)).existsById(dto.getAuthorId());
    }

    @ParameterizedTest
    @MethodSource("provideParams")
    public void createNewBookShouldReturnErrorIfComponentNotExists(boolean genre, boolean publisher,
                                                                   boolean author, String expectedErrorMsg){
        BookDataRequestDto dto = getBookRequestDto();

        when(genreRepository.existsById(dto.getGenreId())).thenReturn(genre);
        when(publisherRepository.existsById(dto.getPublisherId())).thenReturn(publisher);
        when(authorRepository.existsById(dto.getAuthorId())).thenReturn(author);

        assertThrows(NotFoundException.class, () -> bookService.createNewBook(dto), expectedErrorMsg);

        verify(genreRepository, times(1)).existsById(dto.getGenreId());
        verify(publisherRepository, times(1)).existsById(dto.getPublisherId());
        verify(authorRepository, times(1)).existsById(dto.getAuthorId());
    }

    private static Stream<Arguments> provideParams(){
        return Stream.of(
                Arguments.of(false, true, true, GENRE + _NOT_FOUND),
                Arguments.of(true, false, true, PUBLISHER + _NOT_FOUND),
                Arguments.of(true, true, false, AUTHOR + _NOT_FOUND)
        );
    }

    private BookDataRequestDto getBookRequestDto(){
        return BookDataRequestDto.builder()
                .title("Title")
                .description("Description")
                .releaseYear("1991")
                .copiesNumber("23")
                .numberOfPages("123")
                .genreId(1L)
                .authorId(1L)
                .publisherId(1L)
                .build();
    }

    private BookResponseDto getBookResponseDto(){
        return BookResponseDto.builder()
                .title("Title")
                .description("Description")
                .numberOfPages("234")
                .releaseYear("1999")
                .author("author")
                .genre("genre")
                .publisher("publisher")
                .build();
    }

    private Book getBook(){
        return Book.builder()
                .title("title")
                .description("description")
                .numberOfPages(234)
                .releaseYear(Year.of(1999))
                .build();
    }

    private Book getSavedBook(){
        return Book.builder()
                .id(1L)
                .title("title")
                .description("description")
                .numberOfPages(234)
                .releaseYear(Year.of(1999))
                .build();
    }
}
