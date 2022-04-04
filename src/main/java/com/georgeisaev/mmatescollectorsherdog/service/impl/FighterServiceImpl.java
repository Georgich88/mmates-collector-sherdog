package com.georgeisaev.mmatescollectorsherdog.service.impl;

import com.georgeisaev.mmatescollectorsherdog.data.dto.EventDto;
import com.georgeisaev.mmatescollectorsherdog.data.dto.FightDto;
import com.georgeisaev.mmatescollectorsherdog.data.dto.FighterDto;
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

    private static final String DRAWS_ELEMENT = "Draws";
    private static final String NC_ELEMENT = "N/C";
    private static final DateTimeFormatter DATE_TIME_FORMATTER_MMM_DD_YYYY = DateTimeFormatter.ofPattern("MMM / dd / " +
            "yyyy", Locale.US);

    // Repositories
    FighterRepository fighterRepository;

    // Mappers
    FighterMapper fighterMapper;

    // Properties
    FighterSelectors selectors;

    private static final DateTimeFormatter DATE_FORMAT_YYYY_MM_DD = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    // Selectors
    public static final String SELECTOR_FIGHTER_WEIGHT_LBS_ELEMENT = ".size_info .weight strong";
    public static final String SELECTOR_FIGHTER_WINS_ELEMENT = ".winloses.win";
    public static final String SELECTOR_FIGHTER_WINS_METHODS_ELEMENTS = ".bio_graph:first-of-type .graph_tag";
    public static final String SELECTOR_FIGHTER_LOSSES_ELEMENTS = ".bio_graph.loser .counter";
    public static final String SELECTOR_FIGHTER_LOSSES_METHODS_ELEMENT = ".bio_graph.loser .graph_tag";

    // Fighter record
    private static final int METHOD_KO = 0;
    private static final int METHOD_SUBMISSION = 1;
    private static final int METHOD_DECISION = 2;
    private static final int METHOD_OTHERS = 3;

    // Re
    private static final int COLUMN_RESULT = 0;
    private static final int COLUMN_OPPONENT = 1;
    private static final int COLUMN_EVENT = 2;
    private static final int COLUMN_METHOD = 3;
    private static final int COLUMN_ROUND = 4;
    private static final int COLUMN_TIME = 5;

    @Override
    public Mono<FighterDto> save(Mono<FighterDto> fighter) {
        return fighter.map(fighterMapper::toEntity)
                .flatMap(fighterRepository::save)
                .map(fighterMapper::toDto);
    }

    @Override
    public Mono<FighterDto> findById(String fighterId) {
        return fighterRepository.findById(fighterId)
                .map(fighterMapper::toDto);
    }

    @Override
    public FighterDto parse(final String url) throws IOException, ParseException, ParserException {
        log.info("Start. Parse Fighter from {}", url);
        Document doc = ParserUtils.parseDocument(url);
        FighterDto.FighterDtoBuilder builder = FighterDto.builder();
        builder.sherdogUrl(url);
        builder.id(defineIdFromSherdogUrl(url));
        extractAndSetText(doc, selectors.getName(), "name", builder::name);
        extractAndSetText(doc, selectors.getNickname(), "nickname", builder::nickname);
        extractAndSetText(doc, selectors.getAddressLocality(), "addressLocality", builder::addressLocality);
        extractAndSetText(doc, selectors.getAddressNationality(), "nationality", builder::nationality);
        // Birthday
        try {
            Elements birthday = doc.select(selectors.getBirthday());
            builder.birthDate(DateTimeUtils.parseDate(birthday.get(0).html()));
        } catch (Exception e) {
            log.error(MSG_ERR_CANNOT_PARSE_PROPERTY, "birthDate", url, e);
        }
        // height
        extractAndSetText(doc, selectors.getHeightFeet(), "heightFt", builder::heightFt);
        extractAndSetText(doc, selectors.getHeightCm(), "heightCm", builder::heightCm);
        // weight
        extractAndSetText(doc, selectors.getWeightLbs(), "weightLbs", builder::weightLbs);
        extractAndSetText(doc, selectors.getWeightKg(), "weightKg", builder::weightKg);
        // wins
        extractIntProperty(url, doc, SELECTOR_FIGHTER_WINS_ELEMENT, "winsTotals", builder::winsTotals);
        Elements winsMethods = doc.select(SELECTOR_FIGHTER_WINS_METHODS_ELEMENTS);
        extractRecordByMethodProperty(url, winsMethods, METHOD_KO, "winsKoTko", builder::winsKoTko);
        extractRecordByMethodProperty(url, winsMethods, METHOD_SUBMISSION, "winsSubmissions",
                builder::winsSubmissions);
        extractRecordByMethodProperty(url, winsMethods, METHOD_DECISION, "winsDecisions",
                builder::winsDecisions);
        extractRecordByMethodProperty(url, winsMethods, METHOD_OTHERS, "winsOther", builder::winsOther);
        // loses
        extractIntProperty(url, doc, SELECTOR_FIGHTER_LOSSES_ELEMENTS, "lossesTotals", builder::lossesTotals);
        Elements lossesMethods = doc.select(SELECTOR_FIGHTER_LOSSES_METHODS_ELEMENT);
        extractRecordByMethodProperty(url, lossesMethods, METHOD_KO, "lossesKoTko", builder::lossesKoTko);
        extractRecordByMethodProperty(url, lossesMethods, METHOD_SUBMISSION, "lossesSubmissions",
                builder::lossesSubmissions);
        extractRecordByMethodProperty(url, lossesMethods, METHOD_DECISION, "lossesDecisions",
                builder::lossesDecisions);
        extractRecordByMethodProperty(url, lossesMethods, METHOD_OTHERS, "lossesOther", builder::lossesOther);

        // draws and NC
        Elements drawsNc = doc.select(".right_side .bio_graph .card");
        for (Element element : drawsNc) {
            String html = element.select("span.result").html();
            if (DRAWS_ELEMENT.equals(html)) {
                builder.draws(parseInt(element.select("span.counter").html()));
            } else if (NC_ELEMENT.equals(html)) {
                builder.nc(parseInt(element.select("span.counter").html()));
            }

        }
        try {
            Elements picture = doc.select(".bio_fighter .content img[itemprop=\"image\"]");
            String pictureUrl = BASE_HTTPS_URL + picture.attr("src").trim();
            builder.pictureUrl(pictureUrl);
        } catch (Exception e) {
            log.error(MSG_ERR_CANNOT_PARSE_PROPERTY, "pictureUrl", url, e);
        }
        Elements fightTables = doc.select(".fight_history");
        log.info("Found {} fight history tables", fightTables.size());
        builder.fights(new ArrayList<>());
        FighterDto fighter = builder.build();

        fightTables.stream()
                // excluding upcoming fights
                .filter(div -> !div.select(".module_header h2").html().trim().contains("Upcoming"))
                .collect(Collectors.groupingBy(div -> {
                    String categoryName = div.select(".module_header h2").html().trim()
                            .replaceAll("(?i)FIGHT HISTORY - ", "").trim();
                    return FightType.fromString(categoryName);
                })).forEach((key, div) -> div.stream()
                        .map(d -> d.select(".table table tr"))
                        .filter(tdList -> !tdList.isEmpty())
                        .findFirst().ifPresent(tdList -> {
                            List<FightDto> f = parseFights(tdList, fighter);
                            f.forEach(fight -> fight.setType(key));
                            fighter.getFights().addAll(f);
                        }));

        List<FightDto> sorted = fighter.getFights().stream()
                .sorted(Comparator.comparing(FightDto::getDate, nullsFirst(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        fighter.setFights(sorted);

        log.info("Found {} fights for {}", fighter.getFights().size(), fighter.getName());

        return builder.build();
    }

    private void extractRecordByMethodProperty(String url, Elements methods, int method, String property,
                                               IntConsumer setter) {
        try {
            setter.accept(parseInt(methods.get(method).html().split(" ")[0]));
        } catch (Exception e) {
            log.error(MSG_ERR_CANNOT_PARSE_PROPERTY, property, url, e);
        }
    }

    private void extractIntProperty(String url, Document doc, String selector, String property, IntConsumer setter) {
        try {
            Elements name = doc.select(selector);
            setter.accept(parseInt(name.get(0).html()));
        } catch (Exception e) {
            log.error(MSG_ERR_CANNOT_PARSE_PROPERTY, property, url, e);
        }
    }

    /**
     * Get a fighter fights
     *
     * @param tableRows JSOUP TRs document
     * @param fighter   a fighter to parse against
     */
    private List<FightDto> parseFights(Elements tableRows, FighterDto fighter) throws ArrayIndexOutOfBoundsException {
        List<FightDto> fights = new ArrayList<>();
        log.info("{} TRs to parse through", tableRows.size());
        // removing header row...
        if (!tableRows.isEmpty()) {
            tableRows.remove(0);
            tableRows.forEach(tr -> {
                FightDto.FightDtoBuilder fightBuilder = FightDto.builder();
                fightBuilder.firstFighterId(fighter.getId());
                Elements dataCells = tr.select("td");
                fightBuilder.result(parseFightResult(dataCells.get(COLUMN_RESULT)));
                FighterDto opponent = parseOpponent(dataCells.get(COLUMN_OPPONENT));
                fightBuilder.secondFighterId(opponent.getId());
                fightBuilder.eventId(parseEvent(dataCells.get(COLUMN_EVENT)).getId());
                fightBuilder.date(parseDate(dataCells.get(COLUMN_EVENT)));
                fightBuilder.winMethod(WinMethod.defineWinMethod(parseWinMethod(dataCells.get(COLUMN_METHOD))));
                fightBuilder.winRound(parseWinRound(dataCells.get(COLUMN_ROUND)));
                fightBuilder.winTime(parseWinTime(dataCells.get(COLUMN_TIME)));
                fightBuilder.name(fighter.getName() + " VS " + opponent.getName());
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
    private FighterDto parseOpponent(Element td) {
        var opponent = FighterDto.builder();
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
    private EventDto parseEvent(Element td) {
        Element link = td.select("a").get(0);
        var eventBuilder = EventDto.builder();
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

    public void extractAndSetText(Document doc, String selector, String propertyName, Consumer<String> setter) {
        extractAndSet(doc, selector, propertyName, setter, elements -> elements.get(0).html());
    }


}
