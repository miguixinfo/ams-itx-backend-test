package com.miguixinfo.ams.backend.test.similar_products.infrastructure.client;

import com.miguixinfo.ams.backend.test.similar_products.domain.exception.ProductNotFoundException;
import com.miguixinfo.ams.backend.test.similar_products.infrastructure.config.ProductApiProperties;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

class ProductApiClientTest {

    private MockWebServer mockWebServer;
    private ProductApiClient client;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/").toString();
        ProductApiProperties properties = new ProductApiProperties(baseUrl, 1);
        WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
        client = new ProductApiClient(webClient, properties);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void getSimilarIds_happyPath_returnsIds() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("[\"2\",\"3\",\"4\"]")
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(client.getSimilarIds("1"))
                .expectNext("2", "3", "4")
                .verifyComplete();
    }

    @Test
    void getSimilarIds_404upstream_propagatesProductNotFoundException() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        StepVerifier.create(client.getSimilarIds("99"))
                .expectErrorMatches(e ->
                        e instanceof ProductNotFoundException &&
                        e.getMessage().contains("99")
                )
                .verify();
    }

    @Test
    void getProductDetail_happyPath_returnsProductDetail() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"id\":\"2\",\"name\":\"Dress\",\"price\":19.99,\"availability\":true}")
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(client.getProductDetail("2"))
                .expectNextMatches(p ->
                        p.id().equals("2") &&
                        p.name().equals("Dress") &&
                        p.availability()
                )
                .verifyComplete();
    }

    @Test
    void getProductDetail_404upstream_returnsEmpty() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        StepVerifier.create(client.getProductDetail("99"))
                .verifyComplete();
    }

    @Test
    void getProductDetail_500upstream_returnsEmpty() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        StepVerifier.create(client.getProductDetail("1"))
                .verifyComplete();
    }

    @Test
    void getProductDetail_timeoutExceeded_returnsEmpty() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"id\":\"2\",\"name\":\"Dress\",\"price\":19.99,\"availability\":true}")
                .addHeader("Content-Type", "application/json")
                .setBodyDelay(2, TimeUnit.SECONDS));

        StepVerifier.create(client.getProductDetail("2"))
                .verifyComplete();
    }
}
