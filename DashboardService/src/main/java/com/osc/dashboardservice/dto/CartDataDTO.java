package com.osc.dashboardservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.osc.dashboardservice.mapper.CartProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDataDTO {
    @JsonProperty("cartProducts")
    private List<CartProductDTO> cartProducts;

    @JsonProperty("productsCartCount")
    private int productsCartCount;

    @JsonProperty("totalPrice")
    private double price;
}
