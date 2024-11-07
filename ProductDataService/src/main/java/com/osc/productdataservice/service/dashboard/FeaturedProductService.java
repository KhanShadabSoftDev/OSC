package com.osc.productdataservice.service.dashboard;
import com.grpc.product.Product;
import com.osc.avro.ProductDetails;
import com.osc.productdataservice.mapper.GrpcConversionUtility;
import com.osc.productdataservice.kafka.consumer.KafkaRecordService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeaturedProductService {
    private static final Logger log = LogManager.getLogger(FeaturedProductService.class);

    private final KafkaRecordService kafkaRecordService;

    public FeaturedProductService(KafkaRecordService kafkaRecordService) {
        this.kafkaRecordService = kafkaRecordService;
    }

    // Get 12 featured products based on their view count from KafkaRecordService
    public List<Product> getFeaturedProducts() {
        List<ProductDetails> topProductsByViewCount = kafkaRecordService.getTopProductsByViewCount(12);
        log.info("Fetched featured products: {}", topProductsByViewCount);
        return convertToGrpcProducts(topProductsByViewCount);
    }

    // Convert Avro ProductDetails to gRPC Product
    private List<Product> convertToGrpcProducts( List<ProductDetails> featuredProductDetails) {

        return featuredProductDetails.stream().map(GrpcConversionUtility::convertAvroToGrpcProduct)
                .collect(Collectors.toList());
    }
}
