package hu.dani.nhf.springnhf.config;

import hu.dani.nhf.springnhf.entity.Court;
import hu.dani.nhf.springnhf.entity.Equipment;
import hu.dani.nhf.springnhf.entity.User;
import hu.dani.nhf.springnhf.enums.CourtStatus;
import hu.dani.nhf.springnhf.enums.EquipmentStatus;
import hu.dani.nhf.springnhf.enums.Role;
import hu.dani.nhf.springnhf.enums.SportType;
import hu.dani.nhf.springnhf.repository.CourtRepository;
import hu.dani.nhf.springnhf.repository.EquipmentRepository;
import hu.dani.nhf.springnhf.repository.UserRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CourtRepository courtRepository;
    private final EquipmentRepository equipmentRepository;
    // 1. Injektáljuk a User kezelőket
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(@Nonnull String... args) {

        // --- 1. ADMIN FELHASZNÁLÓ BETÖLTÉSE ---
        String adminEmail = "admin@sportcentrum.hu";
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            log.info("Adatbázis inicializálása: Adminisztrátor fiók létrehozása...");
            User admin = new User();
            admin.setName("Fő Adminisztrátor");
            admin.setEmail(adminEmail);
            // Titkosítjuk a jelszót! Ezzel tudsz majd belépni.
            admin.setPassword(passwordEncoder.encode("AdminJelszo123"));
            admin.setRole(Role.ADMIN); // Jogosultság beállítása
            userRepository.save(admin);
        }

        // --- 2. PÁLYA BETÖLTÉSE ---
        if (courtRepository.count() == 0) {
            log.info("Adatbázis inicializálása: Alapértelmezett pálya létrehozása...");
            Court court = new Court();
            court.setName("Központi Teniszpálya");
            court.setSportType(SportType.TENNIS);
            court.setHourlyRate(BigDecimal.valueOf(5000));
            court.setCourtStatus(CourtStatus.ACTIVE);
            courtRepository.save(court);
        }

        // --- 3. ESZKÖZ BETÖLTÉSE ---
        if (equipmentRepository.count() == 0) {
            log.info("Adatbázis inicializálása: Alapértelmezett eszköz létrehozása...");
            Equipment equipment = new Equipment();
            equipment.setName("Profi Teniszütő");
            equipment.setSportType(SportType.TENNIS);
            equipment.setTotalInventory(10);
            equipment.setHourlyRate(BigDecimal.valueOf(1000));
            equipment.setEquipmentStatus(EquipmentStatus.AVAILABLE);
            equipmentRepository.save(equipment);
        }

        log.info("Az adatbázis készen áll!");
    }
}