package hu.dani.nhf.springnhf.controller;

import hu.dani.nhf.springnhf.dto.JwtResponseDto;
import hu.dani.nhf.springnhf.dto.LoginRequestDto;
import hu.dani.nhf.springnhf.dto.UserRegistrationDto;
import hu.dani.nhf.springnhf.security.JwtService;
import hu.dani.nhf.springnhf.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    /**
     * REGISZTRÁCIÓS VÉGPONT (POST /api/auth/register)
     * Bárki elérheti. A bejövő DTO-t a @Valid annotáció ellenőrzi.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserRegistrationDto dto) {
        userService.registerUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Sikeres regisztráció!");
    }

    /**
     * BEJELENTKEZÉSI VÉGPONT (POST /api/auth/login)
     * Sikeres azonosítás után visszaadja a legyártott JWT tokent.
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        // 1. A Spring Security ellenőrzi az e-mailt és a jelszót (BCrypt hash alapján)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        // 2. Beállítjuk a sikeres hitelesítést a kontextusban
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Kinyerjük a betöltött UserDetails objektumot
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 4. Legyártjuk a JWT tokent
        String jwt = jwtService.generateToken(userDetails);

        // 5. Kinyerjük a szerepkört a válasz DTO-hoz
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("ROLE_CUSTOMER");

        // 6. Visszaküldjük a JSON választ a kliensnek
        return ResponseEntity.ok(new JwtResponseDto(jwt, userDetails.getUsername(), role));
    }
}