package com.osc.productdataservice.grpc;
import com.grpc.product.*;
import com.osc.avro.ProductDetails;
import com.osc.productdataservice.kafka.consumer.KafkaRecordService;
import com.osc.productdataservice.mapper.GrpcConversionUtility;
import com.osc.productdataservice.service.dashboard.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@GrpcService
public class ProductDataService extends ProductDashboardServiceGrpc.ProductDashboardServiceImplBase {

    private static final Logger log = LogManager.getLogger(ProductDataService.class);

    private final CategoryService categoryService;
    private final FeaturedProductService featuredProductService;
    private final SimilarProductService similarProductService;
    private final RecentlyViewedService recentlyViewedService;
    private final ProductViewCountKafka productViewCountKafka;
    private final FilterService filterService;
    private final KafkaRecordService kafkaRecordService;

    public ProductDataService(CategoryService categoryService, FeaturedProductService featuredProductService, SimilarProductService similarProductService, RecentlyViewedService recentlyViewedService, ProductViewCountKafka productViewCountKafka, FilterService filterService, KafkaRecordService kafkaRecordService) {
        this.categoryService = categoryService;
        this.featuredProductService = featuredProductService;
        this.similarProductService = similarProductService;
        this.recentlyViewedService = recentlyViewedService;
        this.productViewCountKafka = productViewCountKafka;
        this.filterService = filterService;
        this.kafkaRecordService = kafkaRecordService;
    }

    @Override
    public void fetchProductDashboard(DashboardRequest request, StreamObserver<DashboardResponse> responseObserver) {
        // Extract product IDs from the request
        List<String> productIdsList = request.getProductIdList();

        // Prepare a builder for DashboardDetails
        DashboardDetails.Builder dashboardDetailsBuilder = DashboardDetails.newBuilder();

        try {
            // Fetch categories and add them to the response
            List<Category> grpcCategories = categoryService.aggregateProductViewsByCategory();
            dashboardDetailsBuilder.addAllCategories(grpcCategories);

            if (productIdsList.isEmpty()) {
                // New user: return categories and featured products
                List<Product> grpcFeaturedProducts = featuredProductService.getFeaturedProducts();
                dashboardDetailsBuilder.addAllFeaturedProducts(grpcFeaturedProducts);

            } else {
                // Returning user: Get the ProductDetails for each recently viewed product
                Map<String, ProductDetails> recentlyViewedProductDetails
                        = recentlyViewedService.getProductDetails(productIdsList);
                log.info("Fetched recently viewed product details: {}", recentlyViewedProductDetails);

                List<Product> grpcRecentlyViewedProducts
                        = recentlyViewedService.convertToGrpcProducts(recentlyViewedProductDetails);

                dashboardDetailsBuilder.addAllRecentlyViewedProducts(grpcRecentlyViewedProducts);

                // Extract category IDs from the recently viewed products
                List<String> categoryIds = recentlyViewedProductDetails.values().stream().map(productDetails -> productDetails.getCategoryId().toString()).collect(Collectors.toList());

                log.info("Category IDs from recently viewed products: {}", categoryIds);

                // Fetch similar products using these category IDs
                List<Product> grpcSimilarProducts = similarProductService.findSimilarProducts(categoryIds, productIdsList);
                dashboardDetailsBuilder.addAllSimilarProducts(grpcSimilarProducts);
            }
            // Build and send the response
            DashboardResponse response = DashboardResponse.newBuilder().setDashboardDetails(dashboardDetailsBuilder.build()).build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            // Log the error and send an empty response or an error message if necessary
            log.error("Error fetching product dashboard: ", e);
            DashboardResponse response = DashboardResponse.newBuilder().setDashboardDetails(DashboardDetails.newBuilder().build()) // Empty dashboard details
                    .build();
            responseObserver.onNext(response);
        } finally {
            // Complete the response
            responseObserver.onCompleted();
        }
    }


