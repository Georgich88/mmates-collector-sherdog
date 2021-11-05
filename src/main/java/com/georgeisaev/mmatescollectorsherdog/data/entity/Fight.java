package com.georgeisaev.mmatescollectorsherdog.data.entity;

import com.georgeisaev.mmatescollectorsherdog.data.enumerators.FightResult;
import com.georgeisaev.mmatescollectorsherdog.data.enumerators.FightType;
import com.georgeisaev.mmatescollectorsherdog.data.enumerators.WinMethod;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.ZonedDateTime;

import static com.georgeisaev.mmatescollectorsherdog.data.enumerators.FightResult.NOT_HAPPENED;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Fight {

    String name;
    Event event;
    String firstFighterId;
    String secondFighterId;
    ZonedDateTime date;
    FightResult result = NOT_HAPPENED;
    WinMethod winMethod;
    int winTime; // in seconds
    int winRound;
    FightType type;

}

