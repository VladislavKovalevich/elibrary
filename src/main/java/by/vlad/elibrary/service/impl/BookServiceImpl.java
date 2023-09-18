package by.vlad.elibrary.service.impl;

import by.vlad.elibrary.exception.NotFoundException;
import by.vlad.elibrary.mapper.BookMapper;
import by.vlad.elibrary.model.dto.request.BookDataRequestDto;
import by.vlad.elibrary.model.dto.response.BookResponseDto;
import by.vlad.elibrary.model.entity.Book;
import by.vlad.elibrary.repository.AuthorRepository;
import by.vlad.elibrary.repository.BookRepository;
import by.vlad.elibrary.repository.GenreRepository;
import by.vlad.elibrary.repository.PublisherRepository;
import by.vlad.elibrary.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final GenreRepository genreRepository;

    private final AuthorRepository authorRepository;

    private final PublisherRepository publisherRepository;

    private final BookMapper bookMapper;

    @Override
    public BookResponseDto returnBookById(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("book with this id isn't exists"));

        return bookMapper.fromBookToDto(book);
    }

    @Override
    public List<BookResponseDto> returnBooks() {
        List<Book> books = bookRepository.findAll();

        return bookMapper.fromBooksToDtos(books);
    }

    @Override
    public BookResponseDto createNewBook(BookDataRequestDto bookDataRequestDto) {
        String component = checkBookComponentsExisting(bookDataRequestDto);

        if (component != null){
            throw new NotFoundException(component + " with this id isn't exists");
        }

        Book book = bookMapper.fromDtoToBook(bookDataRequestDto);

        Book savedBook = bookRepository.save(book);

        return bookMapper.fromBookToDto(savedBook);
    }

    @Override
    public BookResponseDto updateBook(BookDataRequestDto bookDataRequestDto) {
        if(!bookRepository.existsById(bookDataRequestDto.getId())){
            throw new NotFoundException("book with this id isn't exists");
        }

        return createNewBook(bookDataRequestDto);
    }

    private String checkBookComponentsExisting(BookDataRequestDto bookDataRequestDto){
        String result = null;

        if (!authorRepository.existsById(bookDataRequestDto.getAuthorId())){
            result = "author";
        }

        if (!genreRepository.existsById(bookDataRequestDto.getGenreId())){
            result = "genre";
        }

        if (!publisherRepository.existsById(bookDataRequestDto.getPublisherId())){
            result = "publisher";
        }

        return result;
    }
}
