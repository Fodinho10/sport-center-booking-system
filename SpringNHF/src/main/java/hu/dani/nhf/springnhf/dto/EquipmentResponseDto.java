package hu.dani.nhf.springnhf.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonPropertyOrder({
        "id",
        "name",
        "sportType",
        "totalInventory",
        "hourlyRate",
        "equipmentStatus"
})
public class EquipmentResponseDto {
    private Long id;
    private String name;
    private String sportType;
    private Integer totalInventory;
    private BigDecimal hourlyRate;
    private String equipmentStatus;
}
