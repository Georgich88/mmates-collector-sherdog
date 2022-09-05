package com.georgeisaev.mmatescollectorsherdog.service.impl;

import com.georgeisaev.mmatescollectorsherdog.data.enumerators.FightResult;
import com.georgeisaev.mmatescollectorsherdog.data.enumerators.FightType;
import com.georgeisaev.mmatescollectorsherdog.data.enumerators.WinMethod;
import com.georgeisaev.mmatescollectorsherdog.data.parser.JsopAttributeParserCommand;
import com.georgeisaev.mmatescollectorsherdog.data.parser.fighter.FighterAttributeParserCommand;
import com.georgeisaev.mmatescollectorsherdog.data.parser.fighter.FighterFightsAttributeParserCommand;
import com.georgeisaev.mmatescollectorsherdog.data.parser.fighter.FighterRecordAttributeParserCommand;
import com.georgeisaev.mmatescollectorsherdog.domain.Event;
import com.georgeisaev.mmatescollectorsherdog.domain.Fight;
import com.georgeisaev.mmatescollectorsherdog.domain.Fighter;
import com.georgeisaev.mmatescollectorsherdog.domain.FighterRecord;
import com.georgeisaev.mmatescollectorsherdog.exception.ParserException;
import com.georgeisaev.mmatescollectorsherdog.service.FighterParserService;
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
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.georgeisaev.mmatescollectorsherdog.common.SherdogParserConstants.BASE_HTTPS_URL;
import static com.georgeisaev.mmatescollectorsherdog.utils.ParserUtils.MSG_ERR_CANNOT_PARSE_PROPERTY;
import static com.georgeisaev.mmatescollectorsherdog.utils.ParserUtils.defineIdFromSherdogUrl;
import static java.util.Comparator.nullsFirst;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class FighterParserServiceImpl implements FighterParserService {


  @Override
  public Fighter parse(final String url) throws IOException, ParseException, ParserException {
    log.info("Start. Parse Fighter from {}", url);

    val fighterParserCommands =
            Stream.concat(FighterAttributeParserCommand.availableCommands().stream(),
                    FighterFightsAttributeParserCommand.availableCommands().stream())
                    .toList();
    val recordParserCommands
            = FighterRecordAttributeParserCommand.availableCommands();

    val doc = ParserUtils.parseDocument(url);
    val builder = parse(doc, Fighter.builder(), fighterParserCommands);
    val fighterRecord = parse(doc, FighterRecord.builder(), recordParserCommands ).build();

    builder.sherdogUrl(url)
            .id(defineIdFromSherdogUrl(url))
            .record(fighterRecord);

    return builder
        .build()
        .postConstruct();
  }

  // NEW METHODS

  public <T, C extends JsopAttributeParserCommand<T>> T parse(
      Document source, T target, Collection<C> commands) {
    commands.forEach(c -> {
      try {
        c.parse(source, target);
      } catch (Exception e) {
        log.error(MSG_ERR_CANNOT_PARSE_PROPERTY, c.getAttribute(), target, e);
      }
    });
    return target;
  }
}
