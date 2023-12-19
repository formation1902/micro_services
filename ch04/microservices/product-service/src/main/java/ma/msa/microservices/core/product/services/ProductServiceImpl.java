package ma.msa.microservices.core.product.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import ma.msa.api.core.product.Product;
import ma.msa.api.core.product.ProductService;
import ma.msa.api.exceptions.InvalidInputException;
import ma.msa.api.exceptions.NotFoundException;
import ma.msa.util.http.ServiceUtil;

@RestController
public class ProductServiceImpl implements ProductService {

  private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

  private final ServiceUtil serviceUtil;

  @Autowired
  public ProductServiceImpl(ServiceUtil serviceUtil) {
    this.serviceUtil = serviceUtil;
  }

  @Override
  public Product getProduct(int productId) {
    logger.debug("/product return the found product for productId={}", productId);

    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    if (productId == 13) {
      throw new NotFoundException("No product found for productId: " + productId);
    }

    
    // int productId, String name, int weight, String serviceAddress
    return new Product(productId, "name-" + productId, 123, serviceUtil.getServiceAddress());
  }
}
