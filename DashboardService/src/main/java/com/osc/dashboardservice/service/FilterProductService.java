package com.osc.dashboardservice.service;

import com.grpc.product.FilterProductRequest;
import com.grpc.product.FilterProductResponse;
import com.grpc.product.Product;
import com.grpc.product.ProductDashboardServiceGrpc;
import com.osc.dashboardservice.dto.FilterDTO;
import com.osc.dashboardservice.mapper.FilterProductResponseDTO;
import com.osc.dashboardservice.response.ApiResponse;
import io.grpc.StatusRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FilterProductService {

    private static final Logger log = LogManager.getLogger(FilterProductService.class);

    private final ProductDashboardServiceGrpc.ProductDashboardServiceBlockingStub productDashboardServiceBlockingStub;
    private final FilterProductResponseDTO filterProductResponseDTO;

    public FilterProductService(ProductDashboardServiceGrpc.ProductDashboardServiceBlockingStub productDashboardServiceBlockingStub,
                                FilterProductResponseDTO filterProductResponseDTO) {
        this.productDashboardServiceBlockingStub = productDashboardServiceBlockingStub;
        this.filterProductResponseDTO = filterProductResponseDTO;
    }

    public ApiResponse filterProducts(FilterDTO filterDTO) {
        try {
            FilterProductRequest filterProductRequest = FilterProductRequest.newBuilder()
                    .setUserId(filterDTO.getUserId())
                    .setCategoryId(filterDTO.getCategoryId())
                    .setFilterCriteria(filterDTO.getFilterCriteria())
                    .build();

            FilterProductResponse filterProductResponse = productDashboardServiceBlockingStub.filterProduct(filterProductRequest);
            List<Product> productsList = filterProductResponse.getProductsList();

            log.info("Products filtered with criteria: {} for category ID: {}. Resulting products: {}", filterDTO.getFilterCriteria(), filterDTO.getCategoryId(), productsList);

            Map<String, Object> dataObject = filterProductResponseDTO.map(filterProductResponse);
            return new ApiResponse(200, dataObject);
        } catch (StatusRuntimeException e) {
            log.error("Failed to filter products: {}", e.getStatus().getDescription());
            return new ApiResponse(500, null);
        } catch (Exception e) {
            log.error("Error while filtering products: {}", e.getMessage(), e);
            return new ApiResponse(500, null);
        }
    }
}
