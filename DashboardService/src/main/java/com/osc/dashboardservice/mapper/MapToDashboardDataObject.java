package com.osc.dashboardservice.mapper;

import com.grpc.product.DashboardDetails;
import com.osc.dashboardservice.response.DataObject;
import java.util.List;

public class MapToDashboardDataObject {
    private final MapProductsToDTO mapProductsToDTO;
    private final MapCategoryToDTO mapCategoriesToDTO;

    public MapToDashboardDataObject(MapProductsToDTO mapProductsToDTO, MapCategoryToDTO mapCategoriesToDTO) {
        this.mapProductsToDTO = mapProductsToDTO;
        this.mapCategoriesToDTO = mapCategoriesToDTO;
    }

    public DataObject map(DashboardDetails dashboardDetails) {
        DataObject dataObject;

        DashboardFeaturedProductsDTO dashboardFeaturedProductsDTO = new DashboardFeaturedProductsDTO(
                "Featured Products",
                mapProductsToDTO.map(dashboardDetails.getFeaturedProductsList())
        );


        DashboardCategoryListDTO dashboardCategoryListDTO = new DashboardCategoryListDTO(
                "Categories",
                mapCategoriesToDTO.map(dashboardDetails.getCategoriesList())
        );

        if (!dashboardDetails.getRecentlyViewedProductsList().isEmpty()) {
            DashboardRecentlyViewProductDTO dashboardRecentlyViewProductDTO = new DashboardRecentlyViewProductDTO(
                    "Recently Viewed Products",
                    mapProductsToDTO.map(dashboardDetails.getRecentlyViewedProductsList())
            );

            DashboardSimilarProductDTO dashboardSimilarProductDTO = new DashboardSimilarProductDTO(
                    "Similar Products",
                    mapProductsToDTO.map(dashboardDetails.getSimilarProductsList())
            );

            // Return a data object that includes all fields (recently viewed, similar products, category, cart)
            dataObject = new DataObject(
                    List.of(dashboardRecentlyViewProductDTO, dashboardSimilarProductDTO, dashboardCategoryListDTO)
            );

            return dataObject;
        }
        // Return a data object with featured products and categories if recently viewed list is empty
        dataObject = new DataObject(
                List.of(dashboardFeaturedProductsDTO, dashboardCategoryListDTO)
        );
        return dataObject;
    }
}
