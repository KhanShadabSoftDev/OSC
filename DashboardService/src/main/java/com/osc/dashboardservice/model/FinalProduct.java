package com.osc.dashboardservice.model;

import com.grpc.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

// This is the class which has product info with its QTY in a Cart
@Getter
@AllArgsConstructor
@ToString
public class FinalProduct {
    private Product product;
    private int productQuantity;
}
