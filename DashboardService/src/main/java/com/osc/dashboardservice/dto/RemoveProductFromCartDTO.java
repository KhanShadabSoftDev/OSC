package com.osc.dashboardservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoveProductFromCartDTO {

    private String userId;
    @JsonProperty("prodId")
    private String productId;
}
