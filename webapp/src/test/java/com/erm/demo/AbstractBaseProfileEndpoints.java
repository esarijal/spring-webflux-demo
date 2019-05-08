package com.erm.demo;

import com.erm.webflux.data.Profile;
import com.erm.webflux.repository.ProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@Slf4j
@WebFluxTest
public abstract class AbstractBaseProfileEndpoints {

    private final WebTestClient client;

    @MockBean
    private ProfileRepository profileRepository;

    public AbstractBaseProfileEndpoints(WebTestClient client) {
        this.client = client;
    }

    @Test
    public void getAll(){
        log.info("running " + this.getClass().getName());

        Mockito
                .when(profileRepository.findAll())
                .thenReturn(Flux.just(
                        new Profile("1", "A"),
                        new Profile("2", "B")
                ));

        client
                .get()
                .uri("/profiles")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo("1")
                .jsonPath("$.[0].email").isEqualTo("A")
                .jsonPath("$.[1].id").isEqualTo("2")
                .jsonPath("$.[1].email").isEqualTo("B");
    }

    @Test
    public void save(){
        Profile data = new Profile("123", UUID.randomUUID().toString() + "@email.com");
        Mockito
                .when(profileRepository.save(any(Profile.class)))
                .thenReturn(Mono.just(data));

        MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;
        client
                .post()
                .uri("/profiles")
                .contentType(mediaType)
                .body(Mono.just(data), Profile.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(mediaType);
    }

    @Test
    public void delete(){
        Profile data = new Profile("123", UUID.randomUUID().toString() + "@email.com");
        Mockito
                .when(profileRepository.findById(data.getId()))
                .thenReturn(Mono.just(data));

        Mockito
                .when(profileRepository.deleteById(data.getId()))
                .thenReturn(Mono.empty());

        client
                .delete()
                .uri("/profiles/" + data.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void update(){
        Profile data = new Profile("123", UUID.randomUUID().toString() + "@email.com");

        Mockito
                .when(profileRepository.findById(data.getId()))
                .thenReturn(Mono.just(data));

        Mockito
                .when(profileRepository.save(data))
                .thenReturn(Mono.just(data));

        client
                .put()
                .uri("/profiles/" + data.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(data), Profile.class)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void getById(){
        Profile data = new Profile("1", "A");

        Mockito
                .when(profileRepository.findById(data.getId()))
                .thenReturn(Mono.just(data));

        client
                .get()
                .uri("/profiles/" + data.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.id").isEqualTo(data.getId())
                .jsonPath("$.email").isEqualTo(data.getEmail());
    }
}
