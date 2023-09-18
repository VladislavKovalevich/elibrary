package by.vlad.elibrary.service;

import by.vlad.elibrary.model.dto.request.BookDataRequestDto;
import by.vlad.elibrary.model.dto.response.BookResponseDto;

import java.util.List;

public interface BookService {

    BookResponseDto returnBookById(Long bookId);

    List<BookResponseDto> returnBooks();

    BookResponseDto createNewBook(BookDataRequestDto bookDataRequestDto);

    BookResponseDto updateBook(BookDataRequestDto bookDataRequestDto);
}
