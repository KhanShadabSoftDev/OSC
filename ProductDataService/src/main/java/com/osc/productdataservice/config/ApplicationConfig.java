package com.osc.productdataservice.config;

public class ApplicationConfig {

    public static final String APPLICATION_ID_CONFIG = "OSC_9" ;
    public static final String BOOTSTRAP_SERVER = "192.168.99.223:19092";
    public static final String SCHEMA_REGISTRY_URL = "http://192.168.99.223:18081";
    public static String CATEGORIES_DETAILS_TOPIC="KHAN-CATEGORY-TOPIC";
    public static String PRODUCT_DETAILS_TOPIC="KHAN-PRODUCT-TOPIC";
    public static String PRODUCT_VIEW_COUNT_TOPIC ="KHAN-PRODUCT-VIEW-COUNT-TOPIC";

    public static final String PRODUCT_STORE_NAME = "KHAN-PRODUCT-STORE";
    public static final String PRODUCT_VIEW_STORE_NAME = "KHAN-PRODUCT-VIEW-COUNT-STORE";

    public static final String CATEGORY_STORE_NAME = "KHAN-CATEGORY-STORE";
}
