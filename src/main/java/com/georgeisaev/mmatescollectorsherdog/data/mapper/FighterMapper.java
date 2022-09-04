package com.georgeisaev.mmatescollectorsherdog.data.mapper;

import com.georgeisaev.mmatescollectorsherdog.data.document.FighterDoc;
import com.georgeisaev.mmatescollectorsherdog.domain.Fighter;
import org.mapstruct.Mapper;

@Mapper(imports = {FightMapper.class})
public interface FighterMapper {

  FighterDoc toEntity(Fighter model);

  Fighter toDto(FighterDoc dto);
}
