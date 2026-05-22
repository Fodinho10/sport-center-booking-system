package hu.dani.nhf.springnhf.service;

import hu.dani.nhf.springnhf.controller.mapper.ReservationMapper;
import hu.dani.nhf.springnhf.dto.ReservationRequestDto;
import hu.dani.nhf.springnhf.dto.ReservationResponseDto;
import hu.dani.nhf.springnhf.entity.*;
import hu.dani.nhf.springnhf.enums.ReservationStatus;
import hu.dani.nhf.springnhf.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CourtRepository courtRepository;
    private final UserRepository userRepository;
    private final EquipmentRepository equipmentRepository;
    private final EquipmentRentalRepository equipmentRentalRepository;
    private final ReservationMapper reservationMapper;

    @Transactional
    public ReservationResponseDto createReservation(Long userId, ReservationRequestDto dto) {
        validateTimeSlot(dto.getStartTime(), dto.getEndTime());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Felhasználó nem található!"));
        Court court = courtRepository.findById(dto.getCourtId())
                .orElseThrow(() -> new EntityNotFoundException("Pálya nem található!"));

        validateCourtAvailability(court.getId(), dto.getStartTime(), dto.getEndTime());

        BigDecimal durationInHours = calculateDurationInHours(dto.getStartTime(), dto.getEndTime());

        Reservation reservation = createBaseReservation(user, court, dto);

        List<EquipmentRental> rentals = processEquipmentRentals(reservation, dto.getEquipmentItems(), dto.getStartTime(), dto.getEndTime());

        BigDecimal totalCost = calculateTotalCost(court, rentals, durationInHours);
        reservation.setTotalCost(totalCost);

        Reservation savedReservation = reservationRepository.save(reservation);
        if (!rentals.isEmpty()) {
            equipmentRentalRepository.saveAll(rentals);
        }

        return mapToResponseDto(savedReservation, rentals);
    }

    private void validateTimeSlot(LocalDateTime start, LocalDateTime end) {
        if (start.isBefore(LocalDateTime.now()) || end.isBefore(start)) {
            throw new IllegalArgumentException("Érvénytelen idősáv!");
        }
    }

    private void validateCourtAvailability(Long courtId, LocalDateTime start, LocalDateTime end) {
        if (reservationRepository.isCourtBooked(courtId, start, end)) {
            throw new IllegalStateException("A pálya már foglalt ebben az időpontban!");
        }
    }

    private BigDecimal calculateDurationInHours(LocalDateTime start, LocalDateTime end) {
        return BigDecimal.valueOf(Duration.between(start, end).toMinutes())
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotalCost(Court court, List<EquipmentRental> rentals, BigDecimal hours) {
        BigDecimal courtCost = court.getHourlyRate().multiply(hours);
        BigDecimal equipmentCost = rentals.stream()
                .map(r -> r.getEquipment().getHourlyRate()
                        .multiply(BigDecimal.valueOf(r.getQuantity()))
                        .multiply(hours))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return courtCost.add(equipmentCost);
    }

    private List<EquipmentRental> processEquipmentRentals(Reservation res, List<ReservationRequestDto.EquipmentSelectionDto> items, LocalDateTime start, LocalDateTime end) {
        List<EquipmentRental> rentals = new ArrayList<>();
        if (items == null) return rentals;

        for (var item : items) {
            Equipment equipment = equipmentRepository.findById(item.getEquipmentId())
                    .orElseThrow(() -> new EntityNotFoundException("Eszköz nem található!"));

            checkEquipmentCapacity(equipment, item.getQuantity(), start, end);

            EquipmentRental rental = new EquipmentRental();
            rental.setReservation(res);
            rental.setEquipment(equipment);
            rental.setQuantity(item.getQuantity());
            rentals.add(rental);
        }
        return rentals;
    }

    private void checkEquipmentCapacity(Equipment equipment, Integer requested, LocalDateTime start, LocalDateTime end) {
        Integer currentlyRented = equipmentRentalRepository.sumRentedQuantityInTimeRange(equipment.getId(), start, end);
        if (currentlyRented == null) {
            currentlyRented = 0;
        }
        if (currentlyRented + requested > equipment.getTotalInventory()) {
            throw new IllegalStateException("Nincs elég készlet: " + equipment.getName());
        }
    }

    private Reservation createBaseReservation(User user, Court court, ReservationRequestDto dto) {
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setCourt(court);
        reservation.setStartTime(dto.getStartTime());
        reservation.setEndTime(dto.getEndTime());
        reservation.setReservationStatus(ReservationStatus.CONFIRMED);
        return reservation;
    }

    private ReservationResponseDto mapToResponseDto(Reservation reservation, List<EquipmentRental> rentals) {
        ReservationResponseDto response = new ReservationResponseDto();
        response.setId(reservation.getId());

        response.setUserName(reservation.getUser().getName());
        response.setUserEmail(reservation.getUser().getEmail());
        response.setCourtName(reservation.getCourt().getName());
        response.setSportType(reservation.getCourt().getSportType().name());

        response.setStartTime(reservation.getStartTime());
        response.setEndTime(reservation.getEndTime());
        response.setTotalCost(reservation.getTotalCost());
        response.setStatus(reservation.getReservationStatus());

        List<ReservationResponseDto.RentedEquipmentDto> equipmentDtos = rentals.stream()
                .map(rental -> {
                    ReservationResponseDto.RentedEquipmentDto eqDto = new ReservationResponseDto.RentedEquipmentDto();
                    eqDto.setEquipmentId(rental.getEquipment().getId());
                    eqDto.setEquipmentName(rental.getEquipment().getName());
                    eqDto.setQuantity(rental.getQuantity());
                    eqDto.setHourlyRate(rental.getEquipment().getHourlyRate());
                    return eqDto;
                }).toList();

        response.setRentedEquipment(equipmentDtos);
        return response;
    }

    @Transactional
    public ReservationResponseDto cancelReservation(Long reservationId, Long currentUserId, boolean isAdmin) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Foglalás nem található!"));

        if (!isAdmin && !Objects.equals(reservation.getUser().getId(), currentUserId)) {
            throw new AccessDeniedException("Nincs jogosultságod lemondani ezt a foglalást!");
        }

        if (reservation.getReservationStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Ez a foglalás már le van mondva!");
        }

        reservation.setReservationStatus(ReservationStatus.CANCELLED);
        Reservation savedReservation = reservationRepository.save(reservation);

        return reservationMapper.toDto(savedReservation);
    }

    @Transactional(readOnly = true)
    public List<Reservation> getReservationByUserId(Long userId){
        return reservationRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getMyReservationsDto(Long userId) {
        List<Reservation> reservations = reservationRepository.findAllByUserId(userId);
        return reservations.stream()
                .map(reservationMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getConfirmedReservationsByCourt(Long courtId) {

        List<Reservation> reservations = reservationRepository.findByCourtIdAndReservationStatus(courtId, ReservationStatus.CONFIRMED);
        return reservations.stream()
                .map(reservationMapper::toDto)
                .toList();
    }
}