package com.cartdataservice.kafka.consumer;

import com.osc.avro.CartList;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.stereotype.Service;

@Service
public class CartStateStore {

    private final ReadOnlyKeyValueStore<String, CartList> cartStore;

    public CartStateStore(ReadOnlyKeyValueStore<String, CartList> cartStore) {
        this.cartStore = cartStore;
    }

    public CartList getCartList(String userId) {

        return cartStore.get(userId);
    }
}
