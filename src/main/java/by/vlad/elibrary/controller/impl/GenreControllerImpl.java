package by.vlad.elibrary.controller.impl;

import by.vlad.elibrary.controller.GenreController;
import by.vlad.elibrary.model.dto.request.GenreRequestDto;
import by.vlad.elibrary.model.dto.response.GenreResponseDto;
import by.vlad.elibrary.service.BookComponentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GenreControllerImpl implements GenreController {

    private final BookComponentsService<GenreResponseDto, GenreRequestDto> genreService;

    @Override
    public ResponseEntity<List<GenreResponseDto>> returnGenres() {
        return new ResponseEntity<>(genreService.returnComponentList(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<GenreResponseDto> returnGenreById(Long id) {
        return new ResponseEntity<>(genreService.returnComponentById(id), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<GenreResponseDto> createNewGenre(GenreRequestDto dto) {
        return new ResponseEntity<>(genreService.createNewComponent(dto), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<GenreResponseDto> updateGenre(GenreRequestDto dto) {
        return new ResponseEntity<>(genreService.updateComponent(dto), HttpStatus.OK);
    }
}
