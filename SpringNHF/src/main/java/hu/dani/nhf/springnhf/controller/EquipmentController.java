package hu.dani.nhf.springnhf.controller;

import hu.dani.nhf.springnhf.dto.EquipmentResponseDto;
import hu.dani.nhf.springnhf.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping
    public ResponseEntity<List<EquipmentResponseDto>> getAllEquipment() {
        return ResponseEntity.ok(equipmentService.getAllEquipments());
    }
}
