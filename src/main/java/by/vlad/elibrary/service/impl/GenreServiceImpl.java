package by.vlad.elibrary.service.impl;

import by.vlad.elibrary.exception.InvalidRequestDataException;
import by.vlad.elibrary.mapper.GenreMapper;
import by.vlad.elibrary.model.dto.request.GenreRequestDto;
import by.vlad.elibrary.model.dto.response.GenreResponseDto;
import by.vlad.elibrary.model.entity.Genre;
import by.vlad.elibrary.repository.GenreRepository;
import by.vlad.elibrary.service.BookComponentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static by.vlad.elibrary.exception.util.ExceptionMessage.GENRE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements BookComponentsService<GenreResponseDto, GenreRequestDto> {

    private final GenreRepository genreRepository;

    private final GenreMapper genreMapper;

    @Override
    public GenreResponseDto returnComponentById(Long componentId) {
        Genre genre = genreRepository.findById(componentId)
                .orElseThrow(()-> new InvalidRequestDataException(GENRE_NOT_FOUND));

        return genreMapper.fromEntityToDto(genre);
    }

    @Override
    public List<GenreResponseDto> returnComponentList() {
        List<Genre> genres = genreRepository.findAll();

        return genreMapper.fromEntitiesToDtos(genres);
    }

    @Override
    public GenreResponseDto createNewComponent(GenreRequestDto dto) {
        Genre genre = genreMapper.fromDtoToEntity(dto);
        genre.setId(null);

        Genre savedGenre = genreRepository.save(genre);
        return genreMapper.fromEntityToDto(savedGenre);
    }

    @Override
    public GenreResponseDto updateComponent(GenreRequestDto dto) {
        if (!genreRepository.existsById(dto.getId())){
            throw new InvalidRequestDataException(GENRE_NOT_FOUND);
        }

        Genre genre = genreMapper.fromDtoToEntity(dto);

        Genre savedGenre = genreRepository.save(genre);
        return genreMapper.fromEntityToDto(savedGenre);
    }
}
