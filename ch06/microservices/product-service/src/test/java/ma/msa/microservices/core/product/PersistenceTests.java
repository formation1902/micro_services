package ma.msa.microservices.core.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.data.domain.Sort.Direction.ASC;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import ma.msa.microservices.core.product.persistence.ProductEntity;
import ma.msa.microservices.core.product.persistence.ProductRepository;

/*
 * @DataMongoTest : starts up a MongoDB Database when the test starts
 */
@DataMongoTest
public class PersistenceTests extends MongoDbTestBase{

	private static final Logger logger = LoggerFactory.getLogger(PersistenceTests.class);

	
	@Autowired
	private ProductRepository product_repository;

	private ProductEntity savedEntity;

	@BeforeEach
	void setupDb() {
		System.out.println("--------- setupDB : ");
		System.out.println("--- Initial count = "+product_repository.count());
		product_repository.deleteAll();
		System.out.println("--- count = "+product_repository.count());
		ProductEntity entity = new ProductEntity(1, "n", 1);
		savedEntity = product_repository.save(entity);
		System.out.println("--- new count = "+product_repository.count());
		System.out.println("\t - savedEntity = " + savedEntity);
		assertEqualsProduct(entity, savedEntity);
		System.out.println("--------- setupDB : Done\n\n");
	}
	
	@Test
	void create() {
		ProductEntity newEntity = new ProductEntity(2, "n", 2);
	    product_repository.save(newEntity);

	    ProductEntity foundEntity = product_repository.findById(newEntity.getId()).get();
	    assertEqualsProduct(newEntity, foundEntity);

	    assertEquals(2, product_repository.count());
	}
	
	@Test
	void update() {
		savedEntity.setName("n2");
		product_repository.save(savedEntity);

		ProductEntity foundEntity = product_repository.findById(savedEntity.getId()).get();
		assertEquals(1, (long) foundEntity.getVersion());
		assertEquals("n2", foundEntity.getName());
	}

	@Test
	void delete() {
		product_repository.delete(savedEntity);
		assertFalse(product_repository.existsById(savedEntity.getId()));
	}

	@Test
	void getByProductId() {
		Optional<ProductEntity> entity = product_repository.findByProductId(savedEntity.getProductId());

		assertTrue(entity.isPresent());
		assertEqualsProduct(savedEntity, entity.get());
	}
	
	
//	@Test
//	void duplicateError() {
//		System.out.println("\n\n------------------ duplicateError fired.");
//    	assertThrows(
//			DuplicateKeyException.class, () -> {
//      			ProductEntity entity = new ProductEntity(savedEntity.getProductId(), "n", 1);
//      			product_repository.save(entity);
//				throw new DuplicateKeyException(("toto"));
//		    }
//		);
//		System.out.println("------------------ duplicateError done.\n\n");
//	  }

	@Test
	void optimisticLockError() {

		// Store the saved entity in two separate entity objects
		ProductEntity entity1 = product_repository.findById(savedEntity.getId()).get();
		ProductEntity entity2 = product_repository.findById(savedEntity.getId()).get();

		// Update the entity using the first entity object
		entity1.setName("n1");
		product_repository.save(entity1);

		// Update the entity using the second entity object.
		// This should fail since the second entity now holds an old version number,
		// i.e. an Optimistic Lock Error
		assertThrows(
				OptimisticLockingFailureException.class, () -> {
					entity2.setName("n2");
					product_repository.save(entity2);
				}
		);

		// Get the updated entity from the database and verify its new sate
		ProductEntity updatedEntity = product_repository.findById(savedEntity.getId()).get();
		assertEquals(1, (int) updatedEntity.getVersion());
		assertEquals("n1", updatedEntity.getName());
	}

	@Test
	void paging() {

		product_repository.deleteAll();

		List<ProductEntity> newProducts = IntStream.rangeClosed(1001, 1010).mapToObj(i -> new ProductEntity(i, "name " + i, i)).collect(Collectors.toList());
		product_repository.saveAll(newProducts);

		Pageable nextPage = PageRequest.of(0, 4, ASC, "productId");
		nextPage = testNextPage(nextPage, "[1001, 1002, 1003, 1004]", true);
		nextPage = testNextPage(nextPage, "[1005, 1006, 1007, 1008]", true);
		nextPage = testNextPage(nextPage, "[1009, 1010]", false);
	}

	// ------------------------------------------------------------------------------------------------------------------------
	
	private Pageable testNextPage(Pageable nextPage, String expectedProductIds, boolean expectsNextPage) {
		Page<ProductEntity> productPage = product_repository.findAll(nextPage);
		assertEquals(expectedProductIds,productPage.getContent().stream().map(p -> p.getProductId()).collect(Collectors.toList()).toString());
		assertEquals(expectsNextPage, productPage.hasNext());
		return productPage.nextPageable();
	}
	

	private void assertEqualsProduct(ProductEntity expectedEntity, ProductEntity actualEntity) {
		assertEquals(expectedEntity.getId(), actualEntity.getId());
		assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
		assertEquals(expectedEntity.getProductId(), actualEntity.getProductId());
		assertEquals(expectedEntity.getName(), actualEntity.getName());
		assertEquals(expectedEntity.getWeight(), actualEntity.getWeight());
	}

}