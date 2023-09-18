package by.vlad.elibrary.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BookDataRequestDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("copiesNumber")
    private String copiesNumber;

    @JsonProperty("releaseYear")
    private String releaseYear;

    @JsonProperty("numberOfPages")
    private String numberOfPages;

    @JsonProperty("description")
    private String description;

    @JsonProperty("authorId")
    private Long authorId;

    @JsonProperty("genreId")
    private Long genreId;

    @JsonProperty("publisherId")
    private Long publisherId;
}
