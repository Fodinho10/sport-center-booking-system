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
        "hourlyRate",
        "courtStatus"
})
public class CourtResponseDto {
    private Long id;
    private String name;
    private String sportType;
    private BigDecimal hourlyRate;
    private String courtStatus;
}
