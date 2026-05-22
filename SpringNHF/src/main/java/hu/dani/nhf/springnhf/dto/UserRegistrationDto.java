package hu.dani.nhf.springnhf.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRegistrationDto {

    @NotBlank(message = "A név megadása kötelező!")
    @Size(min = 3, max = 50, message = "A név hossza 3 és 50 karakter között kell hogy legyen!")
    private String name;

    @NotBlank(message = "Az e-mail cím megadása kötelező!")
    @Email(message = "Érvénytelen e-mail cím formátum!")
    private String email;

    @NotBlank(message = "A jelszó megadása kötelező!")
    @Size(min = 6, message = "A jelszónak legalább 6 karakter hosszúnak kell lennie!")
    private String password;
}
