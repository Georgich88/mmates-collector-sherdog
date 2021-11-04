package com.georgeisaev.mmatescollectorsherdog.data.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "mmates_sherdog_fighter")
public class Fighter {

    @Id
    String id;
    String sherdogUrl;
    String pictureUrl;
    String name;
    String nickname;
    LocalDate birthDate;
    String addressLocality;
    String nationality;
    String heightFt;
    String heightCm;
    String weightLbs;
    String weightCm;
    String association;
    String weightClass;
    Long winsTotals;
    Long winsKoTko;
    Long winsSubmissions;
    Long winsDecisions;
    Integer winsOther;
    Long lossesTotals;
    Long lossesKoTko;
    Long lossesSubmissions;
    Long lossesDecisions;
    Integer lossesOther;
    Integer draws;
    Integer nc;

}
