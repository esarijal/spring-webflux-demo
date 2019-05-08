package com.erm.webflux.repository;

import com.erm.webflux.data.Profile;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ProfileRepository extends ReactiveMongoRepository<Profile, String> {
    Flux<Profile> findByEmailIgnoreCase(String email);
}
