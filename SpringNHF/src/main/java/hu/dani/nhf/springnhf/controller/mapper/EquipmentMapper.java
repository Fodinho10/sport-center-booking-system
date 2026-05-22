package hu.dani.nhf.springnhf.controller.mapper;

import hu.dani.nhf.springnhf.dto.EquipmentResponseDto;
import hu.dani.nhf.springnhf.entity.Equipment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {
    EquipmentResponseDto toDto(Equipment equipment);
}
