package ma.msa.microservices.core.review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("ma.msa")
public class MsaReviewServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsaReviewServiceApplication.class, args);
	}

}
