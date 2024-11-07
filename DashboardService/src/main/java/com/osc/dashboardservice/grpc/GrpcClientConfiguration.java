package com.osc.dashboardservice.grpc;

import com.grpc.cart.CartServiceGrpc;
import com.grpc.product.ProductDashboardServiceGrpc;
import com.grpc.recentHistory.RecentlyViewedServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfiguration {


    @Bean
    public ManagedChannel managedChannel1() {

        return ManagedChannelBuilder.forAddress("localhost", 9097)
                .usePlaintext()
                .build();
    }

    @Bean
    public RecentlyViewedServiceGrpc.RecentlyViewedServiceBlockingStub recentlyViewedDataServiceGrpc(@Qualifier("managedChannel1") ManagedChannel managedChannel1) {
        return RecentlyViewedServiceGrpc.newBlockingStub(managedChannel1);
    }

    @Bean
    public ManagedChannel managedChannel2() {
        return ManagedChannelBuilder.forAddress("localhost", 9088)
                .usePlaintext()
                .build();
    }

    @Bean
    public ProductDashboardServiceGrpc.ProductDashboardServiceBlockingStub productDataServiceGrpc(@Qualifier("managedChannel2") ManagedChannel managedChannel2) {
        return ProductDashboardServiceGrpc.newBlockingStub(managedChannel2);
    }
    @Bean
    public ManagedChannel managedChannel3() {
        return ManagedChannelBuilder.forAddress("localhost", 9009)
                .usePlaintext()
                .build();
    }

    @Bean
    public CartServiceGrpc.CartServiceBlockingStub cartServiceGrpc(@Qualifier("managedChannel3") ManagedChannel managedChannel3) {
        return CartServiceGrpc.newBlockingStub(managedChannel3);
    }
}
