package com.georgeisaev.mmatescollectorsherdog.service.impl;

import com.georgeisaev.mmatescollectorsherdog.data.dto.FighterDto;
import com.georgeisaev.mmatescollectorsherdog.data.mapper.FighterMapper;
import com.georgeisaev.mmatescollectorsherdog.exception.ParserException;
import com.georgeisaev.mmatescollectorsherdog.repository.FighterRepository;
import com.georgeisaev.mmatescollectorsherdog.service.FighterService;
import com.georgeisaev.mmatescollectorsherdog.utils.ParserUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import static java.lang.Integer.parseInt;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Service
public class FighterServiceImpl implements FighterService {

    private static final String MSG_ERR_CANNOT_PARSE_PROPERTY = "Cannot parse property {} from {}";

    FighterMapper fighterMapper;
    FighterRepository fighterRepository;

    private static final DateTimeFormatter DATE_FORMAT_YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Selectors
    public static final String SELECTOR_FIGHTER_NAME_ELEMENT = ".bio_fighter h1 span.fn";
    public static final String SELECTOR_FIGHTER_NICKNAME_ELEMENT = ".bio_fighter span.nickname em";
    public static final String SELECTOR_FIGHTER_BIRTH_DATE_ELEMENT = "span[itemprop=\"birthDate\"]";
    public static final String SELECTOR_FIGHTER_ADDRESS_LOCALITY_ELEMENT = "span[itemprop=\"addressLocality\"]";
    public static final String SELECTOR_FIGHTER_NATIONALITY_ELEMENT = "strong[itemprop=\"nationality\"]";
    public static final String SELECTOR_FIGHTER_HEIGHT_FEET_ELEMENT = ".size_info .height strong";
    public static final String SELECTOR_FIGHTER_HEIGHT_CM_ELEMENT = ".size_info .height";
    public static final String SELECTOR_FIGHTER_WEIGHT_LBS_ELEMENT = ".size_info .weight strong";
    public static final String SELECTOR_FIGHTER_WINS_ELEMENT = ".bio_graph .counter";
    public static final String SELECTOR_FIGHTER_WINS_METHODS_ELEMENTS = ".bio_graph:first-of-type .graph_tag";
    public static final String SELECTOR_FIGHTER_LOSSES_ELEMENTS = ".bio_graph.loser .counter";
    public static final String SELECTOR_FIGHTER_LOSSES_METHODS_ELEMENT = ".bio_graph.loser .graph_tag";

    // Fighter record
    private static final int METHOD_KO = 0;
    private static final int METHOD_SUBMISSION = 1;
    private static final int METHOD_DECISION = 2;
    private static final int METHOD_OTHERS = 3;

    @Override
    public Mono<FighterDto> save(FighterDto fighterDto) {
        return fighterRepository.save(fighterMapper.toEntity(fighterDto))
                .map(fighterMapper::toEntity);
    }

