package com.osc.dashboardservice.mapper;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class DashboardSimilarProductDTO {

    @JsonProperty("TYPE")
    private String type;// "Similar Products"

    @JsonProperty("Similar Products")
    private List<ProductDTO> similarProducts;

}
