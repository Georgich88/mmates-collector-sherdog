package com.georgeisaev.mmatescollectorsherdog.utils;

import com.georgeisaev.mmatescollectorsherdog.data.enumerators.FightResult;
import com.georgeisaev.mmatescollectorsherdog.exception.IllegalSherdogUrlException;
import lombok.experimental.UtilityClass;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import static com.georgeisaev.mmatescollectorsherdog.common.SherdogParserConstants.PARSING_TIMEOUT;

@UtilityClass
public class ParserUtils {

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

}
