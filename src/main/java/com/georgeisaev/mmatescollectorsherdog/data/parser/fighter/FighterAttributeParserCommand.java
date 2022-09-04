package com.georgeisaev.mmatescollectorsherdog.data.parser.fighter;

import com.georgeisaev.mmatescollectorsherdog.data.parser.JsopAttributeParserCommand;
import com.georgeisaev.mmatescollectorsherdog.domain.Fighter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;


@Slf4j
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum FighterAttributeParserCommand
    implements JsopAttributeParserCommand<Fighter.FighterBuilder> {

  // name
  NAME("name", ".fighter-info h1[itemprop=\"name\"] .fn"),
  NICKNAME("nickname", ".fighter-info h1[itemprop=\"name\"] .nickname em"),
  // birthDate
  BIRTH_DATE("birthDate", ".fighter-data span[itemprop=\"birthDate\"]"),
  // address
  ADDRESS_LOCALITY("addressLocality", ".adr .locality"),
  NATIONALITY("addressLocality", "[itemprop=\"nationality\"]"),
  // height
  HEIGHT_FT(
      "heightFt",
      ".fighter-data .bio-holder tr:nth-child(2) td:nth-child(2) b[itemprop=\"height\"]"),
  HEIGHT_CM("heightCm", ".fighter-data .bio-holder tr:nth-child(2) td:nth-child(2)"),
  // weight
  WEIGHT_KG(
      "weightKg",
      ".fighter-data .bio-holder tr:nth-child(2) td:nth-child(2) b[itemprop=\"height\"]"),
  WEIGHT_LBS("weightLbs", ".fighter-data .bio-holder tr:nth-child(2) td:nth-child(2)");

  /** Attribute name */
  String attribute;

  /** CSS-like element selector, that finds elements matching a query */
  String selector;

  public static Collection<FighterAttributeParserCommand> availableCommands() {
    return List.of(values());
  }
}
