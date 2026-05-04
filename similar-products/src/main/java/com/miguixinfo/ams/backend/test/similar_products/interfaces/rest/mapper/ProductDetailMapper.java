package com.miguixinfo.ams.backend.test.similar_products.interfaces.rest.mapper;

import com.miguixinfo.ams.backend.test.similar_products.domain.model.ProductDetail;
import com.miguixinfo.ams.backend.test.similar_products.interfaces.rest.dto.ProductDetailResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductDetailMapper {

    public ProductDetailResponse toResponse(ProductDetail domain) {
        return new ProductDetailResponse(
                domain.id(),
                domain.name(),
                domain.price(),
                domain.availability()
        );
    }
}
