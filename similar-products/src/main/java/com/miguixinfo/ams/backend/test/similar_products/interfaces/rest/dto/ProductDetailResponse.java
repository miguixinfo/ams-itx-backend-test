package com.miguixinfo.ams.backend.test.similar_products.interfaces.rest.dto;

import java.math.BigDecimal;

public record ProductDetailResponse(
        String id,
        String name,
        BigDecimal price,
        boolean availability
) {}
