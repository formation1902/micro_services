package ma.msa.microservices.core.review.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;

import ma.msa.api.core.review.Review;
import ma.msa.api.core.review.ReviewService;
import ma.msa.api.exceptions.InvalidInputException;
import ma.msa.microservices.core.review.persistence.ReviewEntity;
import ma.msa.microservices.core.review.persistence.ReviewRepository;
import ma.msa.util.http.ServiceUtil;

@RestController
public class ReviewServiceImpl implements ReviewService {

	private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);

	private final ReviewRepository 	reviewRepository;
	private final ReviewMapper 		reviewMapper;
	private final ServiceUtil 		serviceUtil;

	@Autowired
	public ReviewServiceImpl(ReviewRepository reviewRepository, ReviewMapper reviewMapper, ServiceUtil serviceUtil) {
		super();
		this.reviewRepository = reviewRepository;
		this.reviewMapper = reviewMapper;
		this.serviceUtil = serviceUtil;
	}

	@Override
	public Review createReview(Review body) {
		try {
			ReviewEntity entity = reviewMapper.apiToEntity(body);
			ReviewEntity newEntity = reviewRepository.save(entity);

			logger.debug("createReview: created a review entity: {}/{}", body.getProductId(), body.getReviewId());
			return reviewMapper.entityToApi(newEntity);

		} catch (DataIntegrityViolationException dive) {
			throw new InvalidInputException(
					"Duplicate key, Product Id: " + body.getProductId() + ", Review Id:" + body.getReviewId());
		}
	}

	@Override
	public List<Review> getReviews(int productId) {

		if (productId < 1) {
			throw new InvalidInputException("Invalid productId: " + productId);
		}

		List<ReviewEntity> entityList = reviewRepository.findByProductId(productId);
		List<Review> list = reviewMapper.entityListToApiList(entityList);
		list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

		logger.debug("getReviews: response size: {}", list.size());

		return list;
	}

	@Override
	public void deleteReviews(int productId) {
		logger.debug("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
		reviewRepository.deleteAll(reviewRepository.findByProductId(productId));
	}

}
