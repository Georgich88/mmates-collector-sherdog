package com.georgeisaev.mmatescollectorsherdog.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "Event details")
public class EventDto {

    String id;
    String sherdogUrl;
    String name;
    PromotionDto promotion;
    String ownership;
    LocalDate date;
    List<FightDto> fights;
    String location;
    String venue;
    String enclosure;

}
