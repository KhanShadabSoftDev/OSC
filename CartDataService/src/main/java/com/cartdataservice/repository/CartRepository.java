package com.cartdataservice.repository;

import com.cartdataservice.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<CartEntity,String> {
}
