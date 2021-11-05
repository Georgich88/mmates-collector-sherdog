package com.georgeisaev.mmatescollectorsherdog.data.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "mmates_sherdog_promotion")
public class Promotion {

    @Id
    String id;
    String sherdogUrl;
    String name;

}
