package by.vlad.elibrary.service;

import by.vlad.elibrary.exception.InvalidRequestDataException;
import by.vlad.elibrary.mapper.ClientMapper;
import by.vlad.elibrary.model.dto.request.UserLoginDataRequestDto;
import by.vlad.elibrary.model.dto.request.UserRegisterDataRequestDto;
import by.vlad.elibrary.model.entity.Client;
import by.vlad.elibrary.model.entity.Role;
import by.vlad.elibrary.repository.ClientRepository;
import by.vlad.elibrary.repository.OrderRepository;
import by.vlad.elibrary.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static by.vlad.elibrary.exception.util.ExceptionMessage.PASSWORDS_MISMATCH;
import static by.vlad.elibrary.exception.util.ExceptionMessage.USER_EMAIL_ALREADY_EXISTS;
import static by.vlad.elibrary.exception.util.ExceptionMessage.WRONG_CREDENTIALS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClientServiceImplTest {

    @InjectMocks
    ClientServiceImpl clientService;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private final UserRegisterDataRequestDto userRegisterDto;

    private final UserRegisterDataRequestDto userRegisterDtoWithWrongPas;

    private final UserLoginDataRequestDto userLoginDto;

    private final Client client;

    private final Client savedClient;

    private final Client loginClient;

    public ClientServiceImplTest() {
        userRegisterDto = UserRegisterDataRequestDto.builder()
                .email("test1user@gmail.com")
                .name("Name")
                .surname("Surname")
                .password("qwerty123")
                .repeatedPassword("qwerty123")
                .build();

        userRegisterDtoWithWrongPas = UserRegisterDataRequestDto.builder()
                .email("test1user@gmail.com")
                .name("Name")
                .surname("Surname")
                .password("qwerty123")
                .repeatedPassword("qwerty1233")
                .build();

        client = Client.builder()
                .email(userRegisterDto.getEmail())
                .name(userRegisterDto.getName())
                .surname(userRegisterDto.getSurname())
                .build();

        savedClient = Client.builder()
                .id(1L)
                .email(userRegisterDto.getEmail())
                .name(userRegisterDto.getName())
                .surname(userRegisterDto.getSurname())
                .password("12E45FQFD452FFQ331")
                .isNonLocked(true)
                .role(Role.CLIENT)
                .build();

        userLoginDto = UserLoginDataRequestDto.builder()
                .email("test1user@gmail.com")
                .password("qwerty123")
                .build();

        loginClient = Client.builder()
                .id(1L)
                .email(userLoginDto.getEmail())
                .password(userLoginDto.getPassword())
                .build();
    }

    @Test
    public void createNewClientShouldReturnNewClient() {
        when(clientRepository.countByEmail(userRegisterDto.getEmail())).thenReturn(0);
        when(clientMapper.convertUserRegistrationDataRequestDtoToClientEntity(userRegisterDto)).thenReturn(client);
        when(passwordEncoder.encode(null)).thenReturn("12E45FQFD452FFQ331");
        when(clientRepository.save(client)).thenReturn(savedClient);

        String result = clientService.createNewClient(userRegisterDto);

        assertThat(result).isNotEmpty();

        verify(clientRepository, times(1)).countByEmail(userRegisterDto.getEmail());
        verify(clientMapper, times(1)).convertUserRegistrationDataRequestDtoToClientEntity(userRegisterDto);
        verify(passwordEncoder, times(1)).encode(null);
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    public void createNewClientShouldReturnErrorIfEmailIsAlreadyExists() {
        when(clientRepository.countByEmail(userRegisterDto.getEmail())).thenReturn(1);

        assertThrows(InvalidRequestDataException.class, () -> clientService.createNewClient(userRegisterDto), USER_EMAIL_ALREADY_EXISTS);

        verify(clientRepository, times(1)).countByEmail(userRegisterDto.getEmail());
    }

    @Test
    public void createNewClientShouldReturnErrorIfPasswordMismatch() {
        when(clientRepository.countByEmail(userRegisterDtoWithWrongPas.getEmail())).thenReturn(0);

        assertThrows(InvalidRequestDataException.class, () -> clientService.createNewClient(userRegisterDtoWithWrongPas), PASSWORDS_MISMATCH);

        verify(clientRepository, times(1)).countByEmail(userRegisterDtoWithWrongPas.getEmail());
    }

    @Test
    public void authorizeClientShouldReturnTrue() {
        when(clientRepository.findByEmail(userLoginDto.getEmail())).thenReturn(Optional.of(loginClient));
        when(passwordEncoder.matches(userLoginDto.getPassword(), loginClient.getPassword())).thenReturn(true);

        assertThat(clientService.authorizeClient(userLoginDto)).isTrue();

        verify(clientRepository, times(1)).findByEmail(userLoginDto.getEmail());
        verify(passwordEncoder, times(1)).matches(userLoginDto.getPassword(), loginClient.getPassword());
    }

    @Test
    public void authorizeClientShouldReturnErrorIfClientNotExists() {
        when(clientRepository.findByEmail(userLoginDto.getEmail())).thenReturn(Optional.empty());

        assertThrows(InvalidRequestDataException.class, () -> clientService.authorizeClient(userLoginDto), WRONG_CREDENTIALS);

        verify(clientRepository, times(1)).findByEmail(userLoginDto.getEmail());
    }

    @Test
    public void authorizeClientShouldReturnFalse() {
        when(clientRepository.findByEmail(userLoginDto.getEmail())).thenReturn(Optional.of(loginClient));
        when(passwordEncoder.matches(userLoginDto.getPassword(), loginClient.getPassword())).thenReturn(false);

        assertThat(clientService.authorizeClient(userLoginDto)).isFalse();

        verify(clientRepository, times(1)).findByEmail(userLoginDto.getEmail());
        verify(passwordEncoder, times(1)).matches(userLoginDto.getPassword(), loginClient.getPassword());
    }
}
