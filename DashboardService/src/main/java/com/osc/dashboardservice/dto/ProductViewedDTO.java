package com.osc.dashboardservice.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductViewedDTO {

    @NotBlank
    @JsonProperty("userId")
    private String userId;
    @NotBlank
    @JsonProperty("catId")
    private String categoryId;
    @NotBlank
    @JsonProperty("prodId")
    private String productId;


}
