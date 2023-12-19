package ma.msa.microservices.core.product;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import static reactor.core.publisher.Mono.just;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import ma.msa.api.core.product.Product;
import ma.msa.microservices.core.product.persistence.ProductRepository;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class MsaProductServiceApplicationTests extends MongoDbTestBase{

	@Autowired 
	private WebTestClient web_test_client;
	
	@Autowired
	private ProductRepository product_repository;
	
	@BeforeEach
	void setupDB() {
		System.out.println("--------------------------");
		product_repository.deleteAll();
	}
	


	  @Test
	  void getProductById() {
	    int productId = 1;
	    postAndVerifyProduct(productId, OK);
	    assertTrue(product_repository.findByProductId(productId).isPresent());
	    getAndVerifyProduct(productId, OK).jsonPath("$.productId").isEqualTo(productId);
	  }

	@Test
	void duplicateError() {
		int productId = 1;
		postAndVerifyProduct(productId, OK);
		assertTrue(product_repository.findByProductId(productId).isPresent());
		postAndVerifyProduct(productId, UNPROCESSABLE_ENTITY)
			.jsonPath("$.path").isEqualTo("/product")
			.jsonPath("$.message").isEqualTo("Duplicate key, Product Id: " + productId);
	}

	
	
	@Test
	void deleteProduct1() {
		
		int productId = 1;
		postAndVerifyProduct(productId, OK);
		assertTrue(product_repository.findByProductId(productId).isPresent());
		
		deleteAndVerifyProduct(productId, OK);
		assertFalse(product_repository.findByProductId(productId).isPresent());
		
		deleteAndVerifyProduct(productId, OK);
		
	}

	@Test
	void getProductInvalidParameterString() {
		getAndVerifyProduct("/no-integer", BAD_REQUEST)
			.jsonPath("$.path").isEqualTo("/product/no-integer")
			.jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	void getProductNotFound() {
		int productIdNotFound = 13;
		getAndVerifyProduct(productIdNotFound, NOT_FOUND)
			.jsonPath("$.path").isEqualTo("/product/" + productIdNotFound)
			.jsonPath("$.message").isEqualTo("No product found for productId: " + productIdNotFound);
	}

	@Test
	void getProductInvalidParameterNegativeValue() {
		int productIdInvalid = -1;
		getAndVerifyProduct(productIdInvalid, UNPROCESSABLE_ENTITY)
			.jsonPath("$.path").isEqualTo("/product/" + productIdInvalid)
			.jsonPath("$.message").isEqualTo("Invalid productId: " + productIdInvalid);
	}

	//----------------------------------------------------------------------------------------
	private WebTestClient.BodyContentSpec getAndVerifyProduct(int productId, HttpStatus expectedStatus) {
		return getAndVerifyProduct("/" + productId, expectedStatus);
	}
	
	private WebTestClient.BodyContentSpec getAndVerifyProduct(String productIdPath, HttpStatus expectedStatus) {
		System.out.println("---------- getAndVerifyProduct : "+productIdPath+"\n");
	    return web_test_client.get()
	      .uri("/product" + productIdPath)
	      .accept(APPLICATION_JSON)
	      .exchange()
	      .expectStatus().isEqualTo(expectedStatus)
	      .expectHeader().contentType(APPLICATION_JSON)
	      .expectBody();
	  }
	private WebTestClient.BodyContentSpec postAndVerifyProduct(int productId, HttpStatus expectedStatus) {
	   Product product = new Product(productId, "Name_" + productId,  productId, "SA");
	   return web_test_client.post()
	      .uri("/product")
	      .body(just(product),Product.class)
	      .accept(APPLICATION_JSON)
	      .exchange()
	      .expectStatus().isEqualTo(expectedStatus)
	      .expectHeader().contentType(APPLICATION_JSON)
	      .expectBody();
	}
	private WebTestClient.BodyContentSpec deleteAndVerifyProduct(int productId, HttpStatus expectedStatus) {
	    return web_test_client.delete()
	      .uri("/product/" + productId)
	      .accept(APPLICATION_JSON)
	      .exchange()
	      .expectStatus().isEqualTo(expectedStatus)
	      .expectBody();
	  }
}
