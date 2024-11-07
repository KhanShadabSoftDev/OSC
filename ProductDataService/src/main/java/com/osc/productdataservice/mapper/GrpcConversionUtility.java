package com.osc.productdataservice.mapper;

import com.grpc.product.Category;
import com.grpc.product.Product;
import com.osc.avro.CategoryDetails;
import com.osc.avro.ProductDetails;
import org.springframework.stereotype.Component;


@Component
public class GrpcConversionUtility {

    public  static Category convertAvroToGrpcCategory(CategoryDetails avroCategory) {
        return Category.newBuilder()
                .setCategoryId(avroCategory.getCategoryId().toString())
                .setCategoryName(avroCategory.getCategoryName().toString())
                .setImage(avroCategory.getImagePath().toString())
                .build();
    }

    public static Product convertAvroToGrpcProduct(ProductDetails avroProduct) {
        return Product.newBuilder().setProductId(avroProduct.getProductId().toString())
                .setCategoryId(avroProduct.getCategoryId().toString())
                .setName(avroProduct.getProductName().toString())
                .setPrice(avroProduct.getProductPrice())
                .setImage(avroProduct.getProductImage().toString())
                .setProductDetails(avroProduct.getProductDescription().toString())
                .build();
    }

}
