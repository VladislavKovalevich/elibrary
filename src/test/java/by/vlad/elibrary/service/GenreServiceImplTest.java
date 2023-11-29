package by.vlad.elibrary.service;

import by.vlad.elibrary.exception.InvalidRequestDataException;
import by.vlad.elibrary.mapper.GenreMapper;
import by.vlad.elibrary.model.dto.request.GenreRequestDto;
import by.vlad.elibrary.model.dto.response.GenreResponseDto;
import by.vlad.elibrary.model.entity.Genre;
import by.vlad.elibrary.repository.GenreRepository;
import by.vlad.elibrary.service.impl.GenreServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static by.vlad.elibrary.exception.util.ExceptionMessage.GENRE_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GenreServiceImplTest {

    @InjectMocks
    private GenreServiceImpl genreService;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private GenreMapper genreMapper;

    private final Genre genre;

    private final GenreRequestDto genreRequestDto;

    private final GenreResponseDto genreResponseDto;

    GenreServiceImplTest() {
        genre = Genre.builder()
                .id(1L)
                .name("NAME")
                .build();

        genreRequestDto = GenreRequestDto.builder()
                .id(1L)
                .name("NAME")
                .build();

        genreResponseDto = GenreResponseDto.builder()
                .name("NAME")
                .build();
    }

    @Test
    public void returnGenreByIdShouldReturnValidGenre() {
        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));
        when(genreMapper.fromEntityToDto(genre)).thenReturn(genreResponseDto);

        GenreResponseDto dtoResult = genreService.returnComponentById(1L);

        assertThat(dtoResult.getName()).isEqualTo(genreResponseDto.getName());

        verify(genreRepository, times(1)).findById(1L);
        verify(genreMapper, times(1)).fromEntityToDto(genre);
    }

    @Test
    public void returnGenreByIdShouldReturnErrorIfGenreNotExists() {
        when(genreRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(InvalidRequestDataException.class, () -> genreService.returnComponentById(1L),
                GENRE_NOT_FOUND);

        verify(genreRepository, times(1)).findById(1L);
    }

    @Test
    public void returnAllGenresShouldReturnValidList() {
        List<Genre> genres = List.of(genre);
        List<GenreResponseDto> dtos = List.of(genreResponseDto);

        when(genreRepository.findAll()).thenReturn(genres);
        when(genreMapper.fromEntitiesToDtos(genres)).thenReturn(dtos);

        List<GenreResponseDto> responseDtos = genreService.returnComponentList();

        assertThat(responseDtos).hasSize(1);

        verify(genreRepository, times(1)).findAll();
        verify(genreMapper, times(1)).fromEntitiesToDtos(genres);
    }

    @Test
    public void createNewGenreShouldReturnValidGenre() {
        when(genreMapper.fromDtoToEntity(genreRequestDto)).thenReturn(genre);
        when(genreRepository.save(genre)).thenReturn(genre);
        when(genreMapper.fromEntityToDto(genre)).thenReturn(genreResponseDto);

        GenreResponseDto dtoResult = genreService.createNewComponent(genreRequestDto);

        assertThat(dtoResult.getName()).isEqualTo(genreRequestDto.getName());

        verify(genreMapper, times(1)).fromDtoToEntity(genreRequestDto);
        verify(genreRepository, times(1)).save(genre);
        verify(genreMapper, times(1)).fromEntityToDto(genre);
    }

    @Test
    public void updateGenreShouldReturnValidGenre() {

        when(genreRepository.existsById(1L)).thenReturn(true);
        when(genreMapper.fromDtoToEntity(genreRequestDto)).thenReturn(genre);
        when(genreRepository.save(genre)).thenReturn(genre);
        when(genreMapper.fromEntityToDto(genre)).thenReturn(genreResponseDto);

        GenreResponseDto dtoResult = genreService.updateComponent(genreRequestDto);

        assertThat(dtoResult.getName()).isEqualTo(genreRequestDto.getName());

        verify(genreRepository, times(1)).existsById(1L);
        verify(genreMapper, times(1)).fromDtoToEntity(genreRequestDto);
        verify(genreRepository, times(1)).save(genre);
        verify(genreMapper, times(1)).fromEntityToDto(genre);
    }

    @Test
    public void updateGenreShouldReturnErrorIfGenreNotExists() {

        when(genreRepository.existsById(1L)).thenReturn(false);

        assertThrows(InvalidRequestDataException.class, () -> genreService.updateComponent(genreRequestDto),
                GENRE_NOT_FOUND);

        verify(genreRepository, times(1)).existsById(1L);
    }
}
