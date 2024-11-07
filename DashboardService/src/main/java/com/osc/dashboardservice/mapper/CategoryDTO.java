package com.osc.dashboardservice.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryDTO {

    private String categoryId;
    private String categoryName;
}
