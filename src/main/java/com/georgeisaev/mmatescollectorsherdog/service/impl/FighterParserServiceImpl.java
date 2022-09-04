package com.georgeisaev.mmatescollectorsherdog.service.impl;

import com.georgeisaev.mmatescollectorsherdog.data.enumerators.FightResult;
import com.georgeisaev.mmatescollectorsherdog.data.enumerators.FightType;
import com.georgeisaev.mmatescollectorsherdog.data.enumerators.WinMethod;
import com.georgeisaev.mmatescollectorsherdog.data.parser.JsopAttributeParserCommand;
import com.georgeisaev.mmatescollectorsherdog.data.parser.fighter.FighterAttributeParserCommand;
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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.georgeisaev.mmatescollectorsherdog.common.SherdogParserConstants.BASE_HTTPS_URL;
import static com.georgeisaev.mmatescollectorsherdog.utils.ParserUtils.MSG_ERR_CANNOT_PARSE_PROPERTY;
import static com.georgeisaev.mmatescollectorsherdog.utils.ParserUtils.defineIdFromSherdogUrl;
import static java.util.Comparator.nullsFirst;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class FighterParserServiceImpl implements FighterParserService {

  private static final DateTimeFormatter DATE_TIME_FORMATTER_MMM_DD_YYYY =
      DateTimeFormatter.ofPattern("MMM / dd / " + "yyyy", Locale.US);
  // Re
  private static final int COLUMN_RESULT = 0;
  private static final int COLUMN_OPPONENT = 1;
  private static final int COLUMN_EVENT = 2;
  private static final int COLUMN_METHOD = 3;
  private static final int COLUMN_ROUND = 4;
  private static final int COLUMN_TIME = 5;


  @Override
  public Fighter parse(final String url) throws IOException, ParseException, ParserException {
    log.info("Start. Parse Fighter from {}", url);
    val doc = ParserUtils.parseDocument(url);
    val builder = Fighter.builder();
    builder.sherdogUrl(url);
    builder.id(defineIdFromSherdogUrl(url));
    parse(doc, builder, FighterAttributeParserCommand.availableCommands());
    builder.record(
            parse(doc, FighterRecord.builder(), FighterRecordAttributeParserCommand.availableCommands())
                    .build());

    try {
      Elements picture = doc.select(".bio_fighter .content img[itemprop=\"image\"]");
      String pictureUrl = BASE_HTTPS_URL + picture.attr("src").trim();
      builder.pictureUrl(pictureUrl);
    } catch (Exception e) {
      log.error(MSG_ERR_CANNOT_PARSE_PROPERTY, "pictureUrl", url, e);
    }
    Elements fightTables = doc.select(".module fight_history");
    log.info("Found {} fight history tables", fightTables.size());
    builder.fights(new ArrayList<>());
    Fighter fighter = builder.build();

    fightTables.stream()
        // excluding upcoming fights
        .filter(div -> !div.select(".module_header h2").html().trim().contains("Upcoming"))
        .collect(
            Collectors.groupingBy(
                div -> {
                  String categoryName =
                      div.select(".module_header h2")
                          .html()
                          .trim()
                          .replaceAll("(?i)FIGHT HISTORY - ", "")
                          .trim();
                  return FightType.fromString(categoryName);
                }))
        .forEach(
            (key, div) ->
                div.stream()
                    .map(d -> d.select(".table table tr"))
                    .filter(tdList -> !tdList.isEmpty())
                    .findFirst()
                    .ifPresent(
                        tdList -> {
                          List<Fight> f = parseFights(tdList, fighter);
                          f.forEach(fight -> fight.setType(key));
                          fighter.getFights().addAll(f);
                        }));

    List<Fight> sorted =
        fighter.getFights().stream()
            .sorted(Comparator.comparing(Fight::getDate, nullsFirst(Comparator.naturalOrder())))
            .toList();
    fighter.setFights(sorted);

    log.info("Found {} fights for {}", fighter.getFights().size(), fighter.getName());

    return builder.build();
  }


  /**
   * Get a fighter fights
   *
   * @param tableRows JSOUP TRs document
   * @param fighter a fighter to parse against
   */
  private List<Fight> parseFights(Elements tableRows, Fighter fighter)
      throws ArrayIndexOutOfBoundsException {
    List<Fight> fights = new ArrayList<>();
    log.info("{} TRs to parse through", tableRows.size());
    // removing header row...
    if (!tableRows.isEmpty()) {
      tableRows.remove(0);
      tableRows.forEach(
          tr -> {
            Elements dataCells = tr.select("td");
            Fighter opponent = parseOpponent(dataCells.get(COLUMN_OPPONENT));

            val fightBuilder = Fight.builder()
                    .firstFighterId(fighter.getId())
                    .result(parseFightResult(dataCells.get(COLUMN_RESULT)))
                    .secondFighterId(opponent.getId())
                    .eventId(parseEvent(dataCells.get(COLUMN_EVENT)).getId())
                    .date(parseDate(dataCells.get(COLUMN_EVENT)))
                    .winMethod(WinMethod.defineWinMethod(parseWinMethod(dataCells.get(COLUMN_METHOD))))
                    .winRound(parseWinRound(dataCells.get(COLUMN_ROUND)))
                    .winTime(parseWinTime(dataCells.get(COLUMN_TIME)))
                    .name(fighter.getName() + " VS " + opponent.getName());

            fights.add(fightBuilder.build());
            log.info("{}", fightBuilder);
          });
    }

    return fights;
  }

  /**
   * Get the fight result
   *
   * @param td a td from sherdogs table
   * @return a fight result enum
   */
  private FightResult parseFightResult(Element td) {
    return ParserUtils.parseFightResult(td);
  }

  /**
   * Get the fight result
   *
   * @param td a td from sherdogs table
   * @return a fight result enum
   */
  private Fighter parseOpponent(Element td) {
    var opponent = Fighter.builder();
    Element opponentLink = td.select("a").get(0);
    opponent.name(opponentLink.html());
    final String sherdogUrl = opponentLink.attr("abs:href");
    opponent.sherdogUrl(sherdogUrl);
    opponent.id(defineIdFromSherdogUrl(sherdogUrl));
    return opponent.build();
  }

  /**
   * Get the fight event
   *
   * @param td a td from sherdogs table
   * @return a sherdog base object with url and name
   */
  private Event parseEvent(Element td) {
    Element link = td.select("a").get(0);
    var eventBuilder = Event.builder();
    eventBuilder.name(link.html().replaceAll("<span itemprop=\"award\">|</span>", ""));
    final String sherdogUrl = link.attr("abs:href");
    eventBuilder.sherdogUrl(sherdogUrl);
    eventBuilder.id(defineIdFromSherdogUrl(sherdogUrl));
    return eventBuilder.build();
  }

  /**
   * Get the date of the fight
   *
   * @param td a td from sherdogs table
   * @return the local datetime of the fight
   */
  private LocalDate parseDate(Element td) {
    // date
    Element dateElement = td.select("span.sub_line").first();
    try {
      assert dateElement != null;
      return LocalDate.parse(dateElement.text(), DATE_TIME_FORMATTER_MMM_DD_YYYY);
    } catch (Exception e) {
      log.debug("", e);
    }
    try {
      TextNode dateText = (TextNode) dateElement.childNode(0);
      return LocalDate.parse(dateText.text(), DATE_TIME_FORMATTER_MMM_DD_YYYY);
    } catch (Exception e) {
      log.debug("", e);
    }
    return null;
  }

  /**
   * Get the winning method
   *
   * @param td a td from sherdogs table
   * @return a string with the finishing method
   */
  private String parseWinMethod(Element td) {
    return td.html().replaceAll("<br>(.*)", "");
  }

  /**
   * Get the winning round
   *
   * @param td a td from sherdogs table
   * @return an integer
   */
  private int parseWinRound(Element td) {
    return Integer.parseInt(td.html());
  }

  /**
   * Get time of win
   *
   * @param td a td from sherdogs table
   * @return the time of win
   */
  private int parseWinTime(Element td) {
    try {
      SimpleDateFormat minutesSecondsDateFormat = new SimpleDateFormat("mm:ss");
      Date date = minutesSecondsDateFormat.parse(td.html());
      return date.getSeconds();

    } catch (ParseException e) {
      return 0;
    }
  }

  // NEW METHODS

  public <T, C extends JsopAttributeParserCommand<T>> T parse(
      Document source,
      T target,
      Collection<C> commands) {
    commands.forEach(c -> c.parse(source, target));
    return target;
  }

}
