package com.georgeisaev.mmatescollectorsherdog.data.parser;

import com.georgeisaev.mmatescollectorsherdog.domain.Fighter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.jsoup.nodes.Document;

import java.util.Collection;
import java.util.List;

import static com.georgeisaev.mmatescollectorsherdog.utils.ParserUtils.extractAndSet;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum FighterAttributeParserCommand
    implements AttributeParserCommand<Fighter.FighterBuilder, Document> {
  NAME("name", ".fighter-info h1[itemprop=\"name\"] .fn") {
    @Override
    public Fighter.FighterBuilder parse(
        final Document source, final Fighter.FighterBuilder object) {
      extractAndSet(
          source, getSelector(), getAttribute(), object::name, elements -> elements.get(0).html());
      return object;
    }
  };

  String attribute;
  String selector;

  public static Collection<FighterAttributeParserCommand> getAvailableCommands() {
    return List.of(values());
  }
}
