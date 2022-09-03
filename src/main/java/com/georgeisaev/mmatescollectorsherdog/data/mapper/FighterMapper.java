package com.georgeisaev.mmatescollectorsherdog.data.mapper;

import com.georgeisaev.mmatescollectorsherdog.domain.Fighter;
import org.mapstruct.Mapper;

@Mapper(imports = {FightMapper.class})
public interface FighterMapper {

  com.georgeisaev.mmatescollectorsherdog.data.document.Fighter toEntity(Fighter dto);

  Fighter toDto(com.georgeisaev.mmatescollectorsherdog.data.document.Fighter dto);
}
