package hu.dani.nhf.springnhf.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import hu.dani.nhf.springnhf.enums.ReservationStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonPropertyOrder({
        "id",
        "status",
        "userName",
        "userEmail",
        "sportType",
        "courtName",
        "startTime",
        "endTime",
        "rentedEquipment",
        "totalCost"
})
public class ReservationResponseDto {

    private Long id;
    private String userName;
    private String userEmail;
    private String courtName;
    private String sportType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal totalCost;
    private ReservationStatus status;
    private List<RentedEquipmentDto> rentedEquipment;

    @Getter
    @Setter
    @JsonPropertyOrder({
            "equipmentId",
            "equipmentName",
            "quantity",
            "hourlyRate"
    })
    public static class RentedEquipmentDto {
        private Long equipmentId;
        private String equipmentName;
        private Integer quantity;
        private BigDecimal hourlyRate;
    }
}