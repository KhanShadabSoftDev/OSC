package com.osc.productdataservice.service.DB.start;

import com.osc.avro.CategoryDetails;
import com.osc.avro.ProductDetails;
import com.osc.avro.ProductViewCount;
import com.osc.productdataservice.kafka.producer.RecordProducer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class KafkaStartupDataProducer implements ApplicationRunner {

    private final ProductCategoryService productCategoryService;
    private final ProductViewCountService productViewCountService;
    private final RecordProducer recordProducer;

    public KafkaStartupDataProducer(ProductCategoryService productCategoryService, ProductViewCountService productViewCountService, RecordProducer recordProducer) {
        this.productCategoryService = productCategoryService;
        this.productViewCountService = productViewCountService;
        this.recordProducer = recordProducer;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        List<CategoryDetails> categories = productCategoryService.fetchAllCategories();
        List<ProductDetails> products = productCategoryService.fetchAllProducts();

        List<ProductViewCount> productViewCounts = productViewCountService.fetchAllProductViewCounts();


//        recordProducer.sendCategoryDetailsToKafka(categories);
//        recordProducer.sendProductDetailsToKafka(products);
//        recordProducer.sendAllProductViewCountToKafka(productViewCounts);

    }
}


