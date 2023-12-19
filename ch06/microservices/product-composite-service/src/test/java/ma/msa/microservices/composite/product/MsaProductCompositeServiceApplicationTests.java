package ma.msa.microservices.composite.product;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import ma.msa.api.composite.product.ProductAggregate;
import ma.msa.api.composite.product.RecommendationSummary;
import ma.msa.api.composite.product.ReviewSummary;
import ma.msa.api.core.product.Product;
import ma.msa.api.core.recommendation.Recommendation;
import ma.msa.api.core.review.Review;
import ma.msa.api.exceptions.InvalidInputException;
import ma.msa.api.exceptions.NotFoundException;
import ma.msa.microservices.composite.product.services.ProductCompositeIntegration;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class MsaProductCompositeServiceApplicationTests {

	private static final int PRODUCT_ID_OK = 1;
	private static final int PRODUCT_ID_NOT_FOUND = 2;
	private static final int PRODUCT_ID_INVALID = 3;

	@Autowired
	private WebTestClient web_test_client;

	@MockBean
	private ProductCompositeIntegration productCompositeIntegration;

	@BeforeEach
	void setUp() {
		System.out.println("\n\nsetUp fired()");
		when(productCompositeIntegration.getProduct(PRODUCT_ID_OK))
		.thenReturn(new Product(PRODUCT_ID_OK, "name", 1, "mock-address"));
		System.out.println("setUp fired()---- 1");
		when(productCompositeIntegration.getRecommendations(PRODUCT_ID_OK))
		.thenReturn(singletonList(new Recommendation(PRODUCT_ID_OK, 1, "author", 1, "content", "mock address")));
		System.out.println("setUp fired()---- 2");
		when(productCompositeIntegration.getReviews(PRODUCT_ID_OK))
		.thenReturn(singletonList(new Review(PRODUCT_ID_OK, 1, "author", "subject", "content", "mock address")));
		System.out.println("setUp fired()---- 3");
		when(productCompositeIntegration.getProduct(PRODUCT_ID_NOT_FOUND))
		.thenThrow(new NotFoundException("NOT FOUND: " + PRODUCT_ID_NOT_FOUND));
		System.out.println("setUp fired()---- 4");
		when(productCompositeIntegration.getProduct(PRODUCT_ID_INVALID))
		.thenThrow(new InvalidInputException("INVALID: " + PRODUCT_ID_INVALID));
		System.out.println("setUp done\n\n");
	}

	@Test
	void contextLoads() {
	}

	@Test
	void createCompositeProduct1() {
		ProductAggregate compositeProduct = new ProductAggregate(1, "name", 1, null, null, null);
		postAndVerifyProduct(compositeProduct, OK);

	}

	@Test
	void createCompositeProduct2() {
		ProductAggregate compositeProduct = new ProductAggregate(
				1, "name", 1,
				singletonList(new RecommendationSummary(1, "a", 1, "c")),
				singletonList(new ReviewSummary(1, "a", "s", "c")), 
				null
		);
		postAndVerifyProduct(compositeProduct, OK);
	}

	@Test
	void deleteCompositeProduct() {
		ProductAggregate compositeProduct = new ProductAggregate(
				1, "name", 1,
				singletonList(new RecommendationSummary(1, "a", 1, "c")),
				singletonList(new ReviewSummary(1, "a", "s", "c")), 
				null
		);
		postAndVerifyProduct(compositeProduct, OK);

		deleteAndVerifyProduct(compositeProduct.getProductId(), OK);
		deleteAndVerifyProduct(compositeProduct.getProductId(), OK);
	}

	@Test
	void getProductById() {
		getAndVerifyProduct(PRODUCT_ID_OK, OK).jsonPath("$.productId").isEqualTo(PRODUCT_ID_OK)
				.jsonPath("$.recommendations.length()").isEqualTo(1).jsonPath("$.reviews.length()").isEqualTo(1);
	}

	@Test
	void getProductNotFound() {
		getAndVerifyProduct(PRODUCT_ID_NOT_FOUND, NOT_FOUND)
			.jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_NOT_FOUND)
			.jsonPath("$.message").isEqualTo("NOT FOUND: " + PRODUCT_ID_NOT_FOUND);
	}

	@Test
	void getProductInvalidInput() {
		getAndVerifyProduct(PRODUCT_ID_INVALID, UNPROCESSABLE_ENTITY)
			.jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_INVALID)
			.jsonPath("$.message").isEqualTo("INVALID: " + PRODUCT_ID_INVALID);
	}
	// -----------------------------------------------------------------------------------

	private WebTestClient.BodyContentSpec getAndVerifyProduct(int productId, HttpStatus expectedStatus) {
		return web_test_client.get()
				.uri("/product-composite/" + productId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private void postAndVerifyProduct(ProductAggregate compositeProduct, HttpStatus expectedStatus) {
		web_test_client.post()
			.uri("/product-composite")
			.body(just(compositeProduct), ProductAggregate.class)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus);
	}

	private void deleteAndVerifyProduct(int productId, HttpStatus expectedStatus) {
		web_test_client.delete()
			.uri("/product-composite/" + productId)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus);
	}

}
