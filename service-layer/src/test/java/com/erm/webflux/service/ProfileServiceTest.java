package com.erm.webflux.service;

import com.erm.webflux.data.Profile;
import com.erm.webflux.repository.ProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Predicate;

@Slf4j
@DataMongoTest
@Import(ProfileService.class)
public class ProfileServiceTest {

    private final ProfileService service;
    private final ProfileRepository repository;

    public ProfileServiceTest(@Autowired ProfileService service,
                              @Autowired ProfileRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @Test
    public void getAll() {
        Flux<Profile> saved = repository.saveAll(Flux.just(
                new Profile(null, "Josh"),
                new Profile(null, "Matt"),
                new Profile(null, "Jane")
        ));

        // action
        Flux<Profile> composite = service.all().thenMany(saved);

        Predicate<Profile> match =
                profile -> saved.any(saveItem -> saveItem.equals(profile)).block();

        StepVerifier
                .create(composite)
                .expectNextMatches(match)
                .expectNextMatches(match)
                .expectNextMatches(match)
                .verifyComplete();
    }

    @Test
    public void save() {
        String email = "esa.rijal@gmail.com";
        Mono<Profile> profileMono = service.create(email);
        StepVerifier.create(profileMono)
                .expectNextMatches(saved ->
                        StringUtils.hasText(saved.getId()) && saved.getEmail().equals(email))
                .verifyComplete();
    }

    @Test
    public void delete() {
        String test = "test@mail.com";
        Mono<Profile> deleted = service.create(test)
                .flatMap(saved -> service.delete(saved.getId()));

        StepVerifier.create(deleted)
                .expectNextMatches(profile -> profile.getEmail().equalsIgnoreCase(test))
                .verifyComplete();

    }

    @Test
    public void update() {
        Mono<Profile> updated = service.create("test")
                .flatMap(p -> service.update(p.getId(), "test1"));

        StepVerifier.create(updated)
                .expectNextMatches(p -> p.getEmail().equalsIgnoreCase("test1"))
                .verifyComplete();

    }

    @Test
    public void getById(){
        Mono<Profile> profileMono =
                service.create("esa@gmail.com")
                .flatMap(saved -> service.get(saved.getId()));

        StepVerifier.create(profileMono)
                .expectNextMatches(profile -> profile.getEmail().equalsIgnoreCase("esa.rijal" +
                        "@gmail.com"));

    }

    @Test
    public void findByEmail(){
        String email = "baca@mail.com";
        Flux<Profile> block = service.create(email)
                .map(profile -> service.findByEmail(email)).block();

        StepVerifier.create(block)
                .expectNextMatches(profile -> profile.getEmail().equalsIgnoreCase(email));
    }
}