package ma.msa.microservices.core.recommendation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("ma.msa")
public class MsaRecommendationServiceApplication {

	private static final Logger logger = LoggerFactory.getLogger(MsaRecommendationServiceApplication.class);

	  public static void main(String[] args) {
	    ConfigurableApplicationContext ctx = SpringApplication.run(MsaRecommendationServiceApplication.class, args);

	    String mongodDbHost = ctx.getEnvironment().getProperty("spring.data.mongodb.host");
	    String mongodDbPort = ctx.getEnvironment().getProperty("spring.data.mongodb.port");
	    logger.info("Connected to MongoDb: " + mongodDbHost + ":" + mongodDbPort);
	  }
}
