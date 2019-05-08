package com.erm.webflux.handler;

import com.erm.webflux.data.Profile;
import com.erm.webflux.service.ProfileService;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class ProfileHandler {
    private final ProfileService profileService;

    public ProfileHandler(ProfileService profileService) {
        this.profileService = profileService;
    }

    private static Mono<ServerResponse> defaultCreatedResponse(Publisher<Profile> profiles) {
        return Mono.from(profiles)
                .flatMap(p -> ServerResponse
                        .created(URI.create("/profiles/" + p.getId()))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .build());
    }

    private static Mono<ServerResponse> defaultOkResponse(Publisher<Profile> profiles){
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(profiles, Profile.class);
    }

    private static String id(ServerRequest r){
        return r.pathVariable("id");
    }

    public Mono<ServerResponse> getById(ServerRequest r){
        return defaultOkResponse(profileService.get(id(r)));
    }

    public Mono<ServerResponse> all(ServerRequest r){
        return defaultOkResponse(profileService.all());
    }

    public Mono<ServerResponse> deleteById(ServerRequest r){
        return Mono.from(profileService.delete(id(r)))
                    .flatMap(profile -> ServerResponse
                            .noContent()
                            .build());
    }

    public Mono<ServerResponse> updateById(ServerRequest r){
        Flux<Profile> id = r.bodyToFlux(Profile.class)
                .flatMap(p -> profileService.update(id(r), p.getEmail()));
        return defaultOkResponse(id);
    }

    public Mono<ServerResponse> create(ServerRequest r){
        Flux<Profile> flux = r.bodyToFlux(Profile.class)
                .flatMap(toWrite -> profileService.create(toWrite.getEmail()));
        return defaultCreatedResponse(flux);
    }
}
