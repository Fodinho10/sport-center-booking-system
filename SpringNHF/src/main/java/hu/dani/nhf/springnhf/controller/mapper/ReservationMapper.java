package hu.dani.nhf.springnhf.controller.mapper;

import hu.dani.nhf.springnhf.dto.ReservationResponseDto;
import hu.dani.nhf.springnhf.entity.EquipmentRental;
import hu.dani.nhf.springnhf.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(source = "court.name", target = "courtName")
    @Mapping(source = "court.sportType", target = "sportType")
    @Mapping(source = "reservationStatus", target = "status")
    @Mapping(source = "equipmentRentals", target = "rentedEquipment")
    ReservationResponseDto toDto(Reservation reservation);

    @Mapping(source = "equipment.id", target = "equipmentId")
    @Mapping(source = "equipment.name", target = "equipmentName")
    @Mapping(source = "equipment.hourlyRate", target = "hourlyRate")
    ReservationResponseDto.RentedEquipmentDto toRentedEquipmentDto(EquipmentRental equipmentRental);
}
