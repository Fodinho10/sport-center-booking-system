package hu.dani.nhf.springnhf.service;

import hu.dani.nhf.springnhf.controller.mapper.CourtMapper;
import hu.dani.nhf.springnhf.controller.mapper.EquipmentMapper;
import hu.dani.nhf.springnhf.controller.mapper.ReservationMapper;
import hu.dani.nhf.springnhf.dto.*;
import hu.dani.nhf.springnhf.entity.Court;
import hu.dani.nhf.springnhf.entity.Equipment;
import hu.dani.nhf.springnhf.enums.CourtStatus;
import hu.dani.nhf.springnhf.enums.EquipmentStatus;
import hu.dani.nhf.springnhf.repository.CourtRepository;
import hu.dani.nhf.springnhf.repository.EquipmentRepository;
import hu.dani.nhf.springnhf.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CourtRepository courtRepository;
    private final EquipmentRepository equipmentRepository;
    private final ReservationRepository reservationRepository;

    private final CourtMapper courtMapper;
    private final EquipmentMapper equipmentMapper;
    private final ReservationMapper reservationMapper;

    // --- PÁLYA METÓDUSOK ---

    @Transactional
    public CourtResponseDto createCourt(CourtRequestDto dto) {
        Court court = new Court();
        court.setName(dto.name());
        court.setSportType(dto.sportType());
        court.setHourlyRate(dto.hourlyRate());
        court.setCourtStatus(CourtStatus.ACTIVE);

        return courtMapper.toDto(courtRepository.save(court));
    }

    @Transactional
    public CourtResponseDto updateCourt(Long id, CourtRequestDto dto) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pálya nem található!"));

        court.setName(dto.name());
        court.setSportType(dto.sportType());
        court.setHourlyRate(dto.hourlyRate());

        return courtMapper.toDto(courtRepository.save(court));
    }

    // --- ESZKÖZ METÓDUSOK ---

    @Transactional
    public EquipmentResponseDto createEquipment(EquipmentRequestDto dto) {
        Equipment equipment = new Equipment();
        equipment.setName(dto.name());
        equipment.setTotalInventory(dto.totalInventory());
        equipment.setHourlyRate(dto.hourlyRate());
        equipment.setSportType(dto.sportType());
        equipment.setEquipmentStatus(EquipmentStatus.AVAILABLE);

        return equipmentMapper.toDto(equipmentRepository.save(equipment));
    }

    @Transactional
    public EquipmentResponseDto updateEquipment(Long id, EquipmentRequestDto dto) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Eszköz nem található!"));

        equipment.setName(dto.name());
        equipment.setTotalInventory(dto.totalInventory());
        equipment.setHourlyRate(dto.hourlyRate());

        return equipmentMapper.toDto(equipmentRepository.save(equipment));
    }

    // --- FOGLALÁS METÓDUSOK ---

    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(reservationMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new RuntimeException("Foglalás nem található!");
        }
        reservationRepository.deleteById(id);
    }
}