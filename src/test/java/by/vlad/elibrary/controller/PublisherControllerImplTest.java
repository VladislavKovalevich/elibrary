package by.vlad.elibrary.controller;

import by.vlad.elibrary.config.security.JwtService;
import by.vlad.elibrary.controller.impl.PublisherControllerImpl;
import by.vlad.elibrary.exception.InvalidRequestDataException;
import by.vlad.elibrary.exception.handler.RestExceptionHandler;
import by.vlad.elibrary.model.dto.request.PublisherRequestDto;
import by.vlad.elibrary.model.dto.response.PublisherResponseDto;
import by.vlad.elibrary.service.ClientService;
import by.vlad.elibrary.service.impl.PublisherServiceImpl;
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

@WebMvcTest(controllers = PublisherControllerImpl.class)
public class PublisherControllerImplTest {

    @Autowired
    private PublisherControllerImpl publisherController;

    @MockBean
    private PublisherServiceImpl publisherService;

    @MockBean
    private ClientService clientService;

    @MockBean
    private JwtService jwtService;

    private static MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(publisherController)
                .setControllerAdvice(new RestExceptionHandler())
                .build();
    }

    @Test
    public void returnAllPublishersShouldReturnValidList() throws Exception {
        when(publisherService.returnComponentList()).thenReturn(getPublisherResponseDtos());

        mockMvc.perform(get("/publisher/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(publisherService, times(1)).returnComponentList();
    }

    @Test
    public void returnPublisherByIdShouldReturnValidPublisher() throws Exception {
        when(publisherService.returnComponentById(1L)).thenReturn(getPublisherResponseDtos().get(0));

        mockMvc.perform(get("/publisher/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("publisher1"))
                .andExpect(jsonPath("$.address").value("address1"));

        verify(publisherService, times(1)).returnComponentById(1L);
    }

    @Test
    public void returnPublisherByIdShouldReturnErrorIfPublisherNotFound() throws Exception {
        when(publisherService.returnComponentById(1L))
                .thenThrow(new InvalidRequestDataException(PUBLISHER_NOT_FOUND));

        mockMvc.perform(get("/publisher/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(PUBLISHER_NOT_FOUND));

        verify(publisherService, times(1)).returnComponentById(1L);
    }

    @Test
    public void createNewPublisherShouldReturnValidPublisher() throws Exception {
        PublisherRequestDto dto = getPublisherRequestDto();

        when(publisherService.createNewComponent(dto)).thenReturn(getPublisherResponseDtos().get(1));

        mockMvc.perform(post("/publisher/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(dto.getName()))
                .andExpect(jsonPath("$.address").value(dto.getAddress()));

        verify(publisherService, times(1)).createNewComponent(dto);
    }

    @Test
    public void updatePublisherShouldReturnValidPublisher() throws Exception{
        PublisherRequestDto dto = getPublisherRequestDto();

        when(publisherService.updateComponent(dto)).thenReturn(getPublisherResponseDtos().get(1));

        mockMvc.perform(put("/publisher/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(dto.getName()))
                .andExpect(jsonPath("$.address").value(dto.getAddress()));

        verify(publisherService, times(1)).updateComponent(dto);
    }

    @Test
    public void updatePublisherShouldReturnErrorIfPublisherNotFound() throws Exception{
        PublisherRequestDto dto = getPublisherRequestDto();

        when(publisherService.updateComponent(dto))
                .thenThrow(new InvalidRequestDataException(PUBLISHER_NOT_FOUND));

        mockMvc.perform(put("/publisher/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(PUBLISHER_NOT_FOUND));

        verify(publisherService, times(1)).updateComponent(dto);
    }

    private List<PublisherResponseDto> getPublisherResponseDtos() {
        return List.of(
                PublisherResponseDto.builder()
                        .name("publisher1")
                        .address("address1")
                        .build(),
                PublisherResponseDto.builder()
                        .name("publisher2")
                        .address("address2")
                        .build()
        );
    }

    private PublisherRequestDto getPublisherRequestDto() {
        return PublisherRequestDto.builder()
                .name("publisher2")
                .address("address2")
                .build();
    }
}
