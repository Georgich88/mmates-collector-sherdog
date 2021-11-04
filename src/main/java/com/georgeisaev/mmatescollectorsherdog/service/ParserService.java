package com.georgeisaev.mmatescollectorsherdog.service;

import com.georgeisaev.mmatescollectorsherdog.exception.ParserException;

import java.io.IOException;
import java.text.ParseException;

public interface ParserService<T> {

    /**
     * Parses a page by url
     *
     * @param url of the site page
     * @return the object parsed by the parser
     * @throws IOException     if connecting to Sherdog fails
     * @throws ParseException  if the page structure has changed
     * @throws ParserException if anything related to the parser goes wrong
     */
    T parse(String url) throws IOException, ParseException, ParserException;

}
