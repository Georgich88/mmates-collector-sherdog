package com.georgeisaev.mmatescollectorsherdog.service;

import com.georgeisaev.mmatescollectorsherdog.data.dto.FighterDto;
import reactor.core.publisher.Mono;

public interface FighterService extends ParserService<FighterDto> {

    Mono<FighterDto> save(FighterDto fighterDto);

}
