package by.vlad.elibrary.controller.impl;

import by.vlad.elibrary.controller.BookController;
import by.vlad.elibrary.model.dto.request.BookDataRequestDto;
import by.vlad.elibrary.model.dto.response.BookResponseDto;
import by.vlad.elibrary.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookControllerImpl implements BookController {

    private final BookService bookService;

    @Override
    public ResponseEntity<BookResponseDto> returnBookById(Long bookId) {
        return new ResponseEntity<>(bookService.returnBookById(bookId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<BookResponseDto>> returnAllBooks() {
        return new ResponseEntity<>(bookService.returnBooks(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BookResponseDto> createNewBook(BookDataRequestDto bookDataRequestDto) {
        return new ResponseEntity<>(bookService.createNewBook(bookDataRequestDto), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<BookResponseDto> updateBook(BookDataRequestDto bookDataRequestDto) {
        return new ResponseEntity<>(bookService.updateBook(bookDataRequestDto), HttpStatus.OK);
    }
}
