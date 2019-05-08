package com.erm.webflux.service;

import com.erm.webflux.data.Profile;
import com.erm.webflux.event.ProfileCreatedEvent;
import com.erm.webflux.repository.ProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ProfileService {
    private final ApplicationEventPublisher publisher;
    private final ProfileRepository profileRepository;

    public ProfileService(ApplicationEventPublisher publisher, ProfileRepository profileRepository) {
        this.publisher = publisher;
        this.profileRepository = profileRepository;
    }

    public Flux<Profile> all(){
        return profileRepository.findAll();
    }

    public Mono<Profile> get(String id){
        return profileRepository.findById(id);
    }

    public Mono<Profile> update(String id, String email){
        return profileRepository
                .findById(id)
                .map(profile -> new Profile(profile.getId(), email))
                .flatMap(profileRepository::save);
    }

    public Mono<Profile> delete(String id){
        return profileRepository
                .findById(id)
                .flatMap(profile -> profileRepository
                        .deleteById(profile.getId())
                        .thenReturn(profile));
    }

    public Mono<Profile> create(String email){
        return profileRepository
                .save(new Profile(null, email))
                .doOnSuccess(profile -> publisher.publishEvent(new ProfileCreatedEvent(profile)));
    }

    public Flux<Profile> findByEmail(String email){
        return profileRepository.findByEmailIgnoreCase(email);
    }

}
