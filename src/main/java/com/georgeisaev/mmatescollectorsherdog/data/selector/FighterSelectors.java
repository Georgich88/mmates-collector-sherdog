package com.georgeisaev.mmatescollectorsherdog.data.selector;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "mmates.sherdog.selector.fighter")
public class FighterSelectors {

  String name;
  String nickname;
  String birthday;
  String addressLocality;
  String addressNationality;
  String heightFeet;
  String heightCm;
  String weightLbs;
  String weightKg;
  String association;
}
