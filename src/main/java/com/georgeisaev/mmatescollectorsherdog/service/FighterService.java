package com.georgeisaev.mmatescollectorsherdog.service;

import com.georgeisaev.mmatescollectorsherdog.data.dto.FighterDto;
import com.georgeisaev.mmatescollectorsherdog.data.entity.Fighter;
import reactor.core.publisher.Mono;

public interface FighterService extends ParserService<FighterDto> {

    Mono<FighterDto> save(FighterDto fighterDto);

}
