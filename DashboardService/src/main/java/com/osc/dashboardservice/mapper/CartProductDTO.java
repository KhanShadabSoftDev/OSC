package com.osc.dashboardservice.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.grpc.product.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartProductDTO {


    @JsonProperty("userId")
    private String userId;

    @JsonProperty("productName")
    private String productName;  // Changed to match the desired JSON response

    @JsonProperty("productId")
    private String productId;

    @JsonProperty("categoryId")
    private String categoryId;

    @JsonProperty("productPrice")
    private double productPrice;  // Changed from 'prodMarketPrice' to 'productPrice'

    @JsonProperty("quantity")
    private int quantity;  // Changed from 'cartQty' to 'quantity'
}
