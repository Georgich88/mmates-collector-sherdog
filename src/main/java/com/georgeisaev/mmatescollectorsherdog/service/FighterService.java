package com.georgeisaev.mmatescollectorsherdog.service;

import com.georgeisaev.mmatescollectorsherdog.domain.Fighter;
import reactor.core.publisher.Mono;

public interface FighterService {

  Mono<Fighter> save(Mono<Fighter> fighterDto);

  Mono<Fighter> findById(String fighterId);
}
