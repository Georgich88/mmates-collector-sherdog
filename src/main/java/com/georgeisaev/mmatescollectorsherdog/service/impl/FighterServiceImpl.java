package com.georgeisaev.mmatescollectorsherdog.service.impl;

import com.georgeisaev.mmatescollectorsherdog.data.dto.FighterDto;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.lang.Integer.parseInt;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Service
public class FighterServiceImpl implements FighterService {

    FighterRepository fighterRepository;

    private static final DateTimeFormatter DATE_FORMAT_YYYY_DD_MM = DateTimeFormatter.ofPattern("yyyy-dd-MM");

    // Selectors
    public static final String SELECTOR_FIGHTER_NAME_ELEMENT = ".bio_fighter h1 span.fn";
    public static final String SELECTOR_FIGHTER_NICKNAME_ELEMENT = ".bio_fighter span.nickname em";
    public static final String SELECTOR_FIGHTER_BIRTH_DATE_ELEMENT = "span[itemprop=\"birthDate\"]";
    public static final String SELECTOR_FIGHTER_HEIGHT_ELEMENT = ".size_info .height strong";
    public static final String SELECTOR_FIGHTER_WEIGHT_ELEMENT = ".size_info .weight strong";
    public static final String SELECTOR_FIGHTER_WINS_ELEMENT = ".bio_graph .counter";
    public static final String SELECTOR_FIGHTER_WINS_METHODS_ELEMENTS = ".bio_graph:first-of-type .graph_tag";
    public static final String SELECTOR_FIGHTER_LOSSES_ELEMENT = ".bio_graph.loser .counter";
    public static final String SELECTOR_FIGHTER_LOSSES_METHODS_ELEMENT = ".bio_graph.loser .graph_tag";

    // Fighter record
    private static final int METHOD_KO = 0;
    private static final int METHOD_SUBMISSION = 1;
    private static final int METHOD_DECISION = 2;
    private static final int METHOD_OTHERS = 3;

    @Override
    public void save(FighterDto fighterDto) {

    }

    @Override
    public FighterDto parse(String url) throws IOException, ParseException, ParserException {
        log.info("Start. Parse fighterBuilder from {}", url);
        Document doc = ParserUtils.parseDocument(url);
        FighterDto.FighterDtoBuilder fighterBuilder = FighterDto.builder();

        try {
            Elements name = doc.select(SELECTOR_FIGHTER_NAME_ELEMENT);
            fighterBuilder.name(name.get(0).html());
        } catch (Exception e) {
            // no info, skipping
            log.error("Cannot parse property {} from {}", "name", url, e);
        }
        // Getting nickname
        try {
            Elements nickname = doc.select(SELECTOR_FIGHTER_NICKNAME_ELEMENT);
            fighterBuilder.nickname(nickname.get(0).html());
        } catch (Exception e) {
            log.error("Cannot parse property {} from {}", "nickname", url, e);
        }
        // Birthday
        try {
            Elements birthday = doc.select(SELECTOR_FIGHTER_BIRTH_DATE_ELEMENT);
            fighterBuilder.birthDate(LocalDate.parse(birthday.get(0).html(), DATE_FORMAT_YYYY_DD_MM));
        } catch (Exception e) {
            log.error("Cannot parse property {} from {}", "birthDate", url, e);
        }
        // height
        try {
            Elements height = doc.select(SELECTOR_FIGHTER_HEIGHT_ELEMENT);
            fighterBuilder.heightFt(height.get(0).html());
        } catch (Exception e) {
            log.error("Cannot parse property {} from {}", "nickname", url, e);
        }
        // weight
        try {
            Elements weight = doc.select(SELECTOR_FIGHTER_WEIGHT_ELEMENT);
            fighterBuilder.weightLbs(new BigDecimal(weight.get(0).html()));
        } catch (Exception e) {
            log.error("Cannot parse property {} from {}", "weightLbs", url, e);
        }
        // wins
        try {
            Elements wins = doc.select(SELECTOR_FIGHTER_WINS_ELEMENT);
            fighterBuilder.winsTotals(parseInt(wins.get(0).html()));
        } catch (Exception e) {
            log.error("Cannot parse property {} from {}", "winsTotals", url, e);
        }
        Elements winsMethods = doc.select(SELECTOR_FIGHTER_WINS_METHODS_ELEMENTS);
        try {
            fighterBuilder.winsKoTko(parseInt(winsMethods.get(METHOD_KO).html().split(" ")[0]));
        } catch (Exception e) {
            log.error("Cannot parse property {} from {}", "winsKoTko", url, e);
        }

        try {
            fighterBuilder.winsSubmissions(parseInt(winsMethods.get(METHOD_SUBMISSION).html().split(" ")[0]));
        } catch (Exception e) {
            log.error("Cannot parse property {} from {}", "winsSubmissions", url, e);
        }

        try {
            fighterBuilder.winsDecisions(parseInt(winsMethods.get(METHOD_DECISION).html().split(" ")[0]));
        } catch (Exception e) {
            log.error("Cannot parse property {} from {}", "winsDecisions", url, e);
        }

        try {
            fighterBuilder.winsOther(parseInt(winsMethods.get(METHOD_OTHERS).html().split(" ")[0]));
        } catch (Exception e) {
            log.error("Cannot parse property {} from {}", "winsOther", url, e);
        }
        // loses
        try {
            fighterBuilder.lossesTotals(parseInt(doc.select(SELECTOR_FIGHTER_LOSSES_ELEMENT).get(0).html()));
        } catch (Exception e) {
            log.error("Cannot parse property {} from {}", "lossesTotals", url, e);
        }

        Elements lossesMethods = doc.select(SELECTOR_FIGHTER_LOSSES_METHODS_ELEMENT);

        try {
            fighterBuilder.lossesKoTko((parseInt(lossesMethods.get(METHOD_KO).html().split(" ")[0])));
        } catch (Exception e) {
            log.error("Cannot parse property {} from {}", "lossesKoTko", url, e);
        }

        try {
            fighterBuilder.lossesSubmissions(parseInt(lossesMethods.get(METHOD_SUBMISSION).html().split(" ")[0]));
        } catch (Exception e) {
            log.error("Cannot parse property {} from {}", "lossesSubmissions", url, e);
        }

        try {
            fighterBuilder.lossesDecisions(parseInt(lossesMethods.get(METHOD_DECISION).html().split(" ")[0]));
        } catch (Exception e) {
            log.error("Cannot parse property {} from {}", "lossesDecisions", url, e);
        }

        try {
            fighterBuilder.lossesOther(parseInt(lossesMethods.get(METHOD_OTHERS).html().split(" ")[0]));
        } catch (Exception e) {
            log.error("Cannot parse property {} from {}", "lossesOther", url, e);
        }

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

        Elements picture = doc.select(".bio_fighter .content img[itemprop=\"image\"]");
        String pictureUrl = "https://www.sherdog.com" + picture.attr("src").trim();


        return fighterBuilder.build();
    }

}
