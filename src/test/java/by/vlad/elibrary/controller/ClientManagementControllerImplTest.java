package by.vlad.elibrary.controller;

import by.vlad.elibrary.config.security.JwtService;
import by.vlad.elibrary.exception.InvalidRequestDataException;
import by.vlad.elibrary.exception.handler.RestExceptionHandler;
import by.vlad.elibrary.model.dto.request.UserLoginDataRequestDto;
import by.vlad.elibrary.model.dto.request.UserRegisterDataRequestDto;
import by.vlad.elibrary.model.entity.Client;
import by.vlad.elibrary.model.entity.Role;
import by.vlad.elibrary.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static by.vlad.elibrary.exception.util.ExceptionMessage.PASSWORDS_MISMATCH;
import static by.vlad.elibrary.exception.util.ExceptionMessage.USER_EMAIL_ALREADY_EXISTS;
import static by.vlad.elibrary.exception.util.ExceptionMessage.WRONG_CREDENTIALS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ClientManagementController.class)
public class ClientManagementControllerImplTest {

    @Autowired
    private ClientManagementController controller;

    @MockBean
    private ClientService clientService;

    @MockBean
    private JwtService jwtService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RestExceptionHandler())
                .build();
    }

    @Test
    public void registerClientShouldReturnNewClient() throws Exception {
        when(clientService.createNewClient(getUserRegisterDto())).thenReturn("OK");

        mockMvc.perform(post("/client/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(getUserRegisterDto())))
                .andExpect(status().isCreated())
                .andExpect(content().string("OK"));

        verify(clientService, times(1)).createNewClient(getUserRegisterDto());
    }


    @Test
    public void registerClientShouldReturnErrorIfEmailAlreadyExists() throws Exception {
        when(clientService.createNewClient(getUserRegisterDto()))
                .thenThrow(new InvalidRequestDataException(USER_EMAIL_ALREADY_EXISTS));

        mockMvc.perform(post("/client/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(getUserRegisterDto())))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(USER_EMAIL_ALREADY_EXISTS));

        verify(clientService, times(1)).createNewClient(getUserRegisterDto());
    }

    @Test
    public void registerClientShouldReturnErrorIfPasswordsMismatch() throws Exception {
        when(clientService.createNewClient(getUserRegisterDto()))
                .thenThrow(new InvalidRequestDataException(PASSWORDS_MISMATCH));

        mockMvc.perform(post("/client/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(getUserRegisterDto())))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(PASSWORDS_MISMATCH));

        verify(clientService, times(1)).createNewClient(getUserRegisterDto());
    }

    @Test
    public void loginClientShouldReturnErrorIfUserNotFound() throws Exception {
        when(clientService.authorizeClient(getUserLoginDto()))
                .thenThrow(new InvalidRequestDataException(WRONG_CREDENTIALS));

        mockMvc.perform(post("/client/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(getUserLoginDto())))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(WRONG_CREDENTIALS));

        verify(clientService, times(1)).authorizeClient(getUserLoginDto());
    }

    @Test
    public void loginClientShouldReturnErrorIfWrongPassword() throws Exception {
        when(clientService.authorizeClient(getUserLoginDto()))
                .thenReturn(false);

        mockMvc.perform(post("/client/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(getUserLoginDto())))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(WRONG_CREDENTIALS));

        verify(clientService, times(1)).authorizeClient(getUserLoginDto());
    }

    @Test
    public void loginClientShouldReturnAcceptToken() throws Exception {
        when(clientService.authorizeClient(getUserLoginDto()))
                .thenReturn(true);

        when(clientService.loadUserByUsername(getUserLoginDto().getEmail()))
                .thenReturn(getUserDetails());

        when(jwtService.generateToken(any(Client.class)))
                .thenReturn("accessTokenADBC123");

        mockMvc.perform(post("/client/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(getUserLoginDto())))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andExpect(header().string("Authorization", "Bearer accessTokenADBC123"));

        verify(clientService, times(1)).authorizeClient(getUserLoginDto());
        verify(clientService, times(1)).loadUserByUsername(getUserLoginDto().getEmail());
        verify(jwtService, times(1)).generateToken(any(Client.class));
    }

    private UserDetails getUserDetails(){
        return Client.builder()
                .role(Role.CLIENT)
                .email("test1@gmail.com")
                .isNonLocked(true)
                .name("Test name")
                .surname("Test surname")
                .password("secret")
                .build();
    }

    private UserLoginDataRequestDto getUserLoginDto() {
        return UserLoginDataRequestDto.builder()
                .email("abcd@gmail.com")
                .password("qwerty123")
                .build();
    }

    private UserRegisterDataRequestDto getUserRegisterDto() {
        return UserRegisterDataRequestDto.builder()
                .name("Vlad")
                .surname("Test surname")
                .email("test1@gmail.com")
                .password("qwerty123")
                .repeatedPassword("qwerty123")
                .build();
    }
}
