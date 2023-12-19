package ma.msa.microservices.core.review;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("ma.msa")
public class MsaReviewServiceApplication {

	private static final Logger logger = LoggerFactory.getLogger(MsaReviewServiceApplication.class);
	
	public static void main(String[] args) {
		
	    ConfigurableApplicationContext ctx = SpringApplication.run(MsaReviewServiceApplication.class, args);

	    String mysqlUri = ctx.getEnvironment().getProperty("spring.datasource.url");
	    logger.info("Connected to MySQL: " + mysqlUri);
	}

}
