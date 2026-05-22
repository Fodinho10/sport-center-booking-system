package hu.dani.nhf.springnhf.dto;

import hu.dani.nhf.springnhf.enums.SportType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record EquipmentRequestDto(
        @NotBlank String name,
        @NotNull @Min(1) Integer totalInventory,
        @NotNull SportType sportType,
        @NotNull @Min(0) BigDecimal hourlyRate
) {}