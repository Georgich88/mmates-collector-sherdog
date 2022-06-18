package com.georgeisaev.mmatescollectorsherdog.service.impl;

import com.georgeisaev.mmatescollectorsherdog.data.mapper.FighterMapperImpl;
import com.georgeisaev.mmatescollectorsherdog.data.selector.FighterSelectors;
import com.georgeisaev.mmatescollectorsherdog.service.FighterService;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
@Import({FighterServiceImpl.class, FighterMapperImpl.class, FighterSelectors.class})
class FighterServiceImplIntegrationTest {

  private static final String KHAMZAT_CHIMAEV_URL =
      "https://www.sherdog.com/fighter/Khamzat-Chimaev-280021";

  @Autowired FighterService fighterService;

  @Test
  @DisplayName("Should parse a valid given fighter ULR and produce a valid fighter")
  void shouldParseValidFighterUrl() {

    // GIVEN
    assertNotNull(fighterService);

    // WHEN
    val khamzatChimaev = assertDoesNotThrow(() -> fighterService.parse(KHAMZAT_CHIMAEV_URL));

    // THEN
    assertNotNull(khamzatChimaev);
  }
}
