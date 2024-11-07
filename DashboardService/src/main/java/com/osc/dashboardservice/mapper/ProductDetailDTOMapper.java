package com.osc.dashboardservice.mapper;

import com.grpc.product.Product;
import com.grpc.product.ProductDataResponse;
import com.osc.dashboardservice.response.ApiResponse;
import com.osc.dashboardservice.response.DataObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ProductDetailDTOMapper {

    public Map<String, Object> map(ProductDataResponse productDataResponse) {

        Product productDTO = productDataResponse.getProduct();
        DashboardSimilarProductDTO similarProductDTO = new DashboardSimilarProductDTO();
        similarProductDTO.setType("Similar Products");
        List<MinimalProductDTO> similarProducts = new ArrayList<>();
        for (Product similarProduct : productDataResponse.getProductsList()) {
            MinimalProductDTO minimalProductDTO = new MinimalProductDTO();
            minimalProductDTO.setProductId(similarProduct.getProductId());
            minimalProductDTO.setProdName(similarProduct.getName());
            minimalProductDTO.setProdMarketPrice(String.valueOf(similarProduct.getPrice()));
            similarProducts.add(minimalProductDTO);
        }
        Map<String, Object> dataObject = Map.of("prodId", productDTO.getProductId(),
                "catId", productDTO.getCategoryId(),
                "prodName", productDTO.getName(),
                "prodDesc", productDTO.getProductDetails(),
                "prodPrice", productDTO.getPrice(),
                "similarProducts", similarProducts );

        return dataObject;
    }
}
