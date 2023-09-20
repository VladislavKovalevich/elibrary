package by.vlad.elibrary.controller.impl;

import by.vlad.elibrary.controller.AuthorController;
import by.vlad.elibrary.model.dto.request.AuthorRequestDto;
import by.vlad.elibrary.model.dto.response.AuthorResponseDto;
import by.vlad.elibrary.service.BookComponentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthorControllerImpl implements AuthorController {

    private final BookComponentsService<AuthorResponseDto, AuthorRequestDto> authorService;

    @Override
    public ResponseEntity<List<AuthorResponseDto>> returnAuthors() {
        return new ResponseEntity<>(authorService.returnComponentList(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AuthorResponseDto> returnAuthorById(Long id) {
        return new ResponseEntity<>(authorService.returnComponentById(id), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AuthorResponseDto> createNewAuthor(AuthorRequestDto dto) {
        return new ResponseEntity<>(authorService.createNewComponent(dto), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<AuthorResponseDto> updateAuthor(AuthorRequestDto dto) {
        return new ResponseEntity<>(authorService.updateComponent(dto), HttpStatus.OK);
    }
}
