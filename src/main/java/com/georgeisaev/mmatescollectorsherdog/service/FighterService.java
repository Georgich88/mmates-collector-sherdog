package com.georgeisaev.mmatescollectorsherdog.service;

import com.georgeisaev.mmatescollectorsherdog.data.dto.FighterDto;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.concurrent.Flow;

public interface FighterService extends ParserService<FighterDto> {

    Mono<FighterDto> save(Mono<FighterDto> fighterDto);

    Mono<FighterDto> findById(String fighterId);

}
