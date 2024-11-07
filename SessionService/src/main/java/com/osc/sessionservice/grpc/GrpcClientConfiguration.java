package com.osc.sessionservice.grpc;

import com.grpc.session.SessionServiceGrpc;
import com.grpc.user.UserDataServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfiguration {

    @Bean
    public ManagedChannel managedChannel1() {

        return ManagedChannelBuilder.forAddress("localhost", 9093)
                .usePlaintext()
                .build();
    }

    @Bean
    public UserDataServiceGrpc.UserDataServiceBlockingStub userDataServiceGrpc(@Qualifier("managedChannel1") ManagedChannel managedChannel1) {
        return UserDataServiceGrpc.newBlockingStub(managedChannel1);
    }

    @Bean
    public ManagedChannel managedChannel2() {
        return ManagedChannelBuilder.forAddress("localhost", 9092)
                .usePlaintext()
                .build();
    }

    @Bean
    public SessionServiceGrpc.SessionServiceBlockingStub sessionDataServiceGrpc(@Qualifier("managedChannel2") ManagedChannel managedChannel2) {
        return SessionServiceGrpc.newBlockingStub(managedChannel2);
    }





}
