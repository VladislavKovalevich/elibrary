package by.vlad.elibrary.mapper;

import by.vlad.elibrary.model.dto.request.GenreRequestDto;
import by.vlad.elibrary.model.dto.response.GenreResponseDto;
import by.vlad.elibrary.model.entity.Genre;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class GenreMapperTest {

    @InjectMocks
    private GenreMapperImpl genreMapper;

    private final Genre expectedGenre;

    private final GenreRequestDto expectedGenreRequestDto;

    private final GenreResponseDto expectedGenreResponseDto;

    private final List<Genre> expectedGenres;

    private final List<GenreResponseDto> expectedGenreResponseDtos;

    public GenreMapperTest(){
        expectedGenre = Genre.builder()
                .id(1L)
                .name("NAME")
                .build();

        expectedGenreResponseDto = GenreResponseDto.builder()
                .id(1L)
                .name("NAME")
                .build();

        expectedGenreRequestDto = GenreRequestDto.builder()
                .id(1L)
                .name("NAME")
                .build();

        expectedGenres = List.of(expectedGenre);

        expectedGenreResponseDtos = List.of(expectedGenreResponseDto);
    }

    @Test
    public void fromDtoToEntityShouldReturnValidAuthor(){
        Genre actualGenre = genreMapper.fromDtoToEntity(expectedGenreRequestDto);

        assertThat(actualGenre).isEqualTo(expectedGenre);
    }

    @Test
    public void fromDtoToEntityShouldReturnNullIfDtoIsNull(){
        Genre actualGenre = genreMapper.fromDtoToEntity(null);

        assertThat(actualGenre).isNull();
    }

    @Test
    public void fromEntityToDtoShouldReturnValidDto(){
        GenreResponseDto actualGenreResponseDto = genreMapper.fromEntityToDto(expectedGenre);

        assertThat(actualGenreResponseDto).isEqualTo(expectedGenreResponseDto);
    }

    @Test
    public void fromEntityToDtoShouldReturnNullIfEntityIsNull(){
        GenreResponseDto actualGenreResponseDto = genreMapper.fromEntityToDto(null);

        assertThat(actualGenreResponseDto).isNull();
    }

    @Test
    public void fromEntitiesToDtosShouldReturnValidList(){
        List<GenreResponseDto> actualGenreResponseDtos = genreMapper.fromEntitiesToDtos(expectedGenres);

        assertThat(actualGenreResponseDtos).isEqualTo(expectedGenreResponseDtos);
    }

    @Test
    public void fromEntitiesToDtosShouldReturnEmptyListIfListIsNull(){
        List<GenreResponseDto> actualGenreResponseDtos = genreMapper.fromEntitiesToDtos(null);

        assertThat(actualGenreResponseDtos).isNull();
    }
}
