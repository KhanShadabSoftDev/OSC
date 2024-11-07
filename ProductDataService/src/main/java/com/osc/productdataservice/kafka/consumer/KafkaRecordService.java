package com.osc.productdataservice.kafka.consumer;
import com.osc.avro.CategoryDetails;
import com.osc.avro.ProductDetails;
import com.osc.avro.ProductViewCount;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class KafkaRecordService {

    private final ReadOnlyKeyValueStore<String, ProductDetails> productStore;
    private final ReadOnlyKeyValueStore<String, CategoryDetails> categoryDetailsStore;
    private final ReadOnlyKeyValueStore<String, ProductViewCount> productViewCountStore;

    // Injecting the state stores directly
    public KafkaRecordService(
            @Qualifier("productStore") ReadOnlyKeyValueStore<String, ProductDetails> productStore,
            @Qualifier("categoryDetailsStore") ReadOnlyKeyValueStore<String, CategoryDetails> categoryStore,
            @Qualifier("productViewCountStore") ReadOnlyKeyValueStore<String, ProductViewCount> productViewCountStore) {
        this.productStore = productStore;
        this.categoryDetailsStore = categoryStore;
        this.productViewCountStore = productViewCountStore;
    }

    public ProductDetails getProductByProductId(String productId) {
        return productStore.get(productId);
    }

    public ProductViewCount getProductViewCount(String productId) {
        return productViewCountStore.get(productId);
    }

    public CategoryDetails getCategoryByCategoryId(String categoryId) {
        return categoryDetailsStore.get(categoryId);
    }

    // Method to get products by category ID from the ReadOnlyKeyValueStore
    public List<ProductDetails> getProductsByCategoryId(String categoryId) {
        List<ProductDetails> productsInSameCategory = new ArrayList<>();
        KeyValueIterator<String, ProductDetails> allProducts = productStore.all();

        while (allProducts.hasNext()) {
            KeyValue<String, ProductDetails> productEntry = allProducts.next();
            ProductDetails productDetails = productEntry.value;

            if (productDetails.getCategoryId().toString().equals(categoryId)) {
                productsInSameCategory.add(productDetails);
            }
        }

        allProducts.close();
        return productsInSameCategory;
    }

    // Method to get product details by product IDs from the ReadOnlyKeyValueStore
    public Map<String, ProductDetails> getProductDetailsByIds(List<String> productIds) {
        Map<String, ProductDetails> productDetailsMap = new LinkedHashMap<>();

        for (String productId : productIds) {
            ProductDetails productDetails = productStore.get(productId);
            if (productDetails != null) {
                productDetailsMap.put(productId, productDetails);
            }
        }
        return productDetailsMap;
    }

    // Method to get all ProductViewCount objects from the store
    public Map<String, ProductViewCount> getProductViewCountObjects() {
        Map<String, ProductViewCount> productViewCountMap = new HashMap<>();

        productViewCountStore.all().forEachRemaining(keyValue -> {
            String productId = keyValue.key;
            ProductViewCount productViewCount = keyValue.value;
            productViewCountMap.put(productId, productViewCount);
        });

        return productViewCountMap;
    }

    // Method to get product view counts from ProductViewCount objects
    public Map<String, Long> getProductViewCounts() {
        Map<String, Long> productViewCounts = new HashMap<>();
        Map<String, ProductViewCount> productViewCountMap = getProductViewCountObjects();

        productViewCountMap.forEach((productId, productViewCount) -> {
            productViewCounts.put(productId, productViewCount.getCount());
        });

        return productViewCounts;
    }

    // Method to get top N products based on view count
    public List<ProductDetails> getTopProductsByViewCount(int topN) {
        Map<String, Long> productViewCounts = getProductViewCounts();

        List<Map.Entry<String, Long>> topProducts = productViewCounts.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(topN)
                .collect(Collectors.toList());

        List<ProductDetails> featuredProducts = new ArrayList<>();
        for (Map.Entry<String, Long> entry : topProducts) {
            String productId = entry.getKey();
            ProductDetails productDetails = getProductByProductId(productId);

            if (productDetails != null) {
                featuredProducts.add(productDetails);
            }
        }

        return featuredProducts;
    }

    // New method for aggregating product views by category
    public Map<String, Long> aggregateProductViewsByCategory() {
        Map<String, Long> categoryViewCounts = new HashMap<>();

        productViewCountStore.all().forEachRemaining(keyValue -> {
            String categoryId = keyValue.value.getCategoryId().toString();
            Long viewCount = keyValue.value.getCount();

            // Sum the view counts by categoryId
            categoryViewCounts.merge(categoryId, viewCount, Long::sum);
        });

        return categoryViewCounts;
    }

    // New method to get sorted CategoryDetails by view count
    public List<CategoryDetails> getSortedCategoriesByViewCount() {
        Map<String, Long> categoryViewCounts = aggregateProductViewsByCategory();

        // Sort the categories by view count in descending order
        List<Map.Entry<String, Long>> sortedCategories = categoryViewCounts.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toList());

        List<CategoryDetails> categoryDetailsList = new ArrayList<>();
        for (Map.Entry<String, Long> entry : sortedCategories) {
            String categoryId = entry.getKey();
            CategoryDetails categoryDetails = categoryDetailsStore.get(categoryId);

            // Ensure categoryDetails is not null before adding
            if (categoryDetails != null) {
                categoryDetailsList.add(categoryDetails);
            }
        }

        return categoryDetailsList;
    }


}

