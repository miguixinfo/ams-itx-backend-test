package com.miguixinfo.ams.backend.test.similar_products.application.usecase;

import com.miguixinfo.ams.backend.test.similar_products.domain.model.ProductDetail;
import com.miguixinfo.ams.backend.test.similar_products.port.in.GetSimilarProductsUseCase;
import com.miguixinfo.ams.backend.test.similar_products.port.out.ProductPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class GetSimilarProductsService implements GetSimilarProductsUseCase {

    private final ProductPort productPort;

    public GetSimilarProductsService(ProductPort productPort) {
        this.productPort = productPort;
    }

    @Override
    public Flux<ProductDetail> getSimilarProducts(String productId) {
        return productPort.getSimilarIds(productId)
                .flatMap(productPort::getProductDetail);
    }
}
