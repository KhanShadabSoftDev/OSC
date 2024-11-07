package com.osc.dashboardservice.mapper;

import com.grpc.product.Product;

import java.util.List;
import java.util.stream.Collectors;

public class MapProductsToDTO {

    public List<ProductDTO> map(List<Product> products) {

        return products.stream()
                .map(product -> new ProductDTO(
                        product.getProductId(),
                        product.getName(),
                        product.getCategoryId(),
                        product.getPrice(),
                        product.getProductDetails()
                ))
                .collect(Collectors.toList());
    }
}
