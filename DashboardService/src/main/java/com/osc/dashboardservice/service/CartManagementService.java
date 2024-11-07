package com.osc.dashboardservice.service;

import com.grpc.cart.*;
import com.grpc.product.Product;
import com.grpc.product.ProductDashboardServiceGrpc;
import com.grpc.product.ProductListRequest;
import com.grpc.product.ProductListResponse;
import com.osc.dashboardservice.model.FinalProduct;
import io.grpc.StatusRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartManagementService {

    private static final Logger log = LogManager.getLogger(CartManagementService.class);
    private final CartServiceGrpc.CartServiceBlockingStub cartServiceBlockingStub;
    private final ProductDashboardServiceGrpc.ProductDashboardServiceBlockingStub productDashboardServiceBlockingStub;

    public CartManagementService(CartServiceGrpc.CartServiceBlockingStub cartServiceBlockingStub, ProductDashboardServiceGrpc.ProductDashboardServiceBlockingStub productDashboardServiceBlockingStub) {
        this.cartServiceBlockingStub = cartServiceBlockingStub;
        this.productDashboardServiceBlockingStub = productDashboardServiceBlockingStub;
    }

    public List<FinalProduct> getCartDetails(String userId) {
        try {
            CartDetailsRequest request = CartDetailsRequest.newBuilder()
                    .setUserId(userId)
                    .build();

            CartDetailsResponse cartDetailsResponse = cartServiceBlockingStub.fetchCartDetails(request);

            Map<String, Integer> productCountMap = cartDetailsResponse.getCartProductsList().stream()
                .collect(Collectors.toMap(ProductDetail::getProductId, ProductDetail::getQuantity));

        List<String> productIds = new ArrayList<>(productCountMap.keySet());
        List<Product> productDetails = getProductDetails(productIds);

           return productDetails.stream()
                .map(product -> new FinalProduct(product, productCountMap.getOrDefault(product.getProductId(), 0)))
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error while fetching cart details: {}", e.getMessage(), e);
            return null;
        }
    }

    private List<Product> getProductDetails(List<String> productIds) {
        ProductListRequest productListRequest = ProductListRequest.newBuilder()
                .addAllProductIds(productIds)
                .build();

        try {
            ProductListResponse productListResponse = productDashboardServiceBlockingStub.fetchProductList(productListRequest);
            log.info("Fetched product details for product IDs: {}", productIds);
            return productListResponse.getProductsList();
        } catch (StatusRuntimeException e) {
            log.error("Failed to fetch product details: {}", e.getStatus().getDescription());
        }
        return Collections.emptyList(); // Return an empty list in case of failure
    }


    public void increaseProductQuantityInCart(String userId, String productId) {
        ProductQuantityRequest productQuantityRequest = ProductQuantityRequest.newBuilder()
                .setUserId(userId)
                .setIsIncrease(true)
                .setProductId(productId)
                .build();

        cartServiceBlockingStub.updateProductQuantity(productQuantityRequest);
        log.info("Increased product quantity by 1 in cart for user ID {}: Product ID {}", userId, productId);
    }

    public void decreaseProductQuantityInCart(String userId, String productId) {
        ProductQuantityRequest productQuantityRequest = ProductQuantityRequest.newBuilder()
                .setUserId(userId)
                .setIsIncrease(false)
                .setProductId(productId)
                .build();

        cartServiceBlockingStub.updateProductQuantity(productQuantityRequest);
        log.info("Decreased product quantity in cart by 1 for user ID {}: Product ID {}", userId, productId);
    }

    public void removeProductFromCart(String userId, String productId) {
        ProductRemoveRequest productRemoveRequest = ProductRemoveRequest.newBuilder()
                .setUserId(userId)
                .setProductId(productId)
                .build();

        cartServiceBlockingStub.removeProduct(productRemoveRequest);
        log.info("Removed product from cart for user ID {}: Product ID {}", userId, productId);
    }

}
