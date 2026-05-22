package hu.dani.nhf.springnhf.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReservationRequestDto {

    @NotNull(message = "A pálya kiválasztása kötelező!")
    private Long courtId;

    @NotNull(message = "A kezdési időpont megadása kötelező!")
    @Future(message = "A foglalás kezdete csak a jövőben lehet!")
    private LocalDateTime startTime;

    @NotNull(message = "A befejezési időpont megadása kötelező!")
    @Future(message = "A foglalás vége csak a jövőben lehet!")
    private LocalDateTime endTime;

    @Valid
    private List<EquipmentSelectionDto> equipmentItems;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class EquipmentSelectionDto {

        @NotNull(message = "Az eszköz azonosítója nem lehet üres!")
        private Long equipmentId;

        @NotNull(message = "A darabszám megadása kötelező!")
        @Min(value = 1, message = "Legalább 1 darabot kell bérelni!")
        private Integer quantity;
    }
}