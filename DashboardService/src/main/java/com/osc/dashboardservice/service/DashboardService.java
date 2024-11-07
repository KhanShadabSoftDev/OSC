package com.osc.dashboardservice.service;

import com.osc.dashboardservice.dto.DashboardDTO;
import com.osc.dashboardservice.dto.FilterDTO;
import com.osc.dashboardservice.dto.ProductViewedDTO;
import com.osc.dashboardservice.model.FinalProduct;
import com.osc.dashboardservice.response.ApiResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final CartManagementService cartManagementService;
    private final DashboardDataRetrievalService dashboardDataRetrievalService;
    private final ProductDetailsService productDetailsService;
    private final FilterProductService filterProductService;

    public DashboardService(CartManagementService cartManagementService, DashboardDataRetrievalService dashboardDataRetrievalService, ProductDetailsService productDetailsService, FilterProductService filterProductService) {
        this.cartManagementService = cartManagementService;
        this.dashboardDataRetrievalService = dashboardDataRetrievalService;
        this.productDetailsService = productDetailsService;
        this.filterProductService = filterProductService;
    }


    public ApiResponse viewDashboard(DashboardDTO dashboardDTO) {

        return dashboardDataRetrievalService.fetchDashboardData(dashboardDTO);
    }

    public ApiResponse viewProductDetails(ProductViewedDTO productViewedDTO) {

        return productDetailsService.viewProductDetails(productViewedDTO);
    }

    public ApiResponse filterProducts(FilterDTO filterDTO) {

        return filterProductService.filterProducts(filterDTO);
    }

    public List<FinalProduct> getCartDetails(String userId) {

        return cartManagementService.getCartDetails(userId);
    }

    public void increaseProductQuantityInCart(String userId, String productId) {

        cartManagementService.increaseProductQuantityInCart(userId, productId);
    }

    public void decreaseProductQuantityInCart(String userId, String productId) {
        cartManagementService.decreaseProductQuantityInCart(userId, productId);
    }

    public void removeProductFromCart(String userId, String productId) {

        cartManagementService.removeProductFromCart(userId, productId);
    }


}


