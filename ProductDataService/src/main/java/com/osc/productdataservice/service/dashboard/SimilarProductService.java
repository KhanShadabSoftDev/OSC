package com.osc.productdataservice.service.dashboard;
import com.grpc.product.Product;
import com.osc.avro.ProductDetails;
import com.osc.avro.ProductViewCount;
import com.osc.productdataservice.mapper.GrpcConversionUtility;
import com.osc.productdataservice.kafka.consumer.KafkaRecordService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SimilarProductService {

    private static final Logger log = LogManager.getLogger(SimilarProductService.class);
    private final KafkaRecordService kafkaRecordService;

    public SimilarProductService(KafkaRecordService kafkaRecordService) {
        this.kafkaRecordService = kafkaRecordService;
    }

    public List<Product> findSimilarProducts(List<String> categoryIds, List<String> recentlyViewedProductIds) {
        log.info("Category IDs from recently viewed products: {}", categoryIds);

        Set<String> recentlyViewedProductSet = new HashSet<>(recentlyViewedProductIds);
        List<ProductDetails> similarProducts = new ArrayList<>();

        // Loop through recently viewed products, and for each, fetch products from the respective category
        for (int i = 0; i < recentlyViewedProductIds.size(); i++) {
            String categoryId = categoryIds.get(i);
            List<ProductDetails> productsInSameCategory = kafkaRecordService.getProductsByCategoryId(categoryId);

            // Filter and sort in one step to avoid filtering multiple times
            productsInSameCategory.stream()
                    .filter(product -> !recentlyViewedProductSet.contains(product.getProductId().toString()))  // Filter out recently viewed
                    .sorted((p1, p2) -> {
                        long p2Count = Optional.ofNullable(kafkaRecordService.getProductViewCount(p2.getProductId().toString()))
                                .map(ProductViewCount::getCount).orElse(0L);
                        long p1Count = Optional.ofNullable(kafkaRecordService.getProductViewCount(p1.getProductId().toString()))
                                .map(ProductViewCount::getCount).orElse(0L);
                        return Long.compare(p2Count, p1Count); // Sort by view count (descending)
                    })
                    .findFirst()  // Get the top product from this category
                    .ifPresent(similarProducts::add);

            // If we've already filled 6 similar products, break early
            if (similarProducts.size() >= 6) {
                break;
            }
        }
        // Fill remaining spots (if less than 6) with products from the first category
        if (similarProducts.size() < 6 && !categoryIds.isEmpty()) {
            String firstCategoryId = categoryIds.get(0);  // First category of recently viewed products
            List<ProductDetails> firstCategoryProducts = kafkaRecordService.getProductsByCategoryId(firstCategoryId);

            // Filter out products already in recently viewed or similarProducts, sort by view count, and fill remaining slots
            firstCategoryProducts.stream()
                    .filter(product -> !recentlyViewedProductSet.contains(product.getProductId().toString()) &&
                            !similarProducts.contains(product))  // Filter out already selected products
                    .sorted((p1, p2) -> {
                        long p2Count = Optional.ofNullable(kafkaRecordService.getProductViewCount(p2.getProductId().toString()))
                                .map(ProductViewCount::getCount).orElse(0L);
                        long p1Count = Optional.ofNullable(kafkaRecordService.getProductViewCount(p1.getProductId().toString()))
                                .map(ProductViewCount::getCount).orElse(0L);
                        return Long.compare(p2Count, p1Count); // Sort by view count (descending)
                    })
                    .limit(6 - similarProducts.size())  // Add only enough products to fill the list to 6
                    .forEach(similarProducts::add);
        }

        log.info("Similar products found: {}", similarProducts);

        return convertToGrpcProducts(similarProducts);
    }

    public  List<Product> findSimilarProductsByCategory(String categoryId, String currentProductId) {
        log.info("Fetching similar products for category: {}, excluding current product ID: {}", categoryId, currentProductId);

        List<ProductDetails> similarProducts = new ArrayList<>();

        // Fetch products from the same category
        List<ProductDetails> productsInSameCategory = kafkaRecordService.getProductsByCategoryId(categoryId);

        // Filter out the current product and sort by view count
        productsInSameCategory.stream()
                .filter(product -> !product.getProductId().toString().equals(currentProductId))  // Exclude the current product
                .sorted((p1, p2) -> {
                    long p2Count = Optional.ofNullable(kafkaRecordService.getProductViewCount(p2.getProductId().toString()))
                            .map(ProductViewCount::getCount).orElse(0L);
                    long p1Count = Optional.ofNullable(kafkaRecordService.getProductViewCount(p1.getProductId().toString()))
                            .map(ProductViewCount::getCount).orElse(0L);
                    return Long.compare(p2Count, p1Count);  // Sort by view count (descending)
                })
                .limit(6)  // Limit to top 6 similar products
                .forEach(similarProducts::add);
        log.info("Similar products found: {}", similarProducts);
        return convertToGrpcProducts(similarProducts);
    }

    private List<Product> convertToGrpcProducts(List<ProductDetails> similarProducts){
        return similarProducts.stream().map(GrpcConversionUtility::convertAvroToGrpcProduct)
                         .collect(Collectors.toList());
    }

}
