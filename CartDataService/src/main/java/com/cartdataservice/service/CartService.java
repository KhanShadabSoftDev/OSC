package com.cartdataservice.service;

import com.cartdataservice.kafka.producer.RecordProducer;
import com.grpc.cart.CartDetailsResponse;
import com.osc.avro.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private static final Logger log = LogManager.getLogger(CartService.class);

    private final CartProductService cartProductService;
    private final CartSyncService cartSyncService;

    public CartService(CartProductService cartProductService, CartSyncService cartSyncService) {
        this.cartProductService = cartProductService;
        this.cartSyncService = cartSyncService;
    }

    public CartDetailsResponse getCartDetails(String userId) {
        // Log the request to fetch cart details
        log.info("Fetching cart data for User ID: {}", userId);

        // Fetching From cartProductService
        return cartProductService.getCartDetails(userId);
    }

    public void updateProductQuantity(String userId, String productId, boolean isIncrease) {

        log.info("Updating quantity for User ID: {}, Product ID: {}, Increase: {}", userId, productId, isIncrease);

        cartProductService.modifyProductQuantity(userId, productId, isIncrease);

    }

    public void removeProductFromCart(String userId, String productId) {
        log.info("Removing product from cart for User ID: {}, Product ID: {}", userId, productId);

        cartProductService.clearProductFromCart(userId, productId); // Remove product from cart
    }

    public void syncCartWithDatabase(String userId) {
        log.info("Synchronizing cart with database for User ID: {}", userId);
//      Fetch cart products
        List<Product> userCartProducts = cartProductService.getUserCartProducts(userId);
        // If cart has products
        if (userCartProducts != null && !userCartProducts.isEmpty()) {
            cartSyncService.persistCartToDatabase(userId, userCartProducts); // Sync with database
        } else {
            log.warn("No products found in cart for User ID: {}", userId); // Warn if cart empty
        }
    }
}







