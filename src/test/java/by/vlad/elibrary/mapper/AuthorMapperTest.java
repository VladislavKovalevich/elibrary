package by.vlad.elibrary.mapper;

import by.vlad.elibrary.model.dto.request.AuthorRequestDto;
import by.vlad.elibrary.model.dto.response.AuthorResponseDto;
import by.vlad.elibrary.model.entity.Author;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AuthorMapperTest {

    @InjectMocks
    private AuthorMapperImpl authorMapper;

    private final Author expectedAuthor;

    private final AuthorRequestDto expectedAuthorRequestDto;

    private final AuthorResponseDto expectedAuthorResponseDto;

    private final List<Author> expectedAuthors;

    private final List<AuthorResponseDto> expectedAuthorResponseDtos;

    public AuthorMapperTest(){
        expectedAuthor = Author.builder()
                .id(1L)
                .name("NAME")
                .surname("SURNAME")
                .build();

        expectedAuthorResponseDto = AuthorResponseDto.builder()
                .id(1L)
                .name("NAME")
                .surname("SURNAME")
                .build();

        expectedAuthorRequestDto = AuthorRequestDto.builder()
                .id(1L)
                .name("NAME")
                .surname("SURNAME")
                .build();

        expectedAuthors = List.of(expectedAuthor);

        expectedAuthorResponseDtos = List.of(expectedAuthorResponseDto);
    }

    @Test
    public void fromDtoToEntityShouldReturnValidAuthor(){
        Author actualAuthor = authorMapper.fromDtoToEntity(expectedAuthorRequestDto);

        assertThat(actualAuthor).isEqualTo(expectedAuthor);
    }

    @Test
    public void fromDtoToEntityShouldReturnNullIfDtoIsNull(){
        Author actualAuthor = authorMapper.fromDtoToEntity(null);

        assertThat(actualAuthor).isNull();
    }

    @Test
    public void fromEntityToDtoShouldReturnValidDto(){
        AuthorResponseDto actualAuthorResponseDto = authorMapper.fromEntityToDto(expectedAuthor);

        assertThat(actualAuthorResponseDto).isEqualTo(expectedAuthorResponseDto);
    }

    @Test
    public void fromEntityToDtoShouldReturnNullIfEntityIsNull(){
        AuthorResponseDto actualAuthorResponseDto = authorMapper.fromEntityToDto(null);

        assertThat(actualAuthorResponseDto).isNull();
    }

    @Test
    public void fromEntitiesToDtosShouldReturnValidList(){
        List<AuthorResponseDto> actualAuthorResponseDtos = authorMapper.fromEntitiesToDtos(expectedAuthors);

        assertThat(actualAuthorResponseDtos).isEqualTo(expectedAuthorResponseDtos);
    }

    @Test
    public void fromEntitiesToDtosShouldReturnEmptyListIfListIsNull(){
        List<AuthorResponseDto> actualAuthorResponseDtos = authorMapper.fromEntitiesToDtos(null);

        assertThat(actualAuthorResponseDtos).isNull();
    }
}
