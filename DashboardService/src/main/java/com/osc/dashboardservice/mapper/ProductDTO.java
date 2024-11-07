package com.osc.dashboardservice.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ProductDTO {

    @JsonProperty("productId")
    private String productId;

    @JsonProperty("prodName")
    private String prodName;

    @JsonProperty("categoryId")
    private String categoryId;

    @JsonProperty("prodMarketPrice")
    private double prodMarketPrice;

    @JsonProperty("productDescription")
    private String productDescription;
}
