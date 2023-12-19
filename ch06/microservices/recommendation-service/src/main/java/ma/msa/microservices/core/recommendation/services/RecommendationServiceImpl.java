package ma.msa.microservices.core.recommendation.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

import ma.msa.api.core.recommendation.Recommendation;
import ma.msa.api.core.recommendation.RecommendationService;
import ma.msa.api.exceptions.InvalidInputException;
import ma.msa.microservices.core.recommendation.persistence.RecommendationEntity;
import ma.msa.microservices.core.recommendation.persistence.RecommendationRepository;
import ma.msa.util.http.ServiceUtil;

@RestController
public class RecommendationServiceImpl implements RecommendationService {

  private static final Logger logger = LoggerFactory.getLogger(RecommendationServiceImpl.class);

  private final RecommendationRepository recommendationRepository;
  private final RecommendationMapper recommendationMapper;
  
  private final ServiceUtil serviceUtil;

  @Autowired
  public RecommendationServiceImpl(
			  RecommendationRepository recommendationRepository,
			  RecommendationMapper recommendationMapper, 
			  ServiceUtil serviceUtil
		 )   {
				super();
				this.recommendationRepository = recommendationRepository;
				this.recommendationMapper = recommendationMapper;
				this.serviceUtil = serviceUtil;
  }
  
  @Override
  public Recommendation createRecommendation(Recommendation body) {
    try {
      RecommendationEntity entity = recommendationMapper.apiToEntity(body);
      RecommendationEntity newEntity = recommendationRepository.save(entity);

      logger.debug("createRecommendation: created a recommendation entity: {}/{}", body.getProductId(), body.getRecommendationId());
      return recommendationMapper.entityToApi(newEntity);

    } catch (DuplicateKeyException dke) {
      throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Recommendation Id:" + body.getRecommendationId());
    }
  }
  
  @Override
  public void deleteRecommendations(int productId) {
    logger.debug("deleteRecommendations: tries to delete recommendations for the product with productId: {}", productId);
    recommendationRepository.deleteAll(recommendationRepository.findByProductId(productId));
  }
  
  
  @Override
  public List<Recommendation> getRecommendations(int productId) {

    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }
    
    List<RecommendationEntity> entityList = recommendationRepository.findByProductId(productId);
    List<Recommendation> list = recommendationMapper.entityListToApiList(entityList);
    list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

    logger.debug("getRecommendations: response size: {}", list.size());

    return list;
  } 

}
