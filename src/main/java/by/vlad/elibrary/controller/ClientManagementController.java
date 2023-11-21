package by.vlad.elibrary.controller;

import by.vlad.elibrary.model.dto.request.UserLoginDataRequestDto;
import by.vlad.elibrary.model.dto.request.UserRegisterDataRequestDto;
import by.vlad.elibrary.model.dto.response.ClientResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(
        name = "Client controller",
        description = """
                Controller to manipulate with users in the App.
                This controller allows users to register in system, login, logout and change profile parameters. Also user with ADMIN role can
                check users list and block simple users. To work with ADMIN role you need to authorize in system using admin credentials"""
)
@RequestMapping("/client/")
public interface ClientManagementController {

    @Operation(summary = "Register new client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Bad request"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Book not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Internal server error")))
    })
    @PostMapping("register")
    ResponseEntity<String> registerClient(@RequestBody UserRegisterDataRequestDto dto);

    @Operation(summary = "Authorize client in system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Bad request"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Book not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Internal server error")))
    })
    @PostMapping("login")
    ResponseEntity<String> loginClient(@RequestBody UserLoginDataRequestDto dto);

}
