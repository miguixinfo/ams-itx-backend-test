package com.miguixinfo.ams.backend.test.similar_products.interfaces.rest;

import com.miguixinfo.ams.backend.test.similar_products.domain.exception.ProductNotFoundException;
import com.miguixinfo.ams.backend.test.similar_products.domain.model.ProductDetail;
import com.miguixinfo.ams.backend.test.similar_products.port.in.GetSimilarProductsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SimilarProductsControllerTest {

        @LocalServerPort
        private int port;

        private WebTestClient webTestClient;

        @MockitoBean
        private GetSimilarProductsUseCase getSimilarProductsUseCase;

        @BeforeEach
        void setUp() {
                webTestClient = WebTestClient.bindToServer()
                                .baseUrl("http://localhost:" + port)
                                .build();
        }

        @Test
        void givenValidProductId_returns200WithProductDetails() {
                ProductDetail p1 = new ProductDetail("2", "Dress", BigDecimal.valueOf(19.99), true);
                ProductDetail p2 = new ProductDetail("3", "Blazer", BigDecimal.valueOf(29.99), false);

                when(getSimilarProductsUseCase.getSimilarProducts("1"))
                                .thenReturn(Flux.just(p1, p2));

                webTestClient.get()
                                .uri("/product/1/similar")
                                .exchange()
                                .expectStatus().isOk()
                                .expectBodyList(Object.class)
                                .hasSize(2);
        }

        @Test
        void givenUnknownProductId_returns404() {
                when(getSimilarProductsUseCase.getSimilarProducts("99"))
                                .thenReturn(Flux.error(new ProductNotFoundException("99")));

                webTestClient.get()
                                .uri("/product/99/similar")
                                .exchange()
                                .expectStatus().isNotFound();
        }
}