    @Override
    public FighterDto parse(final String url) throws IOException, ParseException, ParserException {
        log.info("Start. Parse fighterBuilder from {}", url);
        Document doc = ParserUtils.parseDocument(url);
        FighterDto.FighterDtoBuilder fighterBuilder = FighterDto.builder();
        fighterBuilder.sherdogUrl(url);
        extractTextProperty(url, doc, SELECTOR_FIGHTER_NAME_ELEMENT, "name", fighterBuilder::name);
        extractTextProperty(url, doc, SELECTOR_FIGHTER_NICKNAME_ELEMENT, "nickname", fighterBuilder::nickname);
        extractTextProperty(url, doc, SELECTOR_FIGHTER_ADDRESS_LOCALITY_ELEMENT, "addressLocality", fighterBuilder::addressLocality);
        extractTextProperty(url, doc, SELECTOR_FIGHTER_NATIONALITY_ELEMENT, "nationality", fighterBuilder::nationality);
        // Birthday
        try {
            Elements birthday = doc.select(SELECTOR_FIGHTER_BIRTH_DATE_ELEMENT);
            fighterBuilder.birthDate(LocalDate.parse(birthday.get(0).html(), DATE_FORMAT_YYYY_MM_DD));
        } catch (Exception e) {
            log.error(MSG_ERR_CANNOT_PARSE_PROPERTY, "birthDate", url, e);
        }
        // height
        extractTextProperty(url, doc, SELECTOR_FIGHTER_HEIGHT_FEET_ELEMENT, "heightFt", fighterBuilder::heightFt);
        extractTextProperty(url, doc, SELECTOR_FIGHTER_HEIGHT_CM_ELEMENT, "heightCm", fighterBuilder::heightCm);
        // weight
        extractTextProperty(url, doc, SELECTOR_FIGHTER_WEIGHT_LBS_ELEMENT, "weightLbs", fighterBuilder::weightLbs);
        // wins
        extractIntProperty(url, doc, SELECTOR_FIGHTER_WINS_ELEMENT, "winsTotals", fighterBuilder::winsTotals);
        Elements winsMethods = doc.select(SELECTOR_FIGHTER_WINS_METHODS_ELEMENTS);
        extractRecordByMethodProperty(url, winsMethods, METHOD_KO, "winsKoTko", fighterBuilder::winsKoTko);
        extractRecordByMethodProperty(url, winsMethods, METHOD_SUBMISSION, "winsSubmissions", fighterBuilder::winsSubmissions);
        extractRecordByMethodProperty(url, winsMethods, METHOD_DECISION, "winsDecisions", fighterBuilder::winsDecisions);
        extractRecordByMethodProperty(url, winsMethods, METHOD_OTHERS, "winsOther", fighterBuilder::winsOther);
        // loses
        extractIntProperty(url, doc, SELECTOR_FIGHTER_LOSSES_ELEMENTS, "lossesTotals", fighterBuilder::lossesTotals);
        Elements lossesMethods = doc.select(SELECTOR_FIGHTER_LOSSES_METHODS_ELEMENT);
        extractRecordByMethodProperty(url, lossesMethods, METHOD_KO, "lossesKoTko", fighterBuilder::lossesKoTko);
        extractRecordByMethodProperty(url, lossesMethods, METHOD_SUBMISSION, "lossesSubmissions", fighterBuilder::lossesSubmissions);
        extractRecordByMethodProperty(url, lossesMethods, METHOD_DECISION, "lossesDecisions", fighterBuilder::lossesDecisions);
        extractRecordByMethodProperty(url, lossesMethods, METHOD_OTHERS, "lossesOther", fighterBuilder::lossesOther);

        // draws and NC
        Elements drawsNc = doc.select(".right_side .bio_graph .card");
        for (Element element : drawsNc) {
            String html = element.select("span.result").html();
            if ("Draws".equals(html)) {
                fighterBuilder.draws(parseInt(element.select("span.counter").html()));
            } else if ("N/C".equals(html)) {
                fighterBuilder.nc(parseInt(element.select("span.counter").html()));
            }

        }

        try {
            Elements picture = doc.select(".bio_fighter .content img[itemprop=\"image\"]");
            String pictureUrl = "https://www.sherdog.com" + picture.attr("src").trim();
            fighterBuilder.pictureUrl(pictureUrl);
        } catch (Exception e) {
            log.error(MSG_ERR_CANNOT_PARSE_PROPERTY, "pictureUrl", url, e);
        }

        return fighterBuilder.build();
    }

    private void extractRecordByMethodProperty(String url, Elements methods, int method, String property, Consumer<Integer> setter) {
        try {
            setter.accept(parseInt(methods.get(method).html().split(" ")[0]));
        } catch (Exception e) {
            log.error(MSG_ERR_CANNOT_PARSE_PROPERTY, property, url, e);
        }
    }

    private void extractIntProperty(String url, Document doc, String selector, String property, Consumer<Integer> setter) {
        try {
            Elements name = doc.select(selector);
            setter.accept(parseInt(name.get(0).html()));
        } catch (Exception e) {
            log.error(MSG_ERR_CANNOT_PARSE_PROPERTY, property, url, e);
        }
    }

    private void extractTextProperty(String url, Document doc, String selector, String property, Consumer<String> setter) {
        try {
            Elements name = doc.select(selector);
            setter.accept(name.get(0).html());
        } catch (Exception e) {
            log.error(MSG_ERR_CANNOT_PARSE_PROPERTY, property, url, e);
        }
    }

}
