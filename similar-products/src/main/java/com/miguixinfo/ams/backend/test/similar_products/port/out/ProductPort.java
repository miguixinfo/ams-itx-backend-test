package com.miguixinfo.ams.backend.test.similar_products.port.out;

import com.miguixinfo.ams.backend.test.similar_products.domain.model.ProductDetail;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductPort {

    /**
     * Fetches the IDs of similar products for the given productId.
     * Signals {@link com.miguixinfo.ams.backend.test.similar_products.domain.exception.ProductNotFoundException}
     * if the upstream returns 404.
     */
    Flux<String> getSimilarIds(String productId);

    /**
     * Fetches the detail for a single productId.
     * Returns {@link Mono#empty()} if the product is not found or the call fails.
     * Never propagates errors to the caller.
     */
    Mono<ProductDetail> getProductDetail(String productId);
}
