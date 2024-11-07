package com.osc.recentlyviewedservice.grpcservice;

import com.google.protobuf.Empty;
import com.grpc.recentHistory.*;
import com.osc.avro.RecentViewHistory;
import com.osc.recentlyviewedservice.service.RecentlyViewedProductService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@GrpcService
public class RecentlyViewedDataService extends RecentlyViewedServiceGrpc.RecentlyViewedServiceImplBase {

    private static final Logger log = LogManager.getLogger(RecentlyViewedDataService.class);

    private final RecentlyViewedProductService recentlyViewedProductService;

    public RecentlyViewedDataService(RecentlyViewedProductService recentlyViewedProductService) {
        this.recentlyViewedProductService = recentlyViewedProductService;
    }

    @Override
    public void fetchRecentlyViewedHistory(RecentlyViewedRequest request, StreamObserver<RecentlyViewedResponse> responseObserver) {
        // Step 1: Extract userId from the request
        String userId = request.getUserId();
        log.info("Received fetchRecentlyViewedHistory request for userId: {}", userId);

        try {

            RecentlyViewedResponse recentlyViewedResponse
                    = recentlyViewedProductService.fetchRecentlyViewedProducts(userId);


            responseObserver.onNext(recentlyViewedResponse);
            responseObserver.onCompleted();
            log.info("Successfully sent recently viewed products for userId: {}", userId);
        } catch (Exception e) {
            log.error("Error fetching recently viewed products for userId: {}. Error: {}", userId, e.getMessage());
            responseObserver.onError(e); // Send error to client
        }
    }

    @Override
    public void updateRecentlyViewedProducts(UpdateRecentViewedRequest request, StreamObserver<Empty> responseObserver) {
        // Extracting the new product ID and user ID from the request
        String newProductId = request.getProductId();
        String userId = request.getUserId();
        log.info("Received updateRecentlyViewedProducts request for userId: {}, productId: {}", userId, newProductId);

        try {
            // Update the recently viewed products by producing to Product-View-Topic
            recentlyViewedProductService.produceRecentlyViewedProduct(userId, newProductId);
            log.debug("Updated recently viewed products for userId: {} with productId: {}", userId, newProductId);

            // Assuming the update was successful and returning an empty response
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
            log.info("Successfully updated recently viewed products for userId: {}", userId);
        } catch (Exception e) {
            log.error("Error updating recently viewed products for userId: {} and productId: {}. Error: {}", userId, newProductId, e.getMessage());
            responseObserver.onError(e); // Send error to client
        }
    }

    @Override
    public void syncRecentlyViewedFromKafka(SyncRecentlyViewedRequest request, StreamObserver<Empty> responseObserver) {
        String userId = request.getUserId();
        try {

            recentlyViewedProductService.syncRecentlyViewedToDB(userId);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
            log.info("Successfully synced Recently viewed products for userId: {}", userId);
        } catch (Exception e) {
            log.error("Error syncing recently viewed products for userId: {}  Error: {}", userId, e.getMessage());
            responseObserver.onError(e); // Send error to client
        }
    }
}
