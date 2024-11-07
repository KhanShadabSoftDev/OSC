package com.osc.productdataservice.kafka.producer;

import com.osc.avro.CategoryDetails;
import com.osc.avro.ProductDetails;
import com.osc.avro.ProductViewCount;
import com.osc.productdataservice.config.ApplicationConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final Logger log = LogManager.getLogger(RecordProducer.class);

    public RecordProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAllProductViewCountToKafka(List<ProductViewCount> viewCount) {
      for (ProductViewCount productViewCount:viewCount){

          kafkaTemplate.send(ApplicationConfig.PRODUCT_VIEW_COUNT_TOPIC, productViewCount.getProductId().toString(), productViewCount);
      }
    }
    public void sendProductViewCountToKafka(String productId, ProductViewCount viewCount) {
          kafkaTemplate.send(ApplicationConfig.PRODUCT_VIEW_COUNT_TOPIC, productId, viewCount);

    }

    public void sendProductDetailsToKafka(List<ProductDetails> productDetails) {
       for(ProductDetails product:productDetails){
           kafkaTemplate.send(ApplicationConfig.PRODUCT_DETAILS_TOPIC, product.getProductId().toString(), product);
       }
    }

    public void sendCategoryDetailsToKafka( List<CategoryDetails> categoryDetails) {
        for (CategoryDetails category : categoryDetails) {
            kafkaTemplate.send(ApplicationConfig.CATEGORIES_DETAILS_TOPIC, category.getCategoryId().toString(),category);
        }
    }




}
