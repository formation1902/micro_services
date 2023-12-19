package ma.msa.microservices.core.review;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import ma.msa.api.core.review.Review;
import ma.msa.microservices.core.review.persistence.ReviewEntity;
import ma.msa.microservices.core.review.services.ReviewMapper;

public class MapperTests {

	private ReviewMapper reviewMapper = Mappers.getMapper(ReviewMapper.class);
	

	@Test
	  void mapperTests() {

	    assertNotNull(reviewMapper);

	    Review api = new Review(1, 2, "a", "s", "C", "adr");

	    ReviewEntity entity = reviewMapper.apiToEntity(api);

	    assertEquals(api.getProductId(), entity.getProductId());
	    assertEquals(api.getReviewId(), entity.getReviewId());
	    assertEquals(api.getAuthor(), entity.getAuthor());
	    assertEquals(api.getSubject(), entity.getSubject());
	    assertEquals(api.getContent(), entity.getContent());

	    Review api2 = reviewMapper.entityToApi(entity);

	    assertEquals(api.getProductId(), api2.getProductId());
	    assertEquals(api.getReviewId(), api2.getReviewId());
	    assertEquals(api.getAuthor(), api2.getAuthor());
	    assertEquals(api.getSubject(), api2.getSubject());
	    assertEquals(api.getContent(), api2.getContent());
	    assertNull(api2.getServiceAddress());
	  }

	  @Test
	  void mapperListTests() {

	    assertNotNull(reviewMapper);

	    Review api = new Review(1, 2, "a", "s", "C", "adr");
	    List<Review> apiList = Collections.singletonList(api);

	    List<ReviewEntity> entityList = reviewMapper.apiListToEntityList(apiList);
	    assertEquals(apiList.size(), entityList.size());

	    ReviewEntity entity = entityList.get(0);

	    assertEquals(api.getProductId(), entity.getProductId());
	    assertEquals(api.getReviewId(), entity.getReviewId());
	    assertEquals(api.getAuthor(), entity.getAuthor());
	    assertEquals(api.getSubject(), entity.getSubject());
	    assertEquals(api.getContent(), entity.getContent());

	    List<Review> api2List = reviewMapper.entityListToApiList(entityList);
	    assertEquals(apiList.size(), api2List.size());

	    Review api2 = api2List.get(0);

	    assertEquals(api.getProductId(), api2.getProductId());
	    assertEquals(api.getReviewId(), api2.getReviewId());
	    assertEquals(api.getAuthor(), api2.getAuthor());
	    assertEquals(api.getSubject(), api2.getSubject());
	    assertEquals(api.getContent(), api2.getContent());
	    assertNull(api2.getServiceAddress());
	  }
}
