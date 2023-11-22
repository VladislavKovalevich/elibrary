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

import java.util.Locale;
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

    @Test
    public void createNewClientShouldReturnNewClient() {

        UserRegisterDataRequestDto dto = UserRegisterDataRequestDto.builder()
                .email("test1user@gmail.com")
                .name("Name")
                .surname("Surname")
                .password("qwerty123")
                .repeatedPassword("qwerty123")
                .build();

        Client client = Client.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .surname(dto.getSurname())
                .build();

        Client savedClient = Client.builder()
                .id(1L)
                .email(dto.getEmail())
                .name(dto.getName())
                .surname(dto.getSurname())
                .password("12E45FQFD452FFQ331")
                .isNonLocked(true)
                .role(Role.CLIENT)
                .build();

        when(clientRepository.countByEmail(dto.getEmail())).thenReturn(0);
        when(clientMapper.convertUserRegistrationDataRequestDtoToClientEntity(dto)).thenReturn(client);
        when(passwordEncoder.encode(null)).thenReturn("12E45FQFD452FFQ331");
        when(clientRepository.save(client)).thenReturn(savedClient);

        String result = clientService.createNewClient(dto);

        assertThat(result).isNotEmpty();

        verify(clientRepository, times(1)).countByEmail(dto.getEmail());
        verify(clientMapper, times(1)).convertUserRegistrationDataRequestDtoToClientEntity(dto);
        verify(passwordEncoder, times(1)).encode(null);
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    public void createNewClientShouldReturnErrorIfEmailIsAlreadyExists(){
        UserRegisterDataRequestDto dto = UserRegisterDataRequestDto.builder()
                .email("test1user@gmail.com")
                .name("Name")
                .surname("Surname")
                .password("qwerty123")
                .repeatedPassword("qwerty123")
                .build();

        when(clientRepository.countByEmail(dto.getEmail())).thenReturn(1);

        assertThrows(InvalidRequestDataException.class, () -> clientService.createNewClient(dto), USER_EMAIL_ALREADY_EXISTS);

        verify(clientRepository, times(1)).countByEmail(dto.getEmail());
    }

    @Test
    public void createNewClientShouldReturnErrorIfPasswordMismatch(){
        UserRegisterDataRequestDto dto = UserRegisterDataRequestDto.builder()
                .email("test1user@gmail.com")
                .name("Name")
                .surname("Surname")
                .password("qwerty123")
                .repeatedPassword("qwerty1234")
                .build();

        when(clientRepository.countByEmail(dto.getEmail())).thenReturn(0);

        assertThrows(InvalidRequestDataException.class, () -> clientService.createNewClient(dto), PASSWORDS_MISMATCH);

        verify(clientRepository, times(1)).countByEmail(dto.getEmail());
    }

    @Test
    public void authorizeClientShouldReturnTrue(){
        UserLoginDataRequestDto dto = UserLoginDataRequestDto.builder()
                .email("test1user@gmail.com")
                .password("qwerty123")
                .build();

        Client client = Client.builder()
                .id(1L)
                .email(dto.getEmail())
                .password(dto.getPassword())
                .build();

        when(clientRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(client));
        when(passwordEncoder.matches(dto.getPassword(), client.getPassword())).thenReturn(true);

        assertThat(clientService.authorizeClient(dto)).isTrue();

        verify(clientRepository, times(1)).findByEmail(dto.getEmail());
        verify(passwordEncoder, times(1)).matches(dto.getPassword(), client.getPassword());
    }

    @Test
    public void authorizeClientShouldReturnErrorIfClientNotExists(){
        UserLoginDataRequestDto dto = UserLoginDataRequestDto.builder()
                .email("test1user@gmail.com")
                .password("qwerty123")
                .build();

        when(clientRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        assertThrows(InvalidRequestDataException.class , () -> clientService.authorizeClient(dto), WRONG_CREDENTIALS);

        verify(clientRepository, times(1)).findByEmail(dto.getEmail());
    }

    @Test
    public void authorizeClientShouldReturnFalse(){
        UserLoginDataRequestDto dto = UserLoginDataRequestDto.builder()
                .email("test1user@gmail.com")
                .password("qwerty123")
                .build();

        Client client = Client.builder()
                .id(1L)
                .email(dto.getEmail())
                .password(dto.getPassword().toUpperCase(Locale.ROOT))
                .build();

        when(clientRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(client));
        when(passwordEncoder.matches(dto.getPassword(), client.getPassword())).thenReturn(false);

        assertThat(clientService.authorizeClient(dto)).isFalse();

        verify(clientRepository, times(1)).findByEmail(dto.getEmail());
        verify(passwordEncoder, times(1)).matches(dto.getPassword(), client.getPassword());
    }
}
