package by.vlad.elibrary.mapper;

import by.vlad.elibrary.model.dto.request.UserRegisterDataRequestDto;
import by.vlad.elibrary.model.dto.response.ClientResponseDto;
import by.vlad.elibrary.model.entity.Client;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ClientMapperTest {

    @InjectMocks
    private ClientMapperImpl clientMapper;

    private final Client expectedClient;

    private final UserRegisterDataRequestDto expectedRegisterRequestDto;

    private final ClientResponseDto expectedClientResponseDto;


    public ClientMapperTest() {
        expectedClient = Client.builder()
                .id(1L)
                .name("name")
                .surname("surname")
                .email("test12@gmail.com")
                .password("abcdefg123")
                .isNonLocked(true)
                .build();

        expectedClientResponseDto = ClientResponseDto.builder()
                .name("name")
                .surname("surname")
                .build();

        expectedRegisterRequestDto = UserRegisterDataRequestDto.builder()
                .name("name")
                .surname("surname")
                .email("test12@gmail.com")
                .password("abcdefg123")
                .repeatedPassword("abcdefg123")
                .build();
    }

    @Test
    public void convertUserRegistrationDataRequestDtoToClientEntityShouldReturnValidEntity(){
        Client actualClient = clientMapper.convertUserRegistrationDataRequestDtoToClientEntity(expectedRegisterRequestDto);

        assertThat(actualClient.getEmail()).isEqualTo(expectedClient.getEmail());
        assertThat(actualClient.getName()).isEqualTo(expectedClient.getName());
        assertThat(actualClient.getSurname()).isEqualTo(expectedClient.getSurname());
        assertThat(actualClient.getPassword()).isEqualTo(expectedClient.getPassword());
    }

    @Test
    public void convertUserRegistrationDataRequestDtoToClientEntityShouldReturnNullIfDtoIsNull(){
        Client actualClient = clientMapper.convertUserRegistrationDataRequestDtoToClientEntity(null);

        assertThat(actualClient).isNull();
    }

    @Test
    public void fromEntityToDtoShouldReturnValidDto(){
        ClientResponseDto actualClientResponseDto = clientMapper.fromEntityToDto(expectedClient);

        assertThat(actualClientResponseDto).isEqualTo(expectedClientResponseDto);
    }

    @Test
    public void fromEntityToDtoShouldReturnNullIfEntityIsNull(){
        ClientResponseDto actualClientResponseDto = clientMapper.fromEntityToDto(null);

        assertThat(actualClientResponseDto).isNull();
    }

}
