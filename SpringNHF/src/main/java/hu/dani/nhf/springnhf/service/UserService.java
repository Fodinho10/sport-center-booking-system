package hu.dani.nhf.springnhf.service;

import hu.dani.nhf.springnhf.dto.UserRegistrationDto;
import hu.dani.nhf.springnhf.entity.User;
import hu.dani.nhf.springnhf.enums.Role; // Vagy ahogy az enumodat elnevezted
import hu.dani.nhf.springnhf.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Új felhasználó regisztrációja BCrypt jelszótitkosítással.
     */
    @Transactional
    public User registerUser(UserRegistrationDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalStateException("Ezzel az e-mail címmel már regisztráltak!");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        user.setPassword(encodedPassword);
        user.setRole(Role.CUSTOMER);

        return userRepository.save(user);
    }

    // Ide jöhetnek majd a további metódusok (pl. profil szerkesztése, törlése)
}
