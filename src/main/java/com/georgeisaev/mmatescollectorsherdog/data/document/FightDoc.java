package com.georgeisaev.mmatescollectorsherdog.data.document;

import com.georgeisaev.mmatescollectorsherdog.data.enumerators.FightResult;
import com.georgeisaev.mmatescollectorsherdog.data.enumerators.FightType;
import com.georgeisaev.mmatescollectorsherdog.data.enumerators.WinMethod;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FightDoc {

  String eventId;
  String firstFighterId;
  String secondFighterId;
  LocalDate date;
  FightResult result;
  WinMethod winMethod;
  Integer winTime;
  Integer winRound;
  FightType type;
}
