package com.cartdataservice.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "Cart_Table")
@Entity
public class CartEntity {

    @Id
    private String userId;

    @Column(columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<ProductRecord> productIds;


}
