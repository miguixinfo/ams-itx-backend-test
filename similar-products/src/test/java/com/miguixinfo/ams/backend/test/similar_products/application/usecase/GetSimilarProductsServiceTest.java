package com.miguixinfo.ams.backend.test.similar_products.application.usecase;

import com.miguixinfo.ams.backend.test.similar_products.domain.exception.ProductNotFoundException;
import com.miguixinfo.ams.backend.test.similar_products.domain.model.ProductDetail;
import com.miguixinfo.ams.backend.test.similar_products.port.out.ProductPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSimilarProductsServiceTest {

    @Mock
    private ProductPort productPort;

    private GetSimilarProductsService service;

    @BeforeEach
    void setUp() {
        service = new GetSimilarProductsService(productPort);
    }

    @Test
    void givenSimilarIds_whenBothResolve_returnsBothDetails() {
        ProductDetail product2 = new ProductDetail("2", "Product 2", BigDecimal.valueOf(10.0), true);
        ProductDetail product3 = new ProductDetail("3", "Product 3", BigDecimal.valueOf(20.0), false);

        when(productPort.getSimilarIds("1")).thenReturn(Flux.just("2", "3"));
        when(productPort.getProductDetail("2")).thenReturn(Mono.just(product2));
        when(productPort.getProductDetail("3")).thenReturn(Mono.just(product3));

        StepVerifier.create(service.getSimilarProducts("1"))
                .expectNextMatches(p -> p.id().equals("2") || p.id().equals("3"))
                .expectNextMatches(p -> p.id().equals("2") || p.id().equals("3"))
                .verifyComplete();
    }

    @Test
    void givenSimilarIds_whenOneReturnsEmpty_returnsOnlyResolvedProduct() {
        ProductDetail product2 = new ProductDetail("2", "Product 2", BigDecimal.valueOf(10.0), true);

        when(productPort.getSimilarIds("1")).thenReturn(Flux.just("2", "3"));
        when(productPort.getProductDetail("2")).thenReturn(Mono.just(product2));
        when(productPort.getProductDetail("3")).thenReturn(Mono.empty());

        StepVerifier.create(service.getSimilarProducts("1"))
                .expectNext(product2)
                .verifyComplete();
    }

    @Test
    void givenUnknownProductId_whenSimilarIdsThrows_propagatesProductNotFoundException() {
        when(productPort.getSimilarIds("99")).thenReturn(
                Flux.error(new ProductNotFoundException("99"))
        );

        StepVerifier.create(service.getSimilarProducts("99"))
                .expectErrorMatches(e ->
                        e instanceof ProductNotFoundException &&
                        e.getMessage().contains("99")
                )
                .verify();
    }
}
