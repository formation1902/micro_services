package ma.msa.microservices.composite.product.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import ma.msa.api.composite.product.ProductAggregate;
import ma.msa.api.composite.product.ProductCompositeService;
import ma.msa.api.composite.product.RecommendationSummary;
import ma.msa.api.composite.product.ReviewSummary;
import ma.msa.api.composite.product.ServiceAddresses;
import ma.msa.api.core.product.Product;
import ma.msa.api.core.recommendation.Recommendation;
import ma.msa.api.core.review.Review;
import ma.msa.api.exceptions.NotFoundException;
import ma.msa.util.http.ServiceUtil;

@RestController
public class ProductCompositeServiceImpl implements ProductCompositeService {

	private final static Logger logger = LoggerFactory.getLogger(ProductCompositeServiceImpl.class);

	private final ServiceUtil serviceUtil;
	private ProductCompositeIntegration integration;

	@Autowired
	public ProductCompositeServiceImpl(ServiceUtil serviceUtil, ProductCompositeIntegration integration) {
		this.serviceUtil = serviceUtil;
		this.integration = integration;
	}

	@Override
	public void createProduct(ProductAggregate body) {

		try {

			logger.debug("createCompositeProduct: creates a new composite entity for productId: {}", body.getProductId());

			Product product = new Product(body.getProductId(), body.getName(), body.getWeight(), null);
			
			integration.createProduct(product);

			if (body.getRecommendations() != null) {
				body.getRecommendations().forEach(r -> {
					Recommendation recommendation = new Recommendation(body.getProductId(), r.getRecommendationId(),
							r.getAuthor(), r.getRate(), r.getContent(), null);
					integration.createRecommendation(recommendation);
				});
			}

			if (body.getReviews() != null) {
				body.getReviews().forEach(r -> {
					Review review = new Review(body.getProductId(), r.getReviewId(), r.getAuthor(), r.getSubject(),
							r.getContent(), null);
					integration.createReview(review);
				});
			}

			logger.debug("createCompositeProduct: composite entities created for productId: {}", body.getProductId());

		} catch (RuntimeException re) {
			logger.warn("createCompositeProduct failed", re);
			throw re;
		}
	}

	@Override
	public void deleteProduct(int productId) {

		logger.debug("deleteCompositeProduct: Deletes a product aggregate for productId: {}", productId);

		integration.deleteProduct(productId);

		integration.deleteRecommendations(productId);

		integration.deleteReviews(productId);

		logger.debug("deleteCompositeProduct: aggregate entities deleted for productId: {}", productId);
	}

	@Override
	public ProductAggregate getProduct(int productId) {

		Product product = integration.getProduct(productId);
		if (product == null) {
			throw new NotFoundException("No product found for productId: " + productId);
		}

		List<Recommendation> recommendations = integration.getRecommendations(productId);

		List<Review> reviews = integration.getReviews(productId);

		return createProductAggregate(product, recommendations, reviews, serviceUtil.getServiceAddress());
	}

	// --------------------------------------------------------------------------------------------------------------
	private ProductAggregate createProductAggregate(Product product, List<Recommendation> recommendations,
			List<Review> reviews, String serviceAddress) {
		// 1. Setup product info
		int productId = product.getProductId();
		String name = product.getName();
		int weight = product.getWeight();

		// 2. Copy summary recommendation info, if available
		List<RecommendationSummary> recommendationSummaries = (recommendations == null) ? null
				: recommendations.stream().map(r -> new RecommendationSummary(r.getRecommendationId(), r.getAuthor(),
						r.getRate(), r.getContent())).collect(Collectors.toList());

		// 3. Copy summary review info, if available
		List<ReviewSummary> reviewSummaries = (reviews == null) ? null
				: reviews.stream()
						.map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent()))
						.collect(Collectors.toList());

		// 4. Create info regarding the involved microservices addresses
		String productAddress = product.getServiceAddress();
		String reviewAddress = (reviews != null && reviews.size() > 0) 
				? reviews.get(0).getServiceAddress() 
				: "";
		String recommendationAddress = (recommendations != null && recommendations.size() > 0)
				? recommendations.get(0).getServiceAddress()
				: "";
		ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, productAddress, reviewAddress, recommendationAddress);

		return new ProductAggregate(productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);
	}
}
