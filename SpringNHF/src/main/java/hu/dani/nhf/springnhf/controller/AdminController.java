package hu.dani.nhf.springnhf.controller;

import hu.dani.nhf.springnhf.dto.*;
import hu.dani.nhf.springnhf.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // --- COURTS ---

    @PostMapping("/courts")
    public ResponseEntity<CourtResponseDto> createCourt(@Valid @RequestBody CourtRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createCourt(dto));
    }

    @PutMapping("/courts/{id}")
    public ResponseEntity<CourtResponseDto> updateCourt(@PathVariable Long id, @Valid @RequestBody CourtRequestDto dto) {
        return ResponseEntity.ok(adminService.updateCourt(id, dto));
    }

    // --- EQUIPMENT ---

    @PostMapping("/equipment")
    public ResponseEntity<EquipmentResponseDto> createEquipment(@Valid @RequestBody EquipmentRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createEquipment(dto));
    }

    @PutMapping("/equipment/{id}")
    public ResponseEntity<EquipmentResponseDto> updateEquipment(@PathVariable Long id, @Valid @RequestBody EquipmentRequestDto dto) {
        return ResponseEntity.ok(adminService.updateEquipment(id, dto));
    }

    // --- RESERVATIONS ---

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponseDto>> getAllReservations() {
        return ResponseEntity.ok(adminService.getAllReservations());
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        adminService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}