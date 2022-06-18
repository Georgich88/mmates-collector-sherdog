package com.georgeisaev.mmatescollectorsherdog.data.mapper;

import com.georgeisaev.mmatescollectorsherdog.data.dto.FightDto;
import com.georgeisaev.mmatescollectorsherdog.data.entity.Fight;
import org.mapstruct.Mapper;

@Mapper
public interface FightMapper {

  Fight toEntity(FightDto dto);

  FightDto toEntity(Fight dto);
}
