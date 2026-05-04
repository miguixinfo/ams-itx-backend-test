package com.miguixinfo.ams.backend.test.similar_products.interfaces.rest;

import com.miguixinfo.ams.backend.test.similar_products.domain.exception.ProductNotFoundException;
import com.miguixinfo.ams.backend.test.similar_products.interfaces.rest.dto.ProductDetailResponse;
import com.miguixinfo.ams.backend.test.similar_products.interfaces.rest.mapper.ProductDetailMapper;
import com.miguixinfo.ams.backend.test.similar_products.port.in.GetSimilarProductsUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/product")
public class SimilarProductsController {

    private final GetSimilarProductsUseCase getSimilarProductsUseCase;
    private final ProductDetailMapper mapper;

    public SimilarProductsController(
            GetSimilarProductsUseCase getSimilarProductsUseCase,
            ProductDetailMapper mapper
    ) {
        this.getSimilarProductsUseCase = getSimilarProductsUseCase;
        this.mapper = mapper;
    }

    @GetMapping("/{productId}/similar")
    public Flux<ProductDetailResponse> getSimilarProducts(@PathVariable String productId) {
        return getSimilarProductsUseCase.getSimilarProducts(productId)
                .map(mapper::toResponse)
                .onErrorMap(
                        ProductNotFoundException.class,
                        ex -> new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage())
                );
    }
}
