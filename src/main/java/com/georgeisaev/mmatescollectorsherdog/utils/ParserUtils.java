package com.georgeisaev.mmatescollectorsherdog.utils;

import com.georgeisaev.mmatescollectorsherdog.data.enumerators.FightResult;
import com.georgeisaev.mmatescollectorsherdog.exception.IllegalSherdogUrlException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.georgeisaev.mmatescollectorsherdog.common.SherdogParserConstants.PARSING_TIMEOUT;

@Slf4j
@UtilityClass
public class ParserUtils {

    public static final String MSG_ERR_CANNOT_PARSE_PROPERTY = "Cannot parse property {} from {}";

    /**
     * Parses a URL with all the required parameters
     *
     * @param url of the document to parse
     * @return the jsoup document
     * @throws IOException if the connection fails
     */
    public static Document parseDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .timeout(PARSING_TIMEOUT)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("https://www.google.com").get();
    }

    /**
     * Defines id from Sherdog url
     *
     * @param sherdogUrl sherdog item url
     * @return id from url
     */
    public static String defineIdFromSherdogUrl(String sherdogUrl) {
        String[] parts = sherdogUrl.split("/");
        if (parts.length > 0) {
            return parts[parts.length - 1];
        } else {
            throw new IllegalSherdogUrlException("Cannot define fighter ID from url");
        }
    }

    /**
     * Gets the result of a fight following sherdog website win/lose/draw/nc Make
     * sure to use on Fighter1 only
     *
     * @param element Jsoup element
     * @return a FightResult
     */
    public static FightResult parseFightResult(Element element) {
        if (!element.select(".win").isEmpty()) {
            return FightResult.FIGHTER_1_WIN;
        } else if (!element.select(".loss").isEmpty()) {
            return FightResult.FIGHTER_2_WIN;
        } else if (!element.select(".draw").isEmpty()) {
            return FightResult.DRAW;
        } else if (!element.select(".no_contest").isEmpty()) {
            return FightResult.NO_CONTEST;
        } else {
            return FightResult.NOT_HAPPENED;
        }
    }

    public static <T> void extractAndSet(Document doc, String selector, String propertyName, Consumer<T> setter,
                                         Function<Elements, T> extractor) {
        try {
            setter.accept(extractor.apply(doc.select(selector)));
        } catch (Exception e) {
            log.error(MSG_ERR_CANNOT_PARSE_PROPERTY, propertyName, doc.baseUri(), e);
        }
    }

}
