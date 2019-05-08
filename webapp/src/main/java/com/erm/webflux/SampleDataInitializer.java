package com.erm.webflux;

import com.erm.webflux.data.Profile;
import com.erm.webflux.repository.ProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Slf4j
@Component
@org.springframework.context.annotation.Profile("demo")
public class SampleDataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final ProfileRepository profileRepository;

    public SampleDataInitializer(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        profileRepository.deleteAll()
                .thenMany(
                        Flux
                        .just("A", "B", "C", "D")
                        .map(name -> new Profile(UUID.randomUUID().toString(), name + "@email.com"))
                        .flatMap(profileRepository::save)
                )
                .thenMany(profileRepository.findAll())
                .subscribe(profile -> log.info("Saving : "  + profile.toString()));
    }
}
