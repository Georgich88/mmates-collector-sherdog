package com.georgeisaev.mmatescollectorsherdog.data.parser;

public interface AttributeParserCommand<T, S> {

  T parse(S source, T target);
}
