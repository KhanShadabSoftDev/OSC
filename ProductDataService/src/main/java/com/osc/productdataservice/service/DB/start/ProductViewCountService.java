package com.osc.productdataservice.service.DB.start;
import com.osc.avro.ProductViewCount;
import com.osc.productdataservice.entity.ProductViewCountEntity;
import com.osc.productdataservice.repository.ProductViewCountRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductViewCountService {

    private final ProductViewCountRepository productViewCountRepository;

    public ProductViewCountService(ProductViewCountRepository productViewCountRepository) {
        this.productViewCountRepository = productViewCountRepository;
    }

    public List<ProductViewCount> fetchAllProductViewCounts() {
        return convertProductViewCountEntityToAvroProductViewCount(productViewCountRepository.findAll());
    }

    private List<ProductViewCount> convertProductViewCountEntityToAvroProductViewCount(List<ProductViewCountEntity> productViewCountEntityList) {
        return productViewCountEntityList.stream()
                .map(entity ->
                        ProductViewCount.newBuilder()
                                .setProductId(entity.getProductId())
                                .setCount(entity.getViewcount())
                                .setCategoryId(entity.getCategoryId().toString())
                                .build())
                .collect(Collectors.toList());
    }
}
