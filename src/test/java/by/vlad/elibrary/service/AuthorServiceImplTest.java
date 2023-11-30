package by.vlad.elibrary.service;

import by.vlad.elibrary.exception.InvalidRequestDataException;
import by.vlad.elibrary.mapper.AuthorMapper;
import by.vlad.elibrary.model.dto.request.AuthorRequestDto;
import by.vlad.elibrary.model.dto.response.AuthorResponseDto;
import by.vlad.elibrary.model.entity.Author;
import by.vlad.elibrary.repository.AuthorRepository;
import by.vlad.elibrary.service.impl.AuthorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static by.vlad.elibrary.exception.util.ExceptionMessage.AUTHOR_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceImplTest {

    @InjectMocks
    private AuthorServiceImpl authorService;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthorMapper authorMapper;

    private final Author author;

    private final AuthorRequestDto requestDto;

    private final AuthorResponseDto responseDto;

    AuthorServiceImplTest() {
        author = Author.builder()
                .id(1L)
                .name("NAME")
                .surname("SURNAME")
                .build();

        requestDto = AuthorRequestDto.builder()
                .id(1L)
                .name("NAME")
                .surname("SURNAME")
                .build();

        responseDto = AuthorResponseDto.builder()
                .id(1L)
                .name("NAME")
                .surname("SURNAME")
                .build();
    }

    @Test
    public void returnAuthorByIdShouldReturnValidAuthor() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorMapper.fromEntityToDto(author)).thenReturn(responseDto);

        AuthorResponseDto dtoResult = authorService.returnComponentById(1L);

        assertThat(dtoResult.getId()).isEqualTo(responseDto.getId());

        verify(authorRepository, times(1)).findById(1L);
        verify(authorMapper, times(1)).fromEntityToDto(author);
    }

    @Test
    public void returnAuthorByIdShouldReturnErrorIfAuthorNotExists() {
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(InvalidRequestDataException.class, () -> authorService.returnComponentById(1L),
                AUTHOR_NOT_FOUND);

        verify(authorRepository, times(1)).findById(1L);
    }

    @Test
    public void returnAllAuthorsShouldReturnValidList() {
        List<Author> authors = List.of(author);
        List<AuthorResponseDto> dtos = List.of(responseDto);

        when(authorRepository.findAll()).thenReturn(authors);
        when(authorMapper.fromEntitiesToDtos(authors)).thenReturn(dtos);

        List<AuthorResponseDto> responseDtos = authorService.returnComponentList();

        assertThat(responseDtos).hasSize(1);

        verify(authorRepository, times(1)).findAll();
        verify(authorMapper, times(1)).fromEntitiesToDtos(authors);
    }

    @Test
    public void createNewAuthorShouldReturnValidAuthor() {
        when(authorMapper.fromDtoToEntity(requestDto)).thenReturn(author);
        when(authorRepository.save(author)).thenReturn(author);
        when(authorMapper.fromEntityToDto(author)).thenReturn(responseDto);

        AuthorResponseDto dtoResult = authorService.createNewComponent(requestDto);

        assertThat(dtoResult.getName()).isEqualTo(requestDto.getName());
        assertThat(dtoResult.getSurname()).isEqualTo(requestDto.getSurname());

        verify(authorMapper, times(1)).fromDtoToEntity(requestDto);
        verify(authorRepository, times(1)).save(author);
        verify(authorMapper, times(1)).fromEntityToDto(author);
    }

    @Test
    public void updateAuthorShouldReturnValidAuthor() {

        when(authorRepository.existsById(1L)).thenReturn(true);
        when(authorMapper.fromDtoToEntity(requestDto)).thenReturn(author);
        when(authorRepository.save(author)).thenReturn(author);
        when(authorMapper.fromEntityToDto(author)).thenReturn(responseDto);

        AuthorResponseDto dtoResult = authorService.updateComponent(requestDto);

        assertThat(dtoResult.getName()).isEqualTo(requestDto.getName());
        assertThat(dtoResult.getSurname()).isEqualTo(requestDto.getSurname());

        verify(authorRepository, times(1)).existsById(1L);
        verify(authorMapper, times(1)).fromDtoToEntity(requestDto);
        verify(authorRepository, times(1)).save(author);
        verify(authorMapper, times(1)).fromEntityToDto(author);
    }

    @Test
    public void updateAuthorShouldReturnErrorIfAuthorNotExists() {
        when(authorRepository.existsById(1L)).thenReturn(false);

        assertThrows(InvalidRequestDataException.class, () -> authorService.updateComponent(requestDto),
                AUTHOR_NOT_FOUND);

        verify(authorRepository, times(1)).existsById(1L);
    }
}
