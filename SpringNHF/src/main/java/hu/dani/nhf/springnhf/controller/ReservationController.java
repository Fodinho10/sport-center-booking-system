package hu.dani.nhf.springnhf.controller;

import hu.dani.nhf.springnhf.dto.ReservationRequestDto;
import hu.dani.nhf.springnhf.dto.ReservationResponseDto;
import hu.dani.nhf.springnhf.security.UserDetailsImpl;
import hu.dani.nhf.springnhf.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponseDto> createReservation(
            @Valid @RequestBody ReservationRequestDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        // 1. Kinyerjük az éppen bejelentkezett felhasználó adatbázis ID-ját a JWT tokenből
        Long userId = userDetails.getUser().getId();

        // 2. Meghívjuk a golyóálló, tranzakcionális üzleti logikát
        ReservationResponseDto response = reservationService.createReservation(userId, dto);

        // 3. Visszaadjuk a sikeres 201 Created státuszkódot és a lapos JSON DTO-t
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * FOGLALÁS LEMONDÁSA
     * Végpont: POST /api/reservations/{id}/cancel
     * Elérés: A Service-be írt @PreAuthorize védi (csak a tulajdonos vagy ADMIN hívhatja meg).
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ReservationResponseDto> cancelReservation(
            @PathVariable("id") Long reservationId,
            @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        Long currentUserId = userDetails.getUser().getId();

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(authority -> Objects.equals(authority.getAuthority(), "ROLE_ADMIN"));

        ReservationResponseDto canceledDto = reservationService.cancelReservation(reservationId, currentUserId, isAdmin);
        return ResponseEntity.ok(canceledDto);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponseDto>> getMyReservations(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();

        List<ReservationResponseDto> reservationDtos = reservationService.getMyReservationsDto(userId);

        return ResponseEntity.ok(reservationDtos);
    }

    @GetMapping("/court/{courtId}")
    public ResponseEntity<List<ReservationResponseDto>> getReservationsForCourt(@PathVariable Long courtId) {
        List<ReservationResponseDto> bookedSlots = reservationService.getConfirmedReservationsByCourt(courtId);
        return ResponseEntity.ok(bookedSlots);
    }
}