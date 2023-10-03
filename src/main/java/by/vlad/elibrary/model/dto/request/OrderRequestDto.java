package by.vlad.elibrary.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class OrderRequestDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("order_status")
    private String orderStatus;

    @JsonProperty("name")
    private String name;

    @JsonProperty("books_id")
    private Long bookId;
}
