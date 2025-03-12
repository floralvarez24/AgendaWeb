package dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class ContactDTO {
    @NotBlank (message = "El nombre es obligatorio")
    private String name;

    @Email(message = "El email es inválido" )
    @NotBlank (message = "El email es bligatorio")
    private String email;

}
