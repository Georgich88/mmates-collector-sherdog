package com.georgeisaev.mmatescollectorsherdog.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FighterDto {

    @JsonProperty("Id")
    String id;
    @JsonProperty("Sherdog url")
    String sherdogUrl;
    @JsonProperty("Sherdog picture url")
    String pictureUrl;
    @JsonProperty("Name")
    String name;
    @JsonProperty("Nickname")
    String nickname;
    @JsonProperty("Birth date")
    LocalDate birthDate;
    @JsonProperty("Address")
    String addressLocality;
    @JsonProperty("Nationality")
    String nationality;
    @JsonProperty("Height, ft")
    String heightFt;
    @JsonProperty("Height, cm")
    BigDecimal heightCm;
    @JsonProperty("Weight, lbs")
    BigDecimal weightLbs;
    @JsonProperty("Weight, lbs")
    BigDecimal weightCm;
    @JsonProperty("Association")
    String association;
    @JsonProperty("Weight class")
    String weightClass;
    @JsonProperty("Wins")
    Integer winsTotals;
    @JsonProperty("Wins, KO/TKO")
    Integer winsKoTko;
    @JsonProperty("Wins, submission")
    Integer winsSubmissions;
    @JsonProperty("Wins, decisions")
    Integer winsDecisions;
    @JsonProperty("Wins, other")
    Integer winsOther;
    @JsonProperty("Losses")
    Integer lossesTotals;
    @JsonProperty("Losses, KO/TKO")
    Integer lossesKoTko;
    @JsonProperty("Losses, submission")
    Integer lossesSubmissions;
    @JsonProperty("Losses, decisions")
    Integer lossesDecisions;
    @JsonProperty("Losses, other")
    Integer lossesOther;
    @JsonProperty("Draws")
    Integer draws;
    @JsonProperty("No contest")
    Integer nc;

}
