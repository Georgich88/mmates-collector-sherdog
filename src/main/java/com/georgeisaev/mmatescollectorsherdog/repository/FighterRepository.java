package com.georgeisaev.mmatescollectorsherdog.repository;

import com.georgeisaev.mmatescollectorsherdog.data.entity.Fighter;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface FighterRepository extends ReactiveMongoRepository<Fighter, String> {

}
