package hu.dani.nhf.springnhf.security;

import hu.dani.nhf.springnhf.entity.User;
import hu.dani.nhf.springnhf.enums.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails) {
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("ROLE_CUSTOMER"); // Alapértelmezett érték védelemként

        Long userId = ((UserDetailsImpl) userDetails).getUser().getId();

        return Jwts.builder()
                .claim("role", role)
                .claim("id", userId)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    public UserDetails getUserDetailsFromToken(String token) {
        try {
            Claims payload = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String email = payload.getSubject();
            String roleString = payload.get("role", String.class); // Helyes kulcs beolvasása
            Number idNumber = payload.get("id", Number.class);

            Role roleEnum = Role.CUSTOMER; // Alapértelmezett védelem
            if (roleString != null && roleString.startsWith("ROLE_")) {
                try {
                    roleEnum = Role.valueOf(roleString.substring(5)); // Levágjuk a "ROLE_" előtagot
                } catch (IllegalArgumentException ignored) {
                    // Marad a CUSTOMER
                }
            }

            // Memóriabeli User példányosítása az Adapterhez
            User user = new User();
            user.setId(idNumber != null ? idNumber.longValue() : null);
            user.setEmail(email);
            user.setRole(roleEnum); // A saját dedikált Role enum meződ beállítása

            return new UserDetailsImpl(user);

        } catch (JwtException | IllegalArgumentException e) {
            log.error("Érvénytelen vagy lejárt JWT token: {}", e.getMessage());
        }
        return null;
    }
}