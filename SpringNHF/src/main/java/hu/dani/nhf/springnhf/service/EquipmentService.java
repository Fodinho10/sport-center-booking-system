package hu.dani.nhf.springnhf.service;

import hu.dani.nhf.springnhf.controller.mapper.EquipmentMapper;
import hu.dani.nhf.springnhf.dto.EquipmentResponseDto;
import hu.dani.nhf.springnhf.entity.Equipment;
import hu.dani.nhf.springnhf.enums.EquipmentStatus;
import hu.dani.nhf.springnhf.repository.EquipmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    private final EquipmentMapper equipmentMapper;

    @Transactional(readOnly = true)
    public List<Equipment> getAvailableEquipment() {
        return equipmentRepository.findAllByEquipmentStatus(EquipmentStatus.AVAILABLE);
    }

    @Transactional(readOnly = true)
    public Equipment getEquipmentById(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Eszköz nem található ezzel az ID-val: " + id));
    }

    @Transactional(readOnly = true)
    public List<EquipmentResponseDto> getAllEquipments(){
        return equipmentRepository.findAll().stream()
                        .map(equipmentMapper::toDto)
                        .toList();
    }
}