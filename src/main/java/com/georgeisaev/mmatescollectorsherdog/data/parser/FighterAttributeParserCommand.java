package com.georgeisaev.mmatescollectorsherdog.data.parser;

import com.georgeisaev.mmatescollectorsherdog.domain.Fighter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.jsoup.nodes.Document;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import static com.georgeisaev.mmatescollectorsherdog.utils.ParserUtils.extractAndSet;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum FighterAttributeParserCommand
    implements AttributeParserCommand<Fighter.FighterBuilder, Document> {
  NAME("name", ".fighter-info h1[itemprop=\"name\"] .fn"),

  NICKNAME("nickname", ".fighter-info h1[itemprop=\"name\"] .nickname em"),

  ADDRESS_LOCALITY("addressLocality", ".adr .locality"),

  NATIONALITY("addressLocality", "[itemprop=\"nationality\"]");

  String attribute;
  String selector;

  public static Collection<FighterAttributeParserCommand> getAvailableCommands() {
    return List.of(values());
  }

  @Override
  public void parse(final Document source, final Fighter.FighterBuilder object) {
    final Method setter =
        ReflectionUtils.findMethod(Fighter.FighterBuilder.class, attribute, String.class);
    if (setter == null) return;
    extractAndSet(
        source,
        getSelector(),
        getAttribute(),
        s -> ReflectionUtils.invokeMethod(setter, object, s),
        elements -> elements.get(0).html());
  }
}
