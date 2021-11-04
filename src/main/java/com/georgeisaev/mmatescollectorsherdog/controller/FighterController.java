package com.georgeisaev.mmatescollectorsherdog.controller;

import com.georgeisaev.mmatescollectorsherdog.data.dto.FighterDto;
import com.georgeisaev.mmatescollectorsherdog.service.FighterService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.text.ParseException;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@RestController("/fighters")
public class FighterController {

    FighterService fighterService;

    @PostMapping
    public ResponseEntity<Mono<FighterDto>> save(@RequestBody FighterDto fighterDto) {
        return ResponseEntity.ok(fighterService.save(fighterDto));
    }

    @PostMapping("/parsings")
    public ResponseEntity<Mono<FighterDto>> parse(@RequestBody String sherdogUrl) throws IOException, ParseException {
        return ResponseEntity.ok(Mono.just(fighterService.parse(sherdogUrl)));
    }

}
