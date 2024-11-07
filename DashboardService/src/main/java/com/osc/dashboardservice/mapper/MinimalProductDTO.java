package com.osc.dashboardservice.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MinimalProductDTO {

    @JsonProperty("productId")
    private String productId;
    @JsonProperty("prodName")
    private String prodName;
    @JsonProperty("prodMarketPrice")
    private String prodMarketPrice;
}