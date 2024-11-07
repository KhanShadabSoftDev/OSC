package com.cartdataservice.grpcservice;

import com.cartdataservice.service.CartService;
import com.google.protobuf.Empty;
import com.grpc.cart.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@GrpcService
public class CartDataService extends CartServiceGrpc.CartServiceImplBase {

    private static final Logger log = LogManager.getLogger(CartDataService.class);

    private final CartService cartService;

    public CartDataService(CartService cartService) {
        this.cartService = cartService;
    }

    @Override
    public void fetchCartDetails(CartDetailsRequest request, StreamObserver<CartDetailsResponse> responseObserver) {
        String userId = request.getUserId();
        log.info("Fetching cart details for User ID: {}", userId);
        try {
            // Fetch the cart details from the CartService
            CartDetailsResponse response = cartService.getCartDetails(userId);

            if (response == null) {
                log.warn("No cart details found for User ID: {}", userId);
                responseObserver.onNext(CartDetailsResponse.newBuilder().build());
            } else {
                log.info("Cart details fetched successfully for User ID: {}", userId);
                responseObserver.onNext(response);
            }
        } catch (Exception e) {
            log.error("Error fetching cart details for User ID: {}", userId, e);
            responseObserver.onError(e);
            return;
        }
        responseObserver.onCompleted();
    }


    @Override
    public void updateProductQuantity(ProductQuantityRequest request, StreamObserver<Empty> responseObserver) {
        String userId = request.getUserId();
        String productId = request.getProductId();
        boolean isIncrease = request.getIsIncrease();

        try {
            cartService.updateProductQuantity(userId, productId, isIncrease);

            // Respond with an empty success response
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();

            log.info("Product quantity update successful for User ID: {}", userId);
        } catch (Exception e) {
            log.error("Error updating product quantity for User ID: {} and Product ID: {}", userId, productId, e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void removeProduct(ProductRemoveRequest request, StreamObserver<Empty> responseObserver) {

        String userId = request.getUserId();
        String productId = request.getProductId();

        log.info("Received request to remove product ID: {} from cart of user ID: {}", productId, userId);

        try {
            // Delegate the product removal logic to CartService
            cartService.removeProductFromCart(userId, productId);

            // Respond with an empty success response
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();

            log.info("Successfully removed product ID: {} for user ID: {}", productId, userId);
        } catch (Exception e) {
            log.error("Error removing product ID: {} for user ID: {}", productId, userId, e);
            responseObserver.onError(e);
        }
    }


    @Override
    public void syncCartFromKafka(SyncCartRequest request, StreamObserver<Empty> responseObserver) {
        String userId = request.getUserId();
        System.out.println("Inside the Cart DataService for syncing cartlist");
        try {
            // Fetch the cart details from the CartService
            cartService.syncCartWithDatabase(userId);

            // Respond with an empty success response
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            // Handle exceptions and respond with an error if something goes wrong
            responseObserver.onError(e);
        }
    }
}

