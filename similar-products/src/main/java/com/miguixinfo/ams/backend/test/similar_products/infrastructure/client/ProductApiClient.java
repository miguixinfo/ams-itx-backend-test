package com.miguixinfo.ams.backend.test.similar_products.infrastructure.client;

import com.miguixinfo.ams.backend.test.similar_products.domain.exception.ProductNotFoundException;
import com.miguixinfo.ams.backend.test.similar_products.domain.model.ProductDetail;
import com.miguixinfo.ams.backend.test.similar_products.infrastructure.config.ProductApiProperties;
import com.miguixinfo.ams.backend.test.similar_products.port.out.ProductPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@Component
public class ProductApiClient implements ProductPort {

    private final WebClient webClient;
    private final Duration timeout;

    public ProductApiClient(WebClient productWebClient, ProductApiProperties properties) {
        this.webClient = productWebClient;
        this.timeout = Duration.ofSeconds(properties.timeoutSeconds());
    }

    @Override
    public Flux<String> getSimilarIds(String productId) {
        return webClient.get()
                .uri("/product/{id}/similarids", productId)
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        response -> Mono.error(new ProductNotFoundException(productId))
                )
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .timeout(timeout)
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<ProductDetail> getProductDetail(String productId) {
        return webClient.get()
                .uri("/product/{id}", productId)
                .retrieve()
                .bodyToMono(ProductDetailResponse.class)
                .map(dto -> new ProductDetail(dto.id(), dto.name(), dto.price(), dto.availability()))
                .timeout(timeout)
                .onErrorResume(e -> Mono.empty());
    }

    private record ProductDetailResponse(
            String id,
            String name,
            BigDecimal price,
            boolean availability
    ) {}
}
