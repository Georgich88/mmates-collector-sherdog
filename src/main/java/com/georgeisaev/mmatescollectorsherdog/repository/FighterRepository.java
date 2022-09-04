package com.georgeisaev.mmatescollectorsherdog.repository;

import com.georgeisaev.mmatescollectorsherdog.data.document.FighterDoc;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface FighterRepository extends ReactiveMongoRepository<FighterDoc, String> {}
