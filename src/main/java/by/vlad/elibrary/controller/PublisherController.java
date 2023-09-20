package by.vlad.elibrary.controller;

import by.vlad.elibrary.model.dto.request.PublisherRequestDto;
import by.vlad.elibrary.model.dto.response.PublisherResponseDto;
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
        name = "Publisher controller",
        description = """
                Controller to manipulate with publishers in the App.
                This controller allows users to became publishers list or separate publisher by his uid. Also user with ADMIN role can add
                new publisher or update exists. To work with ADMIN role you need to authorize in system using admin credentials"""
)
@RequestMapping("/publisher/")
public interface PublisherController {

    @Operation(summary = "Return publishers list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PublisherResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Bad request"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Book not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Internal server error")))
    })
    @GetMapping
    ResponseEntity<List<PublisherResponseDto>> returnPublishers();

    @Operation(summary = "Return publisher by identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PublisherResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Bad request"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Book not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Internal server error")))
    })
    @GetMapping("/{id}")
    ResponseEntity<PublisherResponseDto> returnPublisherById(@PathVariable Long id);

    @Operation(summary = "Create new publisher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PublisherResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Bad request"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Book not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Internal server error")))
    })
    @PostMapping
    ResponseEntity<PublisherResponseDto> createNewPublisher(@RequestBody PublisherRequestDto dto);

    @Operation(summary = "Update publishers parameters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PublisherResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Bad request"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Book not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Internal server error")))
    })
    @PutMapping
    ResponseEntity<PublisherResponseDto> updatePublisher(@RequestBody PublisherRequestDto dto);
}
