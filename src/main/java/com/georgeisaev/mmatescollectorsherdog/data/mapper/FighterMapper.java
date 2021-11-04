package com.georgeisaev.mmatescollectorsherdog.data.mapper;

import com.georgeisaev.mmatescollectorsherdog.data.dto.FighterDto;
import com.georgeisaev.mmatescollectorsherdog.data.entity.Fighter;
import org.mapstruct.Mapper;

@Mapper
public interface FighterMapper {

    Fighter toEntity(FighterDto dto);

    FighterDto toEntity(Fighter dto);

}
