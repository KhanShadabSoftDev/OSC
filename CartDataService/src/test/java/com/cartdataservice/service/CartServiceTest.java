package com.cartdataservice.service;

import com.grpc.cart.CartDetailsResponse;
import com.grpc.cart.ProductDetail;
import com.osc.avro.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartProductService cartProductService;

    @Mock
    private CartSyncService cartSyncService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCartDetails_WithProducts() {
        String userId = "testUserId";
        CartDetailsResponse mockResponse = CartDetailsResponse.newBuilder()
                .addCartProducts(ProductDetail.newBuilder().setProductId("product1").setQuantity(2).build())
                .build();

        when(cartProductService.getCartDetails(userId)).thenReturn(mockResponse);

        CartDetailsResponse response = cartService.getCartDetails(userId);

        assertNotNull(response);
        assertEquals(1, response.getCartProductsCount());
        assertEquals("product1", response.getCartProducts(0).getProductId());
        verify(cartProductService).getCartDetails(userId);
    }

    @Test
    void testGetCartDetails_NoProducts() {
        String userId = "testUserId";

        when(cartProductService.getCartDetails(userId)).thenReturn(null);

        CartDetailsResponse response = cartService.getCartDetails(userId);

        assertNull(response);
        verify(cartProductService).getCartDetails(userId);
    }

    @Test
    void testUpdateProductQuantity_ProductExists() {
        String userId = "testUserId";
        String productId = "product1";
        boolean isIncrease = true;

        doNothing().when(cartProductService).modifyProductQuantity(userId, productId, isIncrease);

        cartService.updateProductQuantity(userId, productId, isIncrease);

        verify(cartProductService).modifyProductQuantity(userId, productId, isIncrease);
    }

    @Test
    void testRemoveProductFromCart_ProductExists() {
        String userId = "testUserId";
        String productId = "product1";

        doNothing().when(cartProductService).clearProductFromCart(userId, productId);

        cartService.removeProductFromCart(userId, productId);

        verify(cartProductService).clearProductFromCart(userId, productId);
    }

    @Test
    void testSyncCartWithDatabase_WithProducts() {
        String userId = "testUserId";
        List<Product> mockProducts = Collections.singletonList(Product.newBuilder().setProductId("product1").setProductQuantity(2).build());

        when(cartProductService.getUserCartProducts(userId)).thenReturn(mockProducts);

        cartService.syncCartWithDatabase(userId);

        verify(cartSyncService).persistCartToDatabase(userId, mockProducts);
    }

    @Test
    void testSyncCartWithDatabase_NoProducts() {
        String userId = "testUserId";

        when(cartProductService.getUserCartProducts(userId)).thenReturn(Collections.emptyList());

        cartService.syncCartWithDatabase(userId);

        verify(cartSyncService, never()).persistCartToDatabase(anyString(), anyList());
    }
}
