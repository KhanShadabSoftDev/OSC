package com.osc.dashboardservice.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class DashboardCategoryListDTO {

    @JsonProperty("TYPE")
    private String type;  // "Categories"

    @JsonProperty("Categories")
    private List<CategoryDTO> categories;
}
