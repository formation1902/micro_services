package ma.msa.microservices.core.review;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import ma.msa.microservices.core.review.persistence.ReviewEntity;
import ma.msa.microservices.core.review.persistence.ReviewRepository;

/*
 * 		@DataJpaTest : starts up a SQL Database when the test starts
 * 	
 * 		@Transactional : Not_supported to disable automatic rollback
 * 
 * 		@AutoconfigureTestDatabase : 
 * 
 * 		extends MySQLTestBase : ---> Single Container Pattern
 * 
 */
@DataJpaTest
@Transactional(propagation = NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PersistenceTests extends MySqlTestBase{

	@Autowired
	private ReviewRepository reviewRepository;
	
	private ReviewEntity savedEntity;
	

	@BeforeEach
	void setupDb() {
		System.out.println("\n\n\n--------------------------- setupDb initiated\n\n\n");
		reviewRepository.deleteAll();
		ReviewEntity entity = new ReviewEntity(1, 2, "a", "s", "c");
		savedEntity = reviewRepository.save(entity);
		assertEqualsReview(entity, savedEntity);

		System.out.println("\n\n\n--------------------------- setupDB done\n\n\n");
	}

	@Test
	void create() {

		ReviewEntity newEntity = new ReviewEntity(1, 3, "a", "s", "c");
		reviewRepository.save(newEntity);

		ReviewEntity foundEntity = reviewRepository.findById(newEntity.getId()).get();
		assertEqualsReview(newEntity, foundEntity);

		assertEquals(2, reviewRepository.count());
	}

	@Test
	void update() {
		savedEntity.setAuthor("a2");
		reviewRepository.save(savedEntity);

		ReviewEntity foundEntity = reviewRepository.findById(savedEntity.getId()).get();
		assertEquals(1, (long)foundEntity.getVersion());
		assertEquals("a2", foundEntity.getAuthor());
	}

	@Test
	void delete() {
		reviewRepository.delete(savedEntity);
		assertFalse(reviewRepository.existsById(savedEntity.getId()));
	}

	@Test
	void getByProductId() {
		List<ReviewEntity> entityList = reviewRepository.findByProductId(savedEntity.getProductId());

		assertThat(entityList, hasSize(1));
		assertEqualsReview(savedEntity, entityList.get(0));
	}

	@Test
	void duplicateError() {
		assertThrows(DataIntegrityViolationException.class, () -> {
			ReviewEntity entity = new ReviewEntity(1, 2, "a", "s", "c");
			reviewRepository.save(entity);
		});

	}

	@Test
	void optimisticLockError() {
		// Store the saved entity in two separate entity objects
		ReviewEntity entity1 = reviewRepository.findById(savedEntity.getId()).get();
		ReviewEntity entity2 = reviewRepository.findById(savedEntity.getId()).get();

		// Update the entity using the first entity object
		entity1.setAuthor("a1");
		reviewRepository.save(entity1);

		// Update the entity using the second entity object.
		// This should fail since the second entity now holds an old version number, i.e. an Optimistic Lock Error
		assertThrows(OptimisticLockingFailureException.class, () -> {
			entity2.setAuthor("a2");
			reviewRepository.save(entity2);
		});

		// Get the updated entity from the database and verify its new sate
		ReviewEntity updatedEntity = reviewRepository.findById(savedEntity.getId()).get();
		assertEquals(1, (int)updatedEntity.getVersion());
		assertEquals("a1", updatedEntity.getAuthor());
	}

	// ------------------------------------------------------------------------------------------------
	private void assertEqualsReview(ReviewEntity expectedEntity, ReviewEntity actualEntity) {
		assertEquals(expectedEntity.getId(),        actualEntity.getId());
		assertEquals(expectedEntity.getVersion(),   actualEntity.getVersion());
		assertEquals(expectedEntity.getProductId(), actualEntity.getProductId());
		assertEquals(expectedEntity.getReviewId(),  actualEntity.getReviewId());
		assertEquals(expectedEntity.getAuthor(),    actualEntity.getAuthor());
		assertEquals(expectedEntity.getSubject(),   actualEntity.getSubject());
		assertEquals(expectedEntity.getContent(),   actualEntity.getContent());
	}
}
