package by.vlad.elibrary.controller;

import by.vlad.elibrary.config.security.JwtService;
import by.vlad.elibrary.controller.impl.BookControllerImpl;
import by.vlad.elibrary.model.dto.request.BookDataRequestDto;
import by.vlad.elibrary.model.dto.response.BookResponseDto;
import by.vlad.elibrary.service.BookService;
import by.vlad.elibrary.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(BookControllerImpl.class)
@WithMockUser(authorities = {"ADMIN"}, username = "admin")
public class BookControllerImplTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private ClientService clientService;

    @MockBean
    private JwtService jwtService;


    @Test
    public void findAllShouldReturnAllBooks() throws Exception {
        Mockito.when(bookService.returnBooks()).thenReturn(getBooks());

        mockMvc.perform(get("/book/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void findByIdShouldReturnValidBook() throws Exception {
        Mockito.when(bookService.returnBookById(1L)).thenReturn(getBooks().get(0));

        mockMvc.perform(get("/book/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Test title #1"))
                .andExpect(jsonPath("$.copiesNumber").value("112"))
                .andExpect(jsonPath("$.numberOfPages").value("532"))
                .andExpect(jsonPath("$.releaseYear").value("1923"));
    }

    @Test
    public void createNewBookShouldReturnValidBook() throws Exception {
        Mockito.when(bookService.createNewBook(getNewBook())).thenReturn(getBooks().get(0));

        mockMvc.perform(post("/book/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(getNewBook())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Test title #1"))
                .andExpect(jsonPath("$.copiesNumber").value("112"))
                .andExpect(jsonPath("$.numberOfPages").value("532"))
                .andExpect(jsonPath("$.releaseYear").value("1923"));
    }

    @Test
    public void updateBookShouldReturnValidBook() throws Exception {
        Mockito.when(bookService.updateBook(getNewBook())).thenReturn(getBooks().get(0));

        mockMvc.perform(put("/book/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(getNewBook())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Test title #1"))
                .andExpect(jsonPath("$.copiesNumber").value("112"))
                .andExpect(jsonPath("$.numberOfPages").value("532"))
                .andExpect(jsonPath("$.releaseYear").value("1923"));
    }


    private List<BookResponseDto> getBooks(){

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

    private BookDataRequestDto getNewBook(){
        return BookDataRequestDto.builder()
                .title("Test title #1")
                .copiesNumber("112")
                .numberOfPages("532")
                .releaseYear("1923")
                .build();
    }
}
