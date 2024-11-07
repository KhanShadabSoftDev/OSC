package com.osc.productdataservice.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "product_view_count")
public class ProductViewCountEntity {

    @Id
    @Column(name = "productid")
    private String productId;

    @Column(name = "categoryid")
    private Character categoryId;

    @Column(name = "viewcount")
    private long viewcount;


}
