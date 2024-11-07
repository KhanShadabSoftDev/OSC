package com.osc.productdataservice.repository;

import com.osc.productdataservice.entity.ProductViewCountEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductViewCountRepository extends JpaRepository<ProductViewCountEntity,String> {


    @Modifying
    @Transactional
    @Query("UPDATE ProductViewCountEntity p SET p.viewcount = :viewcount WHERE p.productId = :productId")
    int updateViewCountByProductId(@Param("productId") String productId, @Param("viewcount") long viewcount);
}
