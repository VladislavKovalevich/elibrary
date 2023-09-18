package by.vlad.elibrary.controller;

import by.vlad.elibrary.model.dto.request.BookDataRequestDto;
import by.vlad.elibrary.model.dto.response.BookResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/*
 Инициализация маппинга
 Описание аннотаций для swagger-а
*/
@Tag(
        name = "Book controller",
        description = """
                Controller to manipulate with books in the App.
                This controller allows users to became books list or separate book by his uid. Also user with ADMIN role can add
                new book or update exists. To work with ADMIN role you need to authorize in system using admin credentials"""
)
@RequestMapping("/main/")
public interface BookController {

    /*
    * Возврат книги по id
    */
    @Operation(summary = "Return book by unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Bad request"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Book not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Internal server error")))
    })
    @GetMapping("book/{Id}")
    ResponseEntity<BookResponseDto> returnBookById(@PathVariable(name = "Id") Long bookId);

    /*
     * Возврат списка книг
    */
    @Operation(summary = "Return list of books")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Bad request"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Book not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Internal server error")))
    })
    @GetMapping("book")
    ResponseEntity<List<BookResponseDto>> returnAllBooks();

    /*
     * Добавление новой книги
    */
    @Operation(summary = "Create new book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Bad request"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Book not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Internal server error")))
    })
    @PostMapping("book")
    ResponseEntity<BookResponseDto> createNewBook(@RequestBody BookDataRequestDto bookDataRequestDto);

    /*
     * Изменение существующей книги
    */
    @Operation(summary = "Update exists book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Bad request"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Book not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Internal server error")))
    })
    @PutMapping("book")
    ResponseEntity<BookResponseDto> updateBook(@RequestBody BookDataRequestDto bookDataRequestDto);

    /*
     * Удаление книги
    */

}
