package com.georgeisaev.mmatescollectorsherdog.service.impl;

import com.georgeisaev.mmatescollectorsherdog.domain.Event;
import com.georgeisaev.mmatescollectorsherdog.domain.Fight;
import com.georgeisaev.mmatescollectorsherdog.domain.Fighter;
import com.georgeisaev.mmatescollectorsherdog.data.enumerators.FightResult;
import com.georgeisaev.mmatescollectorsherdog.data.enumerators.FightType;
import com.georgeisaev.mmatescollectorsherdog.data.enumerators.WinMethod;
import com.georgeisaev.mmatescollectorsherdog.data.mapper.FighterMapper;
import com.georgeisaev.mmatescollectorsherdog.data.selector.FighterSelectors;
import com.georgeisaev.mmatescollectorsherdog.exception.ParserException;
import com.georgeisaev.mmatescollectorsherdog.repository.FighterRepository;
import com.georgeisaev.mmatescollectorsherdog.service.FighterService;
import com.georgeisaev.mmatescollectorsherdog.utils.DateTimeUtils;
import com.georgeisaev.mmatescollectorsherdog.utils.ParserUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

import static com.georgeisaev.mmatescollectorsherdog.common.SherdogParserConstants.BASE_HTTPS_URL;
import static com.georgeisaev.mmatescollectorsherdog.utils.ParserUtils.MSG_ERR_CANNOT_PARSE_PROPERTY;
import static com.georgeisaev.mmatescollectorsherdog.utils.ParserUtils.defineIdFromSherdogUrl;
import static com.georgeisaev.mmatescollectorsherdog.utils.ParserUtils.extractAndSet;
import static java.lang.Integer.parseInt;
import static java.util.Comparator.nullsFirst;

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
