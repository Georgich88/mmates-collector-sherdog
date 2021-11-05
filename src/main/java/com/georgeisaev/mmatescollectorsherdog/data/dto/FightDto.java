package com.georgeisaev.mmatescollectorsherdog.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.georgeisaev.mmatescollectorsherdog.data.enumerators.FightResult;
import com.georgeisaev.mmatescollectorsherdog.data.enumerators.FightType;
import com.georgeisaev.mmatescollectorsherdog.data.enumerators.WinMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "Fighter details")
public class FightDto {

    String name;
    EventDto event;
    String firstFighterId;
    String secondFighterId;
    LocalDateTime date;
    FightResult result;
    WinMethod winMethod;
    Integer winTime;
    Integer winRound;
    FightType type;

}

