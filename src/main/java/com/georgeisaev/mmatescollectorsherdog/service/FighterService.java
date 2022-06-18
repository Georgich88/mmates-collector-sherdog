package com.georgeisaev.mmatescollectorsherdog.service;

import com.georgeisaev.mmatescollectorsherdog.data.dto.FighterDto;
import reactor.core.publisher.Mono;

public interface FighterService extends ParserService<FighterDto> {

  Mono<FighterDto> save(Mono<FighterDto> fighterDto);

  Mono<FighterDto> findById(String fighterId);
}
