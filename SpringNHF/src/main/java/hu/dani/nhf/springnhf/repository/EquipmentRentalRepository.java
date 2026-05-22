package hu.dani.nhf.springnhf.repository;

import hu.dani.nhf.springnhf.entity.EquipmentRental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface EquipmentRentalRepository extends JpaRepository<EquipmentRental, Long> {
    /**
     * Lekérdezi, hogy egy adott eszközből az adott idősávban mennyi van éppen kiadva.
     * A COALESCE biztosítja, hogy ha nincs egyetlen foglalás sem, akkor null helyett 0-t kapjunk.
     */
    @Query("""
        SELECT COALESCE(SUM(er.quantity), 0) FROM EquipmentRental er 
        WHERE er.equipment.id = :equipmentId 
          AND er.reservation.reservationStatus = 'CONFIRMED' 
          AND er.reservation.startTime < :endTime 
          AND er.reservation.endTime > :startTime
    """)
    Integer sumRentedQuantityInTimeRange(
            @Param("equipmentId") Long equipmentId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
