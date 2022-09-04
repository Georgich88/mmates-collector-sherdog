package com.georgeisaev.mmatescollectorsherdog.data.parser;

public interface AttributeParserCommand<T, S> {

  void parse(S source, T target);
}