    @Override
    public void fetchProductDetails(ProductDataRequest request, StreamObserver<ProductDataResponse> responseObserver) {
        String productId = request.getProductId();
        log.info("Received request to fetch product details for productId: {}", productId);
        try {
            // Fetch Product Details from Kafka Topic
            ProductDetails productDetails = kafkaRecordService.getProductByProductId(productId);
            // If no product is found, return an error response or an empty object
            if (productDetails == null) {
                log.error("Product not found for ID: {}", productId);
                responseObserver.onError(new RuntimeException("Product not found for ID: " + productId));
                return;
            }
            log.info("Fetched ProductDetails: {}", productDetails);

//            Fetch Similar Products for that ProductId.
            List<Product> grpcSimilarProducts = similarProductService.findSimilarProductsByCategory(productDetails.getCategoryId().toString(), productId);

            //Produce updated count to Kafka
            productViewCountKafka.updateProductViewCount(productId);

            // Convert the ProductDetails (from Kafka) to gRPC Product object
            Product grpcProduct = GrpcConversionUtility.convertAvroToGrpcProduct(productDetails);

            // Build the response
            ProductDataResponse response = ProductDataResponse.newBuilder()
                    .setProduct(grpcProduct)
                    .addAllProducts(grpcSimilarProducts)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            // Handle any unexpected errors
            log.error("Error fetching product details for productId: {}", productId, e);
            responseObserver.onError(new RuntimeException("Error fetching product details: " + e.getMessage()));
        }
    }


    @Override
    public void fetchProductList(ProductListRequest request, StreamObserver<ProductListResponse> responseObserver) {
        List<String> productIds = request.getProductIdsList();
        List<ProductDetails> productDetailsList = new LinkedList<>();

        log.info("Received request to fetch product list for productIds: {}", productIds);

        try {
            // Fetch product details for each product ID
            for (String productId : productIds) {
                ProductDetails productDetails = kafkaRecordService.getProductByProductId(productId);
                if (productDetails != null) {
                    productDetailsList.add(productDetails);
                    log.info("Fetched ProductDetails for productId: {}", productId);
                } else {
                    log.warn("Product not found for productId: {}", productId);
                }
            }

            // Convert ProductDetails to gRPC Product format
            List<Product> grpcProducts = productDetailsList.stream()
                    .map(GrpcConversionUtility::convertAvroToGrpcProduct)
                    .collect(Collectors.toList());

            // Build and send the response
            ProductListResponse response = ProductListResponse.newBuilder()
                    .addAllProducts(grpcProducts)
                    .build();

            responseObserver.onNext(response); // Send the response
            responseObserver.onCompleted(); // Mark the end of the response
            log.info("ProductListResponse successfully sent with {} products", grpcProducts.size());

        } catch (Exception e) {
            // Handle any unexpected errors
            log.error("Error fetching product list for productIds: {}", productIds, e);
            responseObserver.onError(new RuntimeException("Error fetching product list: " + e.getMessage()));
        }
    }


    @Override
    public void filterProduct(FilterProductRequest request, StreamObserver<FilterProductResponse> responseObserver) {
        String categoryId = request.getCategoryId();
        String filterCriteria = request.getFilterCriteria();

        log.info("Received filter product request for categoryId: {}, filterCriteria: {}", categoryId, filterCriteria);

        try {
            List<ProductDetails> productsByCategoryId = filterService.fetchProductsByCategory(categoryId);

            List<Product> grpcProducts = filterService.filterProductsList(productsByCategoryId, filterCriteria);

            // Build and send the response
            FilterProductResponse response = FilterProductResponse.newBuilder()
                    .addAllProducts(grpcProducts)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            log.info("FilterProductResponse successfully sent with {} products", grpcProducts.size());
        } catch (Exception e) {
            // Handle any unexpected errors
            log.error("Error processing filter product request for categoryId: {}, filterCriteria: {}", categoryId, filterCriteria, e);
            responseObserver.onError(new RuntimeException("Error processing filter product request: " + e.getMessage()));
        }
    }
}
