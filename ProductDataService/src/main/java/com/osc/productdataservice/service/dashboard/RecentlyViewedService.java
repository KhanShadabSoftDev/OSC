package com.osc.productdataservice.service.dashboard;
import com.grpc.product.Product;
import com.osc.avro.ProductDetails;
import com.osc.productdataservice.mapper.GrpcConversionUtility;
import com.osc.productdataservice.kafka.consumer.KafkaRecordService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecentlyViewedService {

    private static final Logger log = LogManager.getLogger(FeaturedProductService.class);

    private final KafkaRecordService kafkaRecordService;

    public RecentlyViewedService(KafkaRecordService kafkaRecordService) {
        this.kafkaRecordService = kafkaRecordService;
    }

    // Method to fetch product details by product IDs
    public Map<String, ProductDetails> getProductDetails(List<String> productIdsList) {
        Map<String, ProductDetails> productDetailsByIds = kafkaRecordService.getProductDetailsByIds(productIdsList);
        log.info("Fetched recently viewed product details: {}", productDetailsByIds);
        return productDetailsByIds;
    }

    // Method to convert ProductDetails map values to gRPC Product objects
    public List<Product> convertToGrpcProducts(Map<String, ProductDetails> productDetailsMap) {
        return productDetailsMap.values()
                .stream()
                .map(GrpcConversionUtility::convertAvroToGrpcProduct)
                .collect(Collectors.toList());
    }
}

