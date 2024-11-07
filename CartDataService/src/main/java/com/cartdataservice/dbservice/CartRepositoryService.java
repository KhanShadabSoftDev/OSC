package com.cartdataservice.dbservice;

import com.cartdataservice.entity.CartEntity;
import com.cartdataservice.entity.ProductRecord;
import com.cartdataservice.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartRepositoryService {


    private final CartRepository cartRepository;

    public CartRepositoryService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public void saveCart(String userId, List<ProductRecord> productRecord) {
        CartEntity cartEntity = new CartEntity(userId, productRecord);
        cartRepository.save(cartEntity);
    }


}
