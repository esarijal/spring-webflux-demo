package com.erm.demo;

import com.erm.webflux.config.ProfileEndpointConfiguration;
import com.erm.webflux.controller.ProfileRestController;
import com.erm.webflux.handler.ProfileHandler;
import com.erm.webflux.service.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@Slf4j
@ActiveProfiles("default")
@Import({
        ProfileEndpointConfiguration.class,
        ProfileHandler.class,
        ProfileService.class
})
public class FunctionalProfileEndpointsTest extends AbstractBaseProfileEndpoints {
    public FunctionalProfileEndpointsTest(@Autowired WebTestClient client) {
        super(client);
    }

    @BeforeAll
    static void before(){
        log.info("running default " + ProfileRestController.class.getName() + " tests");
    }
}
