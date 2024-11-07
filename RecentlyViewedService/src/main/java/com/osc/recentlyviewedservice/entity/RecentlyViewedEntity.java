package com.osc.recentlyviewedservice.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "Recently_Viewed")
public class RecentlyViewedEntity {

    @Id
    private String userId;

    private List<String> productIds;


}
