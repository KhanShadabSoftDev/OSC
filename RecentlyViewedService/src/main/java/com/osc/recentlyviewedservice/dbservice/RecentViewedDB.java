package com.osc.recentlyviewedservice.dbservice;


import com.osc.avro.RecentViewHistory;
import com.osc.recentlyviewedservice.entity.RecentlyViewedEntity;
import com.osc.recentlyviewedservice.repository.RecentlyViewedRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecentViewedDB {

    private final RecentlyViewedRepository recentlyViewedRepository;


    public RecentViewedDB(RecentlyViewedRepository recentlyViewedRepository) {
        this.recentlyViewedRepository = recentlyViewedRepository;
    }

    public void saveRVListInDB(String userId, List<String> recentlyViewedList){
        RecentlyViewedEntity entity = new RecentlyViewedEntity(userId, recentlyViewedList);
        recentlyViewedRepository.save(entity);
    }

    public Map<String, RecentViewHistory> fetchFromDB() {
        List<RecentlyViewedEntity> recentlyViewedEntities = recentlyViewedRepository.findAll();
        return convertToRecentViewed(recentlyViewedEntities);
    }

    private Map<String, RecentViewHistory> convertToRecentViewed(List<RecentlyViewedEntity> recentlyViewedEntities) {
        return recentlyViewedEntities.stream()
                .collect(Collectors.toMap(
                        RecentlyViewedEntity::getUserId, // Extract the userId as the key
                        entity -> RecentViewHistory.newBuilder()
                                .setProductIds(new ArrayList<>(entity.getProductIds())) // Cast List<String> to List<CharSequence>
                                .build()
                ));
    }



}
