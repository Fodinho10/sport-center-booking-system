package hu.dani.nhf.springnhf.service;

import hu.dani.nhf.springnhf.controller.mapper.CourtMapper;
import hu.dani.nhf.springnhf.dto.CourtResponseDto;
import hu.dani.nhf.springnhf.entity.Court;
import hu.dani.nhf.springnhf.enums.CourtStatus;
import hu.dani.nhf.springnhf.repository.CourtRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourtService {

    private final CourtRepository courtRepository;

    private final CourtMapper courtMapper;

    @Transactional(readOnly = true)
    public List<Court> getActiveCourts() {
        return courtRepository.findAllByCourtStatus(CourtStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public Court getCourtById(Long id) {
        return courtRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pálya nem található ezzel az ID-val: " + id));
    }

    @Transactional(readOnly = true)
    public List<CourtResponseDto> getAllCourts(){
        return courtRepository.findAll().stream()
                .map(courtMapper::toDto)
                .toList();
    }
}