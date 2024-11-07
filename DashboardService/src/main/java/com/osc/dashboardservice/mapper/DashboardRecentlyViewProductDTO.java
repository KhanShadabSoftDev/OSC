package com.osc.dashboardservice.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class DashboardRecentlyViewProductDTO {

    @JsonProperty("TYPE")
    private String type;  // "Recently Viewed Products"

    @JsonProperty("Recently Viewed Products")
    private List<ProductDTO> recentlyViewedProducts;
}
