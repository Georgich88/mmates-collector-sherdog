package com.georgeisaev.mmatescollectorsherdog.utils;

import lombok.experimental.UtilityClass;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
        return Jsoup.connect(url).timeout(PARSING_TIMEOUT)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("https://www.google.com").get();
    }

}
