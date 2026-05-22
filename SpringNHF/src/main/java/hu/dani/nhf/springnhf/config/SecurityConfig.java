package hu.dani.nhf.springnhf.config;

import hu.dani.nhf.springnhf.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // BEKAPCSOLJA A @PreAuthorize METÓDUSSZINTŰ VÉDELMET A SERVICE-EKBEN
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    /**
     * A jelszavak BCrypt-alapú titkosításáért felelős Bean.
     * Ezt használja a UserService regisztrációkor.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * A Spring Security hitelesítési menedzsere.
     * Ezt hívja meg az AuthController a bejelentkezési adatok (e-mail + jelszó) ellenőrzésekor.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * A HTTP kérések szűrési láncának (Security Filter Chain) modern konfigurációja.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF védelem kikapcsolása (REST API-knál, JWT tokenek mellett felesleges)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. CORS beállítások aktiválása a React frontend összekötéséhez
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 3. Munkamenet (Session) állapotmentessé tétele: a szerver nem tárol Session Cookie-t!
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable) // Teljesen kikapcsoljuk a blokkolást a H2 keretekhez
                )

                // 4. Végpontok hozzáférési szabályai
                .authorizeHttpRequests(auth -> auth
                        // Publikus végpontok: Bárki elérheti azonosítás nélkül
                        .requestMatchers("/api/auth/**").permitAll() // Regisztráció és Belépés
                        .requestMatchers("/api/courts").permitAll()  // Pályák listázása
                        .requestMatchers("/api/equipment").permitAll() // Eszközök listázása
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // Minden más végpont (pl. foglalás leadása, lemondása) szigorúan tokenhez kötött!
                        .anyRequest().authenticated()
                )

                // 5. A saját JWT szűrőnk beillesztése a szabványos UsernamePassword filter ELÉ.
                // Így a bejövő kérésekből a token már az azonosítási lánc elején kiértékelődik.
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS (Cross-Origin Resource Sharing) beállítások.
     * Engedélyezi, hogy a lokális fejlesztői böngésző (React a 3000-es porton)
     * kéréseket küldhessen a Spring Boot backendnek (8080-as port).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Ide írd be a frontend futási címét (React általában localhost:3000 vagy Vite localhost:5173)
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}