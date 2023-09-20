package by.vlad.elibrary.controller;

import by.vlad.elibrary.model.dto.request.GenreRequestDto;
import by.vlad.elibrary.model.dto.response.GenreResponseDto;
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

@Tag(
        name = "Genre controller",
        description = """
                Controller to manipulate with genres in the App.
                This controller allows users to became genres list or separate genre by his uid. Also user with ADMIN role can add
                new genre or update exists. To work with ADMIN role you need to authorize in system using admin credentials"""
)
@RequestMapping("/genre/")
public interface GenreController {


    @Operation(summary = "Return genres list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenreResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Bad request"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Book not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Internal server error")))
    })
    @GetMapping
    ResponseEntity<List<GenreResponseDto>> returnGenres();

    @Operation(summary = "Return genre by identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenreResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Bad request"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Book not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Internal server error")))
    })
    @GetMapping("/{id}")
    ResponseEntity<GenreResponseDto> returnGenreById(@PathVariable Long id);

    @Operation(summary = "Create new genre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenreResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Bad request"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Book not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Internal server error")))
    })
    @PostMapping
    ResponseEntity<GenreResponseDto> createNewGenre(@RequestBody GenreRequestDto dto);

    @Operation(summary = "Update genres parameters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenreResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Bad request"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Book not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Internal server error")))
    })
    @PutMapping
    ResponseEntity<GenreResponseDto> updateGenre(@RequestBody GenreRequestDto dto);
}
