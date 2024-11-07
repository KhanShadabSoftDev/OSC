package com.osc.dashboardservice.mapper;

import com.grpc.product.Category;

import java.util.List;
import java.util.stream.Collectors;

public class MapCategoryToDTO {
    public List<CategoryDTO> map(List<Category> categories) {
        return categories.stream()
                .map(category -> new CategoryDTO(
                        category.getCategoryId(),
                        category.getCategoryName()
                ))
                .collect(Collectors.toList());
    }
}

