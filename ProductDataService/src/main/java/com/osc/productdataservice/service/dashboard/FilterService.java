package com.osc.productdataservice.service.dashboard;
import com.grpc.product.Product;
import com.osc.avro.ProductDetails;
import com.osc.avro.ProductViewCount;
import com.osc.productdataservice.mapper.GrpcConversionUtility;
import com.osc.productdataservice.kafka.consumer.KafkaRecordService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilterService {

    private static final Logger log = LogManager.getLogger(FilterService.class);
    private final KafkaRecordService kafkaRecordService;

    public FilterService(KafkaRecordService kafkaRecordService) {
        this.kafkaRecordService = kafkaRecordService;
    }

    public  List<ProductDetails> fetchProductsByCategory(String categoryId) {
        List<ProductDetails> products = kafkaRecordService.getProductsByCategoryId(categoryId);
        if (products == null || products.isEmpty()) {
            throw new RuntimeException("No products found for the given category ID: " + categoryId);
        }
        log.info("Fetched {} products for categoryId: {}", products.size(), categoryId);
        return products;
    }

    public List<Product> filterProductsList(List<ProductDetails> products, String filterCriteria) {
        // Apply sorting based on the filter criteria

        switch (filterCriteria) {
            case "LH": // Low to High price
//                products.sort(Comparator.comparingDouble(ProductDetails::getProductPrice));

                sortByPriceLowToHigh(products);
                break;

            case "HL": // High to Low price
//                products.sort(Comparator.comparingDouble(ProductDetails::getProductPrice).reversed());
                sortByPriceHighToLow(products);
                break;

            case "P": // Popularity (based on product view count)
                sortByPopularity(products);
                break;


            case "NF": // Newest First (based on date added)
                sortByNewestFirst(products);
                break;

            default:
                throw new IllegalArgumentException("Invalid filter criteria: " + filterCriteria);

        }
        return convertToGrpcProducts(products);
    }

    private List<Product> convertToGrpcProducts(List<ProductDetails> products) {
        return products.stream()
                .map(GrpcConversionUtility::convertAvroToGrpcProduct)
                .collect(Collectors.toList());
    }

    private void sortByPopularity(List<ProductDetails> products) {

        products.sort((p1, p2) -> {
            ProductViewCount viewCount1Obj = kafkaRecordService.getProductViewCount(p1.getProductId().toString());
            ProductViewCount viewCount2Obj = kafkaRecordService.getProductViewCount(p2.getProductId().toString());

            // Get the count from the ProductViewCount object, handle null cases by defaulting to 0
            long viewCount1 = (viewCount1Obj != null) ? viewCount1Obj.getCount() : 0L;
            long viewCount2 = (viewCount2Obj != null) ? viewCount2Obj.getCount() : 0L;

            return Long.compare(viewCount2, viewCount1); // Descending order (most popular first)
        });
    }

    private void sortByNewestFirst(List<ProductDetails> products) {
        Collections.sort(products, (p1, p2) -> {
            LocalDateTime dateAdded1 = LocalDateTime.parse(p1.getDateAdded().toString(), DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime dateAdded2 = LocalDateTime.parse(p2.getDateAdded().toString(), DateTimeFormatter.ISO_DATE_TIME);
            return dateAdded2.compareTo(dateAdded1);
        });
    }

    private void sortByPriceLowToHigh(List<ProductDetails> products) {
        products.sort(Comparator.comparingDouble(ProductDetails::getProductPrice));
    }

    private void sortByPriceHighToLow(List<ProductDetails> products) {
        products.sort(Comparator.comparingDouble(ProductDetails::getProductPrice).reversed());
    }
}