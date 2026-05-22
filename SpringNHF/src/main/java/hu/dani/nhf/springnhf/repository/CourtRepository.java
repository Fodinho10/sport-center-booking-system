package hu.dani.nhf.springnhf.repository;

import hu.dani.nhf.springnhf.entity.Court;
import hu.dani.nhf.springnhf.enums.CourtStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourtRepository extends JpaRepository<Court, Long> {
    List<Court> findAllByCourtStatus(CourtStatus activeStatus);
}
