package com.osc.dashboardservice.service;

import com.grpc.product.ProductDashboardServiceGrpc;
import com.grpc.product.ProductDataRequest;
import com.grpc.product.ProductDataResponse;
import com.grpc.recentHistory.RecentlyViewedServiceGrpc;
import com.grpc.recentHistory.UpdateRecentViewedRequest;
import com.osc.dashboardservice.dto.ProductViewedDTO;
import com.osc.dashboardservice.mapper.ProductDetailDTOMapper;
import com.osc.dashboardservice.response.ApiResponse;
import io.grpc.StatusRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProductDetailsService {

    private static final Logger log = LogManager.getLogger(ProductDetailsService.class);

    private final ProductDashboardServiceGrpc.ProductDashboardServiceBlockingStub productDashboardServiceBlockingStub;
    private final RecentlyViewedServiceGrpc.RecentlyViewedServiceBlockingStub recentlyViewedServiceBlockingStub;
    private final ProductDetailDTOMapper productDetailDTOMapper;

    public ProductDetailsService(ProductDashboardServiceGrpc.ProductDashboardServiceBlockingStub productDashboardServiceBlockingStub,
                                 RecentlyViewedServiceGrpc.RecentlyViewedServiceBlockingStub recentlyViewedServiceBlockingStub,
                                 ProductDetailDTOMapper productDetailDTOMapper) {
        this.productDashboardServiceBlockingStub = productDashboardServiceBlockingStub;
        this.recentlyViewedServiceBlockingStub = recentlyViewedServiceBlockingStub;
        this.productDetailDTOMapper = productDetailDTOMapper;
    }

    public ApiResponse viewProductDetails(ProductViewedDTO productViewedDTO) {
        try {
            String productId = productViewedDTO.getProductId();
            String userId = productViewedDTO.getUserId();

            ProductDataRequest productDataRequest = ProductDataRequest.newBuilder()
                    .setProductId(productId)
                    .build();
            ProductDataResponse productDataResponse = productDashboardServiceBlockingStub.fetchProductDetails(productDataRequest);

            log.info("Product details fetched for ID: {}", productViewedDTO.getProductId());

            // Update recently viewed
            UpdateRecentViewedRequest updateRecentViewedRequest = UpdateRecentViewedRequest.newBuilder()
                    .setProductId(productId)
                    .setUserId(userId)
                    .build();
            recentlyViewedServiceBlockingStub.updateRecentlyViewedProducts(updateRecentViewedRequest);
            log.info("Product ID {} added to recently viewed for user ID {}", productViewedDTO.getProductId(), productViewedDTO.getUserId());

            Map<String, Object> dataObject = productDetailDTOMapper.map(productDataResponse);
            return new ApiResponse(200, dataObject);
        } catch (StatusRuntimeException e) {
            log.error("Failed to view product or update recently viewed history: {}", e.getStatus().getDescription());
            return new ApiResponse(500, null);
        } catch (Exception e) {
            log.error("Error while viewing product details: {}", e.getMessage(), e);
            return new ApiResponse(500, null);
        }
    }
}
