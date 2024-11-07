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
@Table(name = "categories")
public class CategoryEntity {

    @Id
    @Column(name = "categoryid")
    private Character categoryId;

    @Column(name="categoryname")
    private String categoryName;

    @Column(name="imagepath")
    private String imagePath;


}
