package by.vlad.elibrary.controller;

import by.vlad.elibrary.config.security.JwtService;
import by.vlad.elibrary.controller.impl.GenreControllerImpl;
import by.vlad.elibrary.exception.InvalidRequestDataException;
import by.vlad.elibrary.exception.handler.RestExceptionHandler;
import by.vlad.elibrary.model.dto.request.GenreRequestDto;
import by.vlad.elibrary.model.dto.request.PublisherRequestDto;
import by.vlad.elibrary.model.dto.response.GenreResponseDto;
import by.vlad.elibrary.service.ClientService;
import by.vlad.elibrary.service.impl.GenreServiceImpl;
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

import static by.vlad.elibrary.exception.util.ExceptionMessage.GENRE_NOT_FOUND;
import static by.vlad.elibrary.exception.util.ExceptionMessage.PUBLISHER_NOT_FOUND;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GenreControllerImpl.class)
public class GenreControllerImplTest {

    @Autowired
    private GenreControllerImpl genreController;

    @MockBean
    private GenreServiceImpl genreService;

    @MockBean
    private ClientService clientService;

    @MockBean
    private JwtService jwtService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(genreController)
                .setControllerAdvice(new RestExceptionHandler())
                .build();
    }

    @Test
    public void returnAllPublishersShouldReturnValidList() throws Exception {
        when(genreService.returnComponentList()).thenReturn(getGenreResponseDtos());

        mockMvc.perform(get("/genre/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(genreService, times(1)).returnComponentList();
    }

    @Test
    public void returnPublisherByIdShouldReturnValidPublisher() throws Exception {
        when(genreService.returnComponentById(1L)).thenReturn(getGenreResponseDtos().get(0));

        mockMvc.perform(get("/genre/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("genre1"));

        verify(genreService, times(1)).returnComponentById(1L);
    }

    @Test
    public void returnPublisherByIdShouldReturnErrorIfPublisherNotFound() throws Exception {
        when(genreService.returnComponentById(1L))
                .thenThrow(new InvalidRequestDataException(GENRE_NOT_FOUND));

        mockMvc.perform(get("/genre/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(GENRE_NOT_FOUND));

        verify(genreService, times(1)).returnComponentById(1L);
    }

    @Test
    public void createNewPublisherShouldReturnValidPublisher() throws Exception {
        GenreRequestDto dto = getGenreRequestDto();

        when(genreService.createNewComponent(dto)).thenReturn(getGenreResponseDtos().get(1));

        mockMvc.perform(post("/genre/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(dto.getName()));

        verify(genreService, times(1)).createNewComponent(dto);
    }

    @Test
    public void updatePublisherShouldReturnValidPublisher() throws Exception{
        GenreRequestDto dto = getGenreRequestDto();

        when(genreService.updateComponent(dto)).thenReturn(getGenreResponseDtos().get(1));

        mockMvc.perform(put("/genre/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(dto.getName()));

        verify(genreService, times(1)).updateComponent(dto);
    }

    @Test
    public void updatePublisherShouldReturnErrorIfPublisherNotFound() throws Exception{
        GenreRequestDto dto = getGenreRequestDto();

        when(genreService.updateComponent(dto))
                .thenThrow(new InvalidRequestDataException(GENRE_NOT_FOUND));

        mockMvc.perform(put("/genre/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(GENRE_NOT_FOUND));

        verify(genreService, times(1)).updateComponent(dto);
    }

    private List<GenreResponseDto> getGenreResponseDtos() {
        return List.of(
                GenreResponseDto.builder()
                        .name("genre1")
                        .build(),
                GenreResponseDto.builder()
                        .name("genre2")
                        .build()
        );
    }

    private GenreRequestDto getGenreRequestDto() {
        return GenreRequestDto.builder()
                .name("genre2")
                .build();
    }
}
