package com.miguixinfo.ams.backend.test.similar_products.domain.model;

import java.math.BigDecimal;

public record ProductDetail(
        String id,
        String name,
        BigDecimal price,
        boolean availability
) {}
