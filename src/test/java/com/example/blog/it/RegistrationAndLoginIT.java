package com.example.blog.it;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegistrationAndLoginIT {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void integrationTest() {
        // ## Arrange ##
        // ## Act ##
        var responserSpec = webTestClient.get().uri("/").exchange();
        // ## Assert ##
        responserSpec.expectStatus().isNoContent();
    }
}
