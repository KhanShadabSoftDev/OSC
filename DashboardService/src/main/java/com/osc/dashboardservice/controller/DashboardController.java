package com.osc.dashboardservice.controller;


import com.osc.dashboardservice.dto.*;
import com.osc.dashboardservice.mapper.MapCartToDTO;
import com.osc.dashboardservice.model.FinalProduct;
import com.osc.dashboardservice.response.ApiResponse;
import com.osc.dashboardservice.service.DashboardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @PostMapping("/user/dashBoard")
    public ApiResponse dashboard(@RequestBody DashboardDTO dashboardDTO) {

        return dashboardService.viewDashboard(dashboardDTO);
    }

    @PostMapping("/product/details")
    public ApiResponse viewProduct(@RequestBody ProductViewedDTO productViewedDTO) {

        return dashboardService.viewProductDetails(productViewedDTO);
    }


    //    Filtering  Products API's
    @PostMapping("/filter/product")
    public ApiResponse filterProducts(@RequestBody FilterDTO filterDTO) {

        return dashboardService.filterProducts(filterDTO);
    }

    //   Cart Section API's (4)

    @PostMapping("/user/cart/view")
    public ApiResponse viewCart(@RequestBody ViewCartDTO viewCartDTO) {

        List<FinalProduct> cartDetails = dashboardService.getCartDetails(viewCartDTO.getUserId());

        CartDataDTO cartDataDTO = new MapCartToDTO().mapCartDetails(cartDetails, viewCartDTO.getUserId());

        return new ApiResponse(200, cartDataDTO);
    }

    @PostMapping("user/cart/increase")
    public ApiResponse increaseProductInCart(@RequestBody CartUpdateDTO cartUpdateDTO) {
        try {
            dashboardService.increaseProductQuantityInCart(cartUpdateDTO.getUserId(),
                    cartUpdateDTO.getProductId());

            return new ApiResponse(200, null);
        } catch (Exception e) {
            return new ApiResponse(500, null);
        }
    }

    @PostMapping("user/cart/decrease")
    public ApiResponse decreaseProductInCart(@RequestBody CartUpdateDTO cartUpdateDTO) {
        try {
            dashboardService.decreaseProductQuantityInCart(cartUpdateDTO.getUserId(),
                    cartUpdateDTO.getProductId());

            return new ApiResponse(200, null);
        } catch (Exception e) {
            return new ApiResponse(500, null);
        }
    }

    //     This Api can be used for removing product from cart
    @PostMapping("/user/cart/remove")
    public ApiResponse removeProductInCart(@RequestBody RemoveProductFromCartDTO removeProductFromCartDTO) {
        try {
            dashboardService.removeProductFromCart(removeProductFromCartDTO.getUserId(),
                    removeProductFromCartDTO.getProductId());

            return new ApiResponse(200, null);
        } catch (Exception e) {
            return new ApiResponse(500, null);
        }
    }

}
