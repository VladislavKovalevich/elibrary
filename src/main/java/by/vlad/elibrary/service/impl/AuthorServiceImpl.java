package by.vlad.elibrary.service.impl;

import by.vlad.elibrary.exception.InvalidRequestDataException;
import by.vlad.elibrary.mapper.AuthorMapper;
import by.vlad.elibrary.model.dto.request.AuthorRequestDto;
import by.vlad.elibrary.model.dto.response.AuthorResponseDto;
import by.vlad.elibrary.model.entity.Author;
import by.vlad.elibrary.repository.AuthorRepository;
import by.vlad.elibrary.service.BookComponentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static by.vlad.elibrary.exception.util.ExceptionMessage.AUTHOR_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements BookComponentsService<AuthorResponseDto, AuthorRequestDto> {

    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    @Override
    public AuthorResponseDto returnComponentById(Long authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(()-> new InvalidRequestDataException(AUTHOR_NOT_FOUND));
        return authorMapper.fromEntityToDto(author);
    }

    @Override
    public List<AuthorResponseDto> returnComponentList() {
        List<Author> authors = authorRepository.findAll();

        return authorMapper.fromEntitiesToDtos(authors);
    }

    @Override
    public AuthorResponseDto createNewComponent(AuthorRequestDto dto) {
        Author author = authorMapper.fromDtoToEntity(dto);
        author.setId(null);

        Author savedAuthor = authorRepository.save(author);

        return authorMapper.fromEntityToDto(savedAuthor);
    }

    @Override
    public AuthorResponseDto updateComponent(AuthorRequestDto dto) {
        if (!authorRepository.existsById(dto.getId())){
            throw new InvalidRequestDataException(AUTHOR_NOT_FOUND);
        }

        Author updatedAuthor = authorRepository.save(authorMapper.fromDtoToEntity(dto));

        return authorMapper.fromEntityToDto(updatedAuthor);
    }
}
