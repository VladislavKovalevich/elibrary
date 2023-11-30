package by.vlad.elibrary.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
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
public class UserRegisterDataRequestDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("surname")
    private String surname;

    @JsonProperty("email")
    private String email;

    @NotNull
    @JsonProperty("password")
    private String password;

    @NotNull
    @JsonProperty("repeated_password")
    private String repeatedPassword;
}
