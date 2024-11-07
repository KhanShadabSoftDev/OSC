package com.osc.productdataservice.service.DB.start;
import com.osc.avro.CategoryDetails;
import com.osc.avro.ProductDetails;
import com.osc.productdataservice.entity.CategoryEntity;
import com.osc.productdataservice.entity.ProductEntity;
import com.osc.productdataservice.repository.CategoryRepository;
import com.osc.productdataservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductCategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public ProductCategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public List<CategoryDetails> fetchAllCategories() {
        return convertCategoryEntityToAvroCategory(categoryRepository.findAll());
    }

    public List<ProductDetails> fetchAllProducts() {
        return convertProductEntityToAvroProduct(productRepository.findAll());
    }

    private List<CategoryDetails> convertCategoryEntityToAvroCategory(List<CategoryEntity> categoryEntityList) {
        return categoryEntityList.stream()
                .map(entity -> CategoryDetails.newBuilder()
                        .setCategoryId(entity.getCategoryId().toString())
                        .setCategoryName(entity.getCategoryName())
                        .setImagePath(entity.getImagePath())
                        .build())
                .collect(Collectors.toList());
    }

    private List<ProductDetails> convertProductEntityToAvroProduct(List<ProductEntity> productEntityList) {
        return productEntityList.stream()
                .map(entity -> ProductDetails.newBuilder()
                        .setProductId(entity.getProductId())
                        .setProductDescription(entity.getProductDescription())
                        .setCategoryId(entity.getCategoryId())
                        .setProductName(entity.getProductName())
                        .setProductPrice(entity.getProductPrice())
                        .setProductImage(entity.getImagePath())
                        .setDateAdded(LocalDateTime.now().toString())
                        .build())
                .collect(Collectors.toList());
    }
}

