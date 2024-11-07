package com.osc.dashboardservice.mapper;

import com.grpc.product.FilterProductResponse;
import com.grpc.product.Product;
import com.osc.dashboardservice.response.ApiResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class FilterProductResponseDTO {

    public   Map<String, Object> map(FilterProductResponse filterProductResponse) {
        // Extract the product list from the response
        List<Product> productsList = filterProductResponse.getProductsList();
        List<MinimalProductDTO> filterProductsList = new ArrayList<>();

        // Map each Product to MinimalProductDTO and add to the list
        for (Product product : productsList) {
            MinimalProductDTO minimalProductDTO = new MinimalProductDTO();
            minimalProductDTO.setProductId(product.getProductId());
            minimalProductDTO.setProdName(product.getName());
            minimalProductDTO.setProdMarketPrice(String.valueOf(product.getPrice()));
            filterProductsList.add(minimalProductDTO);
        }

        // Prepare the dataObject map with all the filtered products in a list
        Map<String, Object> dataObject = Map.of(
                "products", filterProductsList // Store the filtered products under the "products" key
        );

        // Return ApiResponse with the products list
        return dataObject;
    }
}