package hu.dani.nhf.springnhf.repository;

import hu.dani.nhf.springnhf.entity.Reservation;
import hu.dani.nhf.springnhf.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByUserId(Long userId);

    /**
     * Pálya ütközésvizsgálat: Igennel tér vissza, ha a pálya adott idősávban már foglalt.
     */
    @Query("""
        SELECT COUNT(r) > 0 FROM Reservation r 
        WHERE r.court.id = :courtId 
          AND r.reservationStatus = 'CONFIRMED' 
          AND r.startTime < :endTime 
          AND r.endTime > :startTime
    """)
    boolean isCourtBooked(
            @Param("courtId") Long courtId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    List<Reservation> findByCourtIdAndReservationStatus(Long courtId, ReservationStatus reservationStatus);
}
