package com.miguixinfo.ams.backend.test.similar_products.port.in;

import com.miguixinfo.ams.backend.test.similar_products.domain.model.ProductDetail;
import reactor.core.publisher.Flux;

public interface GetSimilarProductsUseCase {

    /**
     * Returns the product details of all similar products for the given productId,
     * preserving the order returned by the similar-ids upstream API.
     * Products that fail to resolve (404, 500, timeout) are silently omitted.
     *
     * @throws ProductNotFoundException
     *                                  if the given productId has no similar-ids
     *                                  record upstream
     */
    Flux<ProductDetail> getSimilarProducts(String productId);
}
