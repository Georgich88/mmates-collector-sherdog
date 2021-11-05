package com.georgeisaev.mmatescollectorsherdog.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "Promotion details")
public class PromotionDto {

    String id;
    String sherdogUrl;
    String name;

}
