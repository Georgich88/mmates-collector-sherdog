package com.georgeisaev.mmatescollectorsherdog.service;

import com.georgeisaev.mmatescollectorsherdog.data.dto.FighterDto;

public interface FighterService extends ParserService<FighterDto> {

    void save(FighterDto fighterDto);

}
