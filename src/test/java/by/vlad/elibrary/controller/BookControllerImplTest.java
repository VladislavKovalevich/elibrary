package by.vlad.elibrary.controller;

import by.vlad.elibrary.config.security.JwtService;
import by.vlad.elibrary.controller.impl.BookControllerImpl;
import by.vlad.elibrary.exception.NotFoundException;
import by.vlad.elibrary.exception.handler.RestExceptionHandler;
import by.vlad.elibrary.model.dto.request.BookDataRequestDto;
import by.vlad.elibrary.model.dto.response.BookResponseDto;
import by.vlad.elibrary.service.BookService;
import by.vlad.elibrary.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static by.vlad.elibrary.exception.util.ExceptionMessage.AUTHOR;
import static by.vlad.elibrary.exception.util.ExceptionMessage.BOOK_NOT_FOUND;
import static by.vlad.elibrary.exception.util.ExceptionMessage.GENRE;
import static by.vlad.elibrary.exception.util.ExceptionMessage.PUBLISHER;
import static by.vlad.elibrary.exception.util.ExceptionMessage._NOT_FOUND;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookControllerImpl.class)
public class BookControllerImplTest {

    @Autowired
    private BookController bookController;

    @MockBean
    private BookService bookService;

    @MockBean
    private ClientService clientService;

    @MockBean
    private JwtService jwtService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                .setControllerAdvice(new RestExceptionHandler())
                .build();
    }

    @Test
    public void findAllShouldReturnAllBooks() throws Exception {
        when(bookService.returnBooks()).thenReturn(getBooks());

        mockMvc.perform(get("/book/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(bookService, times(1)).returnBooks();
    }

    @Test
    public void findByIdShouldReturnValidBook() throws Exception {
        when(bookService.returnBookById(1L)).thenReturn(getBooks().get(0));

        mockMvc.perform(get("/book/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Test title #1"))
                .andExpect(jsonPath("$.copiesNumber").value("112"))
                .andExpect(jsonPath("$.numberOfPages").value("532"))
                .andExpect(jsonPath("$.releaseYear").value("1923"));

        verify(bookService, times(1)).returnBookById(1L);
    }

    @Test
    public void findByIdShouldReturnErrorMessageWhenBookIdIsInvalid() throws Exception {
        when(bookService.returnBookById(1L))
                .thenThrow(new NotFoundException(BOOK_NOT_FOUND));

        mockMvc.perform(get("/book/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(BOOK_NOT_FOUND));

        verify(bookService, times(1)).returnBookById(1L);
    }

    @Test
    public void createNewBookShouldReturnValidBook() throws Exception {
        when(bookService.createNewBook(getNewBook())).thenReturn(getBooks().get(0));

        mockMvc.perform(post("/book/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(getNewBook())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Test title #1"))
                .andExpect(jsonPath("$.copiesNumber").value("112"))
                .andExpect(jsonPath("$.numberOfPages").value("532"))
                .andExpect(jsonPath("$.releaseYear").value("1923"));

        verify(bookService, times(1)).createNewBook(getNewBook());
    }

    @Test
    public void createNewBookShouldReturnErrorIfAuthorIsNotFound() throws Exception {
        when(bookService.createNewBook(getNewBook()))
                .thenThrow(new NotFoundException(AUTHOR + _NOT_FOUND));

        mockMvc.perform(post("/book/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(getNewBook())))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(AUTHOR + _NOT_FOUND));

        verify(bookService, times(1)).createNewBook(getNewBook());
    }

    @Test
    public void createNewBookShouldReturnErrorIfPublisherIsNotFound() throws Exception {
        when(bookService.createNewBook(getNewBook()))
                .thenThrow(new NotFoundException(PUBLISHER + _NOT_FOUND));

        mockMvc.perform(post("/book/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(getNewBook())))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(PUBLISHER + _NOT_FOUND));

        verify(bookService, times(1)).createNewBook(getNewBook());
    }

    @Test
    public void createNewBookShouldReturnErrorIfGenreIsNotFound() throws Exception {
        when(bookService.createNewBook(getNewBook()))
                .thenThrow(new NotFoundException(GENRE + _NOT_FOUND));

        mockMvc.perform(post("/book/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(getNewBook())))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(GENRE + _NOT_FOUND));

        verify(bookService, times(1)).createNewBook(getNewBook());
    }

    @Test
    public void updateBookShouldReturnValidBook() throws Exception {
        when(bookService.updateBook(getNewBook())).thenReturn(getBooks().get(0));

        mockMvc.perform(put("/book/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(getNewBook())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Test title #1"))
                .andExpect(jsonPath("$.copiesNumber").value("112"))
                .andExpect(jsonPath("$.numberOfPages").value("532"))
                .andExpect(jsonPath("$.releaseYear").value("1923"));

        verify(bookService, times(1)).updateBook(getNewBook());
    }

    @Test
    public void updateBookShouldReturnErrorIfBookNotFound() throws Exception {
        when(bookService.updateBook(getNewBook()))
                .thenThrow(new NotFoundException(BOOK_NOT_FOUND));

        mockMvc.perform(put("/book/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(getNewBook())))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(BOOK_NOT_FOUND));

        verify(bookService, times(1)).updateBook(getNewBook());
    }


    private List<BookResponseDto> getBooks() {

        return List.of(
                BookResponseDto.builder()
                        .id("1")
                        .title("Test title #1")
                        .copiesNumber("112")
                        .numberOfPages("532")
                        .releaseYear("1923")
                        .build(),
                BookResponseDto.builder()
                        .id("2")
                        .title("Test title #2")
                        .copiesNumber("119")
                        .numberOfPages("433")
                        .releaseYear("2011")
                        .build()
        );
    }

    private BookDataRequestDto getNewBook() {
        return BookDataRequestDto.builder()
                .title("Test title #1")
                .copiesNumber("112")
                .numberOfPages("532")
                .releaseYear("1923")
                .publisherId(1L)
                .authorId(1L)
                .genreId(1L)
                .build();
    }
}
