package by.vlad.elibrary.service;

import by.vlad.elibrary.mapper.ClientMapper;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

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

        when(clientMapper.convertUserRegistrationDataRequestDtoToClientEntity(dto))
                .thenReturn(client);
        when(passwordEncoder.encode(null))
                .thenReturn("12E45FQFD452FFQ331");
        when(clientRepository.save(client))
                .thenReturn(savedClient);

        String result = clientService.createNewClient(dto);

        assertThat(result).isEqualTo("OK");

        verify(clientMapper, times(1))
                .convertUserRegistrationDataRequestDtoToClientEntity(dto);
        verify(passwordEncoder, times(1))
                .encode(null);
        verify(clientRepository, times(1))
                .save(client);
    }

}
