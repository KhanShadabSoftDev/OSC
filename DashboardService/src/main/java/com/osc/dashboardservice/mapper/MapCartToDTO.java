package com.osc.dashboardservice.mapper;


import com.osc.dashboardservice.dto.CartDataDTO;
import com.osc.dashboardservice.model.FinalProduct;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MapCartToDTO {
    public CartDataDTO mapCartDetails(List<FinalProduct> cartDetails, String userId) {

        List<CartProductDTO> cartProductDTOs = cartDetails.stream()
                .map(finalProduct -> {

                    // Create a new CartProductDTO with mapped fields
                    return new CartProductDTO(
                            userId,                                         // User ID
                            finalProduct.getProduct().getName(),            // Product Name
                            finalProduct.getProduct().getProductId(),       // Product ID
                            finalProduct.getProduct().getCategoryId(),      // Category ID
                            finalProduct.getProduct().getPrice(),           // Product Price
                            finalProduct.getProductQuantity()               // Quantity
                    );
                })
                .collect(Collectors.toList());

        // Calculate the total price based on product price and quantity
        double totalPrice = cartDetails.stream()
                .mapToDouble(finalProduct ->
                        finalProduct.getProduct().getPrice() * finalProduct.getProductQuantity()
                ).sum();

        // Create the CartDataDTO object
        CartDataDTO cartDataDTO = new CartDataDTO(
                cartProductDTOs,
                cartDetails.size(),   // Number of products in the cart
                totalPrice            // Total price of all products
        );

        // Return the CartDTO
        return cartDataDTO;
    }

}

