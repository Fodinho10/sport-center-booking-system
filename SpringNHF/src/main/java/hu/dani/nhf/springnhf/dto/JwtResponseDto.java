package hu.dani.nhf.springnhf.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtResponseDto {
    private String token;
    private String type = "Bearer";
    private String email;
    private String role;

    public JwtResponseDto(String token, String email, String role) {
        this.token = token;
        this.email = email;
        this.role = role;
    }
}