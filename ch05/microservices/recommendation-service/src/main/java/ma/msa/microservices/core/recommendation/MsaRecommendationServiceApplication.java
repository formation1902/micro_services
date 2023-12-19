package ma.msa.microservices.core.recommendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("ma.msa")
public class MsaRecommendationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsaRecommendationServiceApplication.class, args);
	}

}
