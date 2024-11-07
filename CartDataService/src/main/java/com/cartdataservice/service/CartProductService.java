package com.cartdataservice.service;

import com.cartdataservice.kafka.consumer.CartStateStore;
import com.cartdataservice.kafka.producer.RecordProducer;
import com.grpc.cart.CartDetailsResponse;
import com.grpc.cart.ProductDetail;
import com.osc.avro.CartList;
import com.osc.avro.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class CartProductService {

    private static final Logger log = LogManager.getLogger(CartProductService.class);
    private final RecordProducer recordProducer;

    private final CartStateStore cartStateStore;

    public CartProductService(RecordProducer recordProducer, CartStateStore cartStateStore) {
        this.recordProducer = recordProducer;
        this.cartStateStore = cartStateStore;
    }


    public CartDetailsResponse getCartDetails(String userId) {
        CartList userCartList = cartStateStore.getCartList(userId);
        if (userCartList == null || userCartList.getProducts().isEmpty()) {
            log.info("No products found in the cart for User ID: {}", userId);
            return null;
        }

        CartDetailsResponse.Builder responseBuilder = CartDetailsResponse.newBuilder();
        List<ProductDetail> cartProductDetailsList = new ArrayList<>();

        for (Product cartProduct : userCartList.getProducts()) {
            log.debug("Processing product ID: {}", cartProduct.getProductId());
            ProductDetail productDetail = ProductDetail.newBuilder()
                    .setProductId(cartProduct.getProductId().toString())
                    .setQuantity(cartProduct.getProductQuantity())
                    .build();
            cartProductDetailsList.add(productDetail);
        }

        return responseBuilder.addAllCartProducts(cartProductDetailsList).build();
    }

    public void modifyProductQuantity(String userId, String productId, boolean isIncrease) {

        CartList userCartList = cartStateStore.getCartList(userId);

        if (userCartList == null) {
            log.warn("No cart found for User ID: {}", userId);
            userCartList = CartList.newBuilder()
                    .setProducts(new LinkedList<>())
                    .build();
        }

        List<Product> userCartProducts = Optional.ofNullable(userCartList.getProducts()).orElse(new LinkedList<>());
        boolean isProductFound = false;

        for (Product cartProduct : userCartProducts) {
            if (cartProduct.getProductId().toString().equals(productId)) {
                int currentQuantity = cartProduct.getProductQuantity();
                cartProduct.setProductQuantity(isIncrease ? currentQuantity + 1 : Math.max(0, currentQuantity - 1));
                isProductFound = true;
                break;
            }
        }

        if (!isProductFound) {
            log.debug("Product ID: {} not found in cart. Adding with quantity 1.", productId);
            Product newProductInCart = Product.newBuilder()
                    .setProductId(productId)
                    .setProductQuantity(1)
                    .build();
            userCartProducts.add(newProductInCart);
        }

        recordProducer.sendCartDetailsToKafka(userId, userCartProducts); // Sync cart to Kafka
    }

    public void clearProductFromCart(String userId, String productId) {

        CartList userCartList = cartStateStore.getCartList(userId);

        List<Product> userCartProducts = Optional.ofNullable(userCartList)
                .map(CartList::getProducts)
                .map(ArrayList::new)
                .orElse(new ArrayList<>());

        boolean isProductRemoved = userCartProducts.removeIf(cartProduct -> cartProduct.getProductId().toString().equals(productId));
        if (isProductRemoved) {
            log.debug("Product ID: {} removed from cart for user ID: {}", productId, userId);
        } else {
            log.warn("Product ID: {} not found in cart for user ID: {}", productId, userId);
        }

        recordProducer.sendCartDetailsToKafka(userId, userCartProducts); // Sync updated cart to Kafka
    }



    public List<Product> getUserCartProducts(String userId) {
        CartList userCartList = cartStateStore.getCartList(userId);
        return Optional.ofNullable(userCartList).map(CartList::getProducts).orElse(new ArrayList<>());
    }
}

