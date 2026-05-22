package hu.dani.nhf.springnhf.controller;

import hu.dani.nhf.springnhf.dto.CourtResponseDto;
import hu.dani.nhf.springnhf.service.CourtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/courts")
@RequiredArgsConstructor
public class CourtController {

    private final CourtService courtService;

    @GetMapping
    public ResponseEntity<List<CourtResponseDto>> getAllCourts() {
        return ResponseEntity.ok(courtService.getAllCourts());
    }
}
