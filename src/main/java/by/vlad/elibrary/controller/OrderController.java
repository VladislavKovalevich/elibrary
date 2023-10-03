package by.vlad.elibrary.controller;

import by.vlad.elibrary.model.dto.request.OrderRequestDto;
import by.vlad.elibrary.model.dto.response.OrderResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(
        name = "Order controller",
        description = """
                Controller to manipulate with orders in the App.
                This controller allows users to became orders list or separate order by his uid.
                To work with ADMIN role you need to authorize in system using admin credentials"""
)
@RequestMapping("/order/")
public interface OrderController {

    @Operation(summary = "Return genres list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Bad request"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Book not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Internal server error")))
    })
    @GetMapping
    ResponseEntity<List<OrderResponseDto>> returnOrdersAll();

    @Operation(summary = "Create new order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Bad request"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Book not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Internal server error")))
    })
    @PostMapping
    ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto dto);

    @Operation(summary = "Add book to order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Bad request"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Book not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Internal server error")))
    })
    @PatchMapping
    ResponseEntity<OrderResponseDto> addBookToOrder(@RequestBody OrderRequestDto dto);

    @Operation(summary = "Remove book from order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Bad request"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Book not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Internal server error")))
    })
    @DeleteMapping
    ResponseEntity<OrderResponseDto> removeBookFromOrder(@RequestBody OrderRequestDto dto);

    @Operation(summary = "Reserve order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Bad request"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Book not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Internal server error")))
    })
    @PatchMapping("/reserve/{id}")
    ResponseEntity<OrderResponseDto> reserveOrder(@PathVariable("id") Long id);

    @Operation(summary = "Accept order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Bad request"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Book not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Internal server error")))
    })
    @PatchMapping("/accept/{id}")
    ResponseEntity<OrderResponseDto> acceptOrder(@PathVariable("id") Long id);

    @Operation(summary = "Reject order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Bad request"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Book not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Internal server error")))
    })
    @PatchMapping("/reject/{id}")
    ResponseEntity<OrderResponseDto> rejectOrder(@PathVariable("id") Long id);

    @Operation(summary = "Return order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Bad request"))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Book not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Internal server error")))
    })
    @PatchMapping("/return/{id}")
    ResponseEntity<OrderResponseDto> returnOrder(@PathVariable("id") Long id);
}
