package com.cartdataservice.service;

import com.cartdataservice.dbservice.CartRepositoryService;
import com.cartdataservice.entity.ProductRecord;
import com.osc.avro.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartSyncService {

    private static final Logger log = LogManager.getLogger(CartSyncService.class);

    private final CartRepositoryService cartRepositoryService;

    public CartSyncService(CartRepositoryService cartRepositoryService) {
        this.cartRepositoryService = cartRepositoryService;
    }


    public void persistCartToDatabase(String userId, List<Product> cartProducts) {

        List<ProductRecord> productRecords = cartProducts.stream()
                .map(product -> new ProductRecord(product.getProductId().toString(), product.getProductQuantity()))
                .collect(Collectors.toList());

        cartRepositoryService.saveCart(userId, productRecords);
        log.info("Cart synced to database for user ID: {}", userId);
    }
}
