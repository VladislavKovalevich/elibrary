package by.vlad.elibrary.controller;

import by.vlad.elibrary.config.security.JwtService;
import by.vlad.elibrary.controller.impl.AuthorControllerImpl;
import by.vlad.elibrary.exception.InvalidRequestDataException;
import by.vlad.elibrary.exception.handler.RestExceptionHandler;
import by.vlad.elibrary.model.dto.request.AuthorRequestDto;
import by.vlad.elibrary.model.dto.response.AuthorResponseDto;
import by.vlad.elibrary.service.ClientService;
import by.vlad.elibrary.service.impl.AuthorServiceImpl;
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

import static by.vlad.elibrary.exception.util.ExceptionMessage.AUTHOR_NOT_FOUND;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthorControllerImpl.class)
public class AuthorControllerImplTest {

    @Autowired
    private AuthorControllerImpl authorController;

    @MockBean
    private AuthorServiceImpl authorService;

    @MockBean
    private ClientService clientService;

    @MockBean
    private JwtService jwtService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authorController)
                .setControllerAdvice(new RestExceptionHandler())
                .build();
    }

    @Test
    public void returnAuthorsShouldReturnList() throws Exception {
        when(authorService.returnComponentList()).thenReturn(getAuthorResponseDto());

        mockMvc.perform(get("/author/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(authorService, times(1)).returnComponentList();
    }

    @Test
    public void returnAuthorByIdShouldReturnValidAuthor() throws Exception {
        when(authorService.returnComponentById(1L)).thenReturn(getAuthorResponseDto().get(0));

        mockMvc.perform(get("/author/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Name1"))
                .andExpect(jsonPath("$.surname").value("Surname1"));

        verify(authorService, times(1)).returnComponentById(1L);
    }

    @Test
    public void returnAuthorByIdShouldReturnErrorIfAuthorNotExists() throws Exception {
        when(authorService.returnComponentById(1L)).thenThrow(new InvalidRequestDataException(AUTHOR_NOT_FOUND));

        mockMvc.perform(get("/author/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(AUTHOR_NOT_FOUND));

        verify(authorService, times(1)).returnComponentById(1L);
    }

    @Test
    public void createNewAuthorShouldReturnValidAuthor() throws Exception {
        AuthorRequestDto dto = getAuthorRequestDto();

        when(authorService.createNewComponent(dto)).thenReturn(getAuthorResponseDto().get(1));

        mockMvc.perform(post("/author/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Name2"))
                .andExpect(jsonPath("$.surname").value("Surname2"));

        verify(authorService, times(1)).createNewComponent(dto);
    }

    @Test
    public void updateAuthorShouldReturnValidAuthor() throws Exception {
        AuthorRequestDto dto = getAuthorRequestDto();

        when(authorService.updateComponent(dto)).thenReturn(getAuthorResponseDto().get(1));

        mockMvc.perform(put("/author/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Name2"))
                .andExpect(jsonPath("$.surname").value("Surname2"));

        verify(authorService, times(1)).updateComponent(dto);
    }

    @Test
    public void updateAuthorShouldReturnErrorAuthorNotExists() throws Exception {
        AuthorRequestDto dto = getAuthorRequestDto();

        when(authorService.updateComponent(dto)).thenThrow(new InvalidRequestDataException(AUTHOR_NOT_FOUND));

        mockMvc.perform(put("/author/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(AUTHOR_NOT_FOUND));

        verify(authorService, times(1)).updateComponent(dto);
    }

    private List<AuthorResponseDto> getAuthorResponseDto() {
        return List.of(
                AuthorResponseDto.builder()
                        .id(1L)
                        .name("Name1")
                        .surname("Surname1")
                        .build(),
                AuthorResponseDto.builder()
                        .id(2L)
                        .name("Name2")
                        .surname("Surname2")
                        .build()
        );
    }

    private AuthorRequestDto getAuthorRequestDto(){
        return AuthorRequestDto.builder()
                .name("Name2")
                .surname("Surname2")
                .build();
    }
}
