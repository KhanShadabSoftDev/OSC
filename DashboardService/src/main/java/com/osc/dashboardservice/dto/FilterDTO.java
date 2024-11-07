package com.osc.dashboardservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FilterDTO {

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("catId")
    private String categoryId;

    @JsonProperty("filter")
    private String filterCriteria;


}
