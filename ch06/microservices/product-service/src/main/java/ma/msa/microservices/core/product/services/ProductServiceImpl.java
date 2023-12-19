package ma.msa.microservices.core.product.services;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

import ma.msa.api.core.product.Product;
import ma.msa.api.core.product.ProductService;
import ma.msa.api.exceptions.InvalidInputException;
import ma.msa.api.exceptions.NotFoundException;
import ma.msa.microservices.core.product.persistence.ProductEntity;
import ma.msa.microservices.core.product.persistence.ProductRepository;
import ma.msa.util.http.ServiceUtil;

@RestController
public class ProductServiceImpl implements ProductService {

  private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

  private final ServiceUtil serviceUtil;

  private final ProductRepository product_repository;
  
  private final ProductMapper product_mapper;
  
  private static Function<Integer,NotFoundException> not_fount_exception_generator = product_id -> new NotFoundException("No product found for productId: " + product_id);
  private static BiFunction<Product,String,InvalidInputException> invalid_input_exception_generator = (product,message) -> new InvalidInputException(message +", Product Id: " + product.getProductId());
  
  
  
  @Autowired
  public ProductServiceImpl(ProductRepository product_repository, ProductMapper product_mapper, ServiceUtil serviceUtil) {
	super();
	this.serviceUtil = serviceUtil;
	this.product_repository = product_repository;
	this.product_mapper = product_mapper;
  }



	@Override
	public Product createProduct(Product body) {
		try {
	        ProductEntity entity    = product_mapper.apiToEntity(body);        
	        ProductEntity newEntity = product_repository.save(entity);
	        
	        logger.debug("createProduct: entity created for productId: {}", body.getProductId());
	        return product_mapper.entityToApi(newEntity);
	        
	    } catch (DuplicateKeyException dke) {
//	        throw new InvalidInputException("Duplicate key, Product Id: " +	        body.getProductId());
	    	throw invalid_input_exception_generator.apply(body,"Duplicate key");
	    }
	}

	  @Override
	  public Product getProduct(int productId) {
	    logger.debug("/product return the found product for productId={}", productId);

	    if (productId < 1) {
	      throw new InvalidInputException("Invalid productId: " + productId);
	    }

	    ProductEntity product_entity = product_repository.findByProductId(productId).orElseThrow(()->not_fount_exception_generator.apply(productId));
	    
	    Product product = product_mapper.entityToApi(product_entity);
	    
	    product.setServiceAddress(serviceUtil.getServiceAddress());
	    
	    logger.debug("getProduct: found productId: {}", product.getProductId());
	    
	    return product;
	  }
	  
	@Override
	public void deleteProduct(int productId) {
		logger.debug("deleteProduct: tries to delete an entity with productId: {}", productId);
		product_repository.findByProductId(productId).ifPresent(product->product_repository.delete(product));		
	}
}
