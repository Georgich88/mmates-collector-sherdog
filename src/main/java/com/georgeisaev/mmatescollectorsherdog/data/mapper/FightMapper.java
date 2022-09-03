package com.georgeisaev.mmatescollectorsherdog.data.mapper;

import com.georgeisaev.mmatescollectorsherdog.data.document.FightDoc;
import com.georgeisaev.mmatescollectorsherdog.domain.Fight;
import org.mapstruct.Mapper;

@Mapper
public interface FightMapper {

  FightDoc toEntity(Fight dto);

  Fight toEntity(FightDoc dto);
}
