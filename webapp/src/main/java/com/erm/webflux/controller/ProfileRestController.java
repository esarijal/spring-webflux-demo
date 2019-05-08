package com.erm.webflux.controller;

import com.erm.webflux.data.Profile;
import com.erm.webflux.service.ProfileService;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping(value = "profiles", produces = MediaType.APPLICATION_JSON_VALUE)
@org.springframework.context.annotation.Profile("classic")
public class ProfileRestController {
    private final MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;
    private final ProfileService profileService;

    public ProfileRestController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public Publisher<Profile> getAll(){
        return profileService.all();
    }

    @GetMapping("{id}")
    public Publisher<Profile> getById(@PathVariable String id){
        return profileService.get(id);
    }

    @PostMapping
    public Publisher<ResponseEntity<Profile>> create(@RequestBody Profile profile){
        return profileService.create(profile.getEmail())
                .map(p -> ResponseEntity.created(URI.create("/profiles/" + p.getId()))
                        .contentType(mediaType)
                        .build());
    }

    @DeleteMapping("{id}")
    public Publisher<ResponseEntity<Profile>> deleteById(@PathVariable String id){
        return profileService.delete(id)
                .map(p -> ResponseEntity.noContent().build());
    }

    @PutMapping("{id}")
    public Publisher<ResponseEntity<Profile>> updateById(@PathVariable String id,
                                                         @RequestBody Profile profile){
        return Mono.just(profile)
                .flatMap(p -> profileService.update(id, p.getEmail()))
                .map(p -> ResponseEntity.ok()
                        .contentType(mediaType)
                        .build());
    }


}
