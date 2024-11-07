package com.osc.productdataservice.service.dashboard;
import com.grpc.product.Category;
import com.osc.avro.CategoryDetails;
import com.osc.productdataservice.mapper.GrpcConversionUtility;
import com.osc.productdataservice.kafka.consumer.KafkaRecordService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private static final Logger log = LogManager.getLogger(CategoryService.class);
    private final KafkaRecordService kafkaRecordService;

    public CategoryService(KafkaRecordService kafkaRecordService) {
        this.kafkaRecordService = kafkaRecordService;
    }

    // This method now delegates to KafkaRecordService to get sorted categories by view count
    public List<Category> aggregateProductViewsByCategory() {
        List<CategoryDetails> sortedCategoriesByViewCount = kafkaRecordService.getSortedCategoriesByViewCount();

        log.info("Fetched category details: {}", sortedCategoriesByViewCount);
        return convertToGrpcCategories(sortedCategoriesByViewCount);
    }

    // Convert Avro CategoryDetails to gRPC Category
    private List<Category> convertToGrpcCategories(List<CategoryDetails> categoryDetailsList) {

        //            Shows All the Categories(12)

        return categoryDetailsList.stream()
                .map(GrpcConversionUtility::convertAvroToGrpcCategory)
                .collect(Collectors.toList());
    }

}
