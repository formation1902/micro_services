package ma.msa.microservices.core.review;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;


public abstract class MySqlTestBase {
	
	private static JdbcDatabaseContainer database = new MySQLContainer();

	static {
	  database.start();
	}
  
	@DynamicPropertySource
	static void databaseProperties(DynamicPropertyRegistry registry) {
	  registry.add("spring.datasource.url", database::getJdbcUrl);
	  registry.add("spring.datasource.username", database::getUsername);
	  registry.add("spring.datasource.password", database::getPassword);
	}
}
