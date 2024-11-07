package com.osc.dashboardservice.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class DashboardFeaturedProductsDTO {

    @JsonProperty("TYPE")
    private String type;  // "Featured Products"

    @JsonProperty("Featured Products")
    private List<ProductDTO> featuredProducts;
}
