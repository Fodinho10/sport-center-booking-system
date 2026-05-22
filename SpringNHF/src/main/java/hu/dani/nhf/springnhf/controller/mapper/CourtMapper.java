package hu.dani.nhf.springnhf.controller.mapper;

import hu.dani.nhf.springnhf.dto.CourtResponseDto;
import hu.dani.nhf.springnhf.entity.Court;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourtMapper {
    CourtResponseDto toDto(Court court);

}
