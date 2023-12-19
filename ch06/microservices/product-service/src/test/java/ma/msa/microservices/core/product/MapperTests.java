package ma.msa.microservices.core.product;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import ma.msa.api.core.product.Product;
import ma.msa.microservices.core.product.persistence.ProductEntity;
import ma.msa.microservices.core.product.services.ProductMapper;

public class MapperTests {

	private ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

	@Test
	void mapperTests() {

	assertNotNull(mapper);

	Product api = new Product(1, "n", 1, "sa");
	
	System.out.println("API = " + api);

	ProductEntity entity = mapper.apiToEntity(api);
	
	System.out.println("Entity = " + entity);

	assertEquals(api.getProductId(), entity.getProductId());
	assertEquals(api.getProductId(), entity.getProductId());
	assertEquals(api.getName(), entity.getName());
	assertEquals(api.getWeight(), entity.getWeight());

	Product api2 = mapper.entityToApi(entity);

	System.out.println("Api2 = " + api2);
	
	assertEquals(api.getProductId(), api2.getProductId());
	assertEquals(api.getProductId(), api2.getProductId());
	assertEquals(api.getName(),      api2.getName());
	assertEquals(api.getWeight(),    api2.getWeight());
	assertNull(api2.getServiceAddress());
	
	System.out.println("\n\nMappers : ");
	System.out.println("\t api    = "+api);
	System.out.println("\t entity = "+entity);
	System.out.println("\t api2   = "+api2);
	
	}
}
