package com.georgeisaev.mmatescollectorsherdog.service.impl;

import com.georgeisaev.mmatescollectorsherdog.domain.Fighter;
import com.georgeisaev.mmatescollectorsherdog.data.mapper.FighterMapper;
import com.georgeisaev.mmatescollectorsherdog.repository.FighterRepository;
import com.georgeisaev.mmatescollectorsherdog.service.FighterService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Service
public class FighterServiceImpl implements FighterService {

  // Repositories
  FighterRepository fighterRepository;
  // Mappers
  FighterMapper fighterMapper;

  @Override
  public Mono<Fighter> save(Mono<Fighter> fighter) {
    return fighter
        .map(fighterMapper::toEntity)
        .flatMap(fighterRepository::save)
        .map(fighterMapper::toDto);
  }

  @Override
  public Mono<Fighter> findById(String fighterId) {
    return fighterRepository.findById(fighterId).map(fighterMapper::toDto);
  }
}
