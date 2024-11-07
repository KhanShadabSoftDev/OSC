package com.osc.recentlyviewedservice.repository;


import com.osc.recentlyviewedservice.entity.RecentlyViewedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecentlyViewedRepository extends JpaRepository<RecentlyViewedEntity, String> {


}
