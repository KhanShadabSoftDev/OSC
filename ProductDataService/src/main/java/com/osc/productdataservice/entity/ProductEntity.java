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
@Table(name = "products")
public class ProductEntity {

    @Id
    @Column(name = "productid")
    private String productId;

    @Column(name = "categoryid")
    private String categoryId;

    @Column(name = "productname")
    private String productName;

    @Column(name = "productdescription",columnDefinition = "TEXT")
    private String productDescription;

    @Column(name = "productprice")
    private Double productPrice;

    @Column(name = "imagepath")
    private String imagePath;

}

