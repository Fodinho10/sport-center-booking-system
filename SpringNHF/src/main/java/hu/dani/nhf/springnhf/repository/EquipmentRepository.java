package hu.dani.nhf.springnhf.repository;

import hu.dani.nhf.springnhf.entity.Equipment;
import hu.dani.nhf.springnhf.enums.EquipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    List<Equipment> findAllByEquipmentStatus(EquipmentStatus equipmentStatus);
}
