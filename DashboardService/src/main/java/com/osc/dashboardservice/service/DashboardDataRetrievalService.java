package com.osc.dashboardservice.service;

import com.grpc.product.DashboardDetails;
import com.grpc.product.DashboardRequest;
import com.grpc.product.DashboardResponse;
import com.grpc.product.ProductDashboardServiceGrpc;
import com.grpc.recentHistory.RecentlyViewedRequest;
import com.grpc.recentHistory.RecentlyViewedResponse;
import com.grpc.recentHistory.RecentlyViewedServiceGrpc;
import com.osc.dashboardservice.dto.DashboardDTO;
import com.osc.dashboardservice.mapper.MapCategoryToDTO;
import com.osc.dashboardservice.mapper.MapProductsToDTO;
import com.osc.dashboardservice.mapper.MapToDashboardDataObject;
import com.osc.dashboardservice.response.ApiResponse;
import com.osc.dashboardservice.response.DataObject;
import io.grpc.StatusRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardDataRetrievalService {

    private static final Logger log = LogManager.getLogger(DashboardService.class);
    private final ProductDashboardServiceGrpc.ProductDashboardServiceBlockingStub productDashboardServiceBlockingStub;
    private final RecentlyViewedServiceGrpc.RecentlyViewedServiceBlockingStub recentlyViewedServiceBlockingStub;

    public DashboardDataRetrievalService(ProductDashboardServiceGrpc.ProductDashboardServiceBlockingStub productDashboardServiceBlockingStub, RecentlyViewedServiceGrpc.RecentlyViewedServiceBlockingStub recentlyViewedServiceBlockingStub) {
        this.productDashboardServiceBlockingStub = productDashboardServiceBlockingStub;
        this.recentlyViewedServiceBlockingStub = recentlyViewedServiceBlockingStub;
    }

    public ApiResponse fetchDashboardData(DashboardDTO dashboardDTO) {
        try {
            RecentlyViewedRequest recentlyViewedRequest = RecentlyViewedRequest.newBuilder()
                    .setUserId(dashboardDTO.getUserId())
                    .build();

            RecentlyViewedResponse recentlyViewedResponse = recentlyViewedServiceBlockingStub.fetchRecentlyViewedHistory(recentlyViewedRequest);
            List<String> productIds = recentlyViewedResponse.getProductIdList();

            log.info("Recently Viewed Product List: {}", productIds);

            DashboardRequest.Builder dashboardRequestBuilder = DashboardRequest.newBuilder();
            DashboardResponse dashboardResponse;

            if (productIds.isEmpty()) {
                log.info("First-time user login. Fetching categories and featured products.");
                dashboardResponse = productDashboardServiceBlockingStub.fetchProductDashboard(dashboardRequestBuilder.build());
            } else {
                log.info("Returning dashboard for returning user with recently viewed products.");
                dashboardRequestBuilder.addAllProductId(productIds);
                dashboardResponse = productDashboardServiceBlockingStub.fetchProductDashboard(dashboardRequestBuilder.build());
            }

//             Map the data to the dashboard DTOs
            MapToDashboardDataObject mapToDashboardDataObject = new MapToDashboardDataObject(
                    new MapProductsToDTO(),
                    new MapCategoryToDTO()
            );

            // Build the DataObject for the API response
            DashboardDetails dashboardDetails = DashboardDetails.newBuilder()
                    .addAllCategories(dashboardResponse.getDashboardDetails().getCategoriesList())
                    .addAllFeaturedProducts(dashboardResponse.getDashboardDetails().getFeaturedProductsList())
                    .addAllRecentlyViewedProducts(dashboardResponse.getDashboardDetails().getRecentlyViewedProductsList())
                    .addAllSimilarProducts(dashboardResponse.getDashboardDetails().getSimilarProductsList())
                    .build();
            // Fetch the basic dashboard data

            DataObject dataObject = mapToDashboardDataObject.map(dashboardDetails);

            return new ApiResponse(200, dataObject);
        } catch (StatusRuntimeException e) {
            log.error("Failed to view dashboard {}", e.getStatus().getDescription());
            return new ApiResponse(500, null);
        } catch (Exception e) {
            log.error("Error while viewing dashboard: {}", e.getMessage(), e);
            return new ApiResponse(500, null);
        }
    }




}
