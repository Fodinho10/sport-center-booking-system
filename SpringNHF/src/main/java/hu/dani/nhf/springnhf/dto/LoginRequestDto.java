package hu.dani.nhf.springnhf.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    @NotBlank(message = "Az e-mail cím megadása kötelező!")
    @Email(message = "Érvénytelen e-mail formátum!")
    private String email;

    @NotBlank(message = "A jelszó megadása kötelező!")
    private String password;
}