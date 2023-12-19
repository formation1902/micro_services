spring init \
	--boot-version=3.1.0 \
	--type=gradle-project \
	--java-version=17 \
	--packaging=jar \
	--name=msa-product-service \
	--package-name=ma.msa.microservices.core.product \
	--groupId=ma.msa.microservices.core.product \
	--dependencies=actuator,webflux \
	--version=1.0.0-SNAPSHOT \
	product-service


spring init \
	--boot-version=3.1.0 \
	--type=gradle-project \
	--java-version=17 \
	--packaging=jar \
	--name=msa-review-service \
	--package-name=ma.msa.microservices.core.review \
	--groupId=ma.msa.microservices.core.review \
	--dependencies=actuator,webflux \
	--version=1.0.0-SNAPSHOT \
	review-service

spring init \
	--boot-version=3.1.0 \
	--type=gradle-project \
	--java-version=17 \
	--packaging=jar \
	--name=msa-recommendation-service \
	--package-name=ma.msa.microservices.core.recommendation \
	--groupId=ma.msa.microservices.core.recommendation \
	--dependencies=actuator,webflux \
	--version=1.0.0-SNAPSHOT \
	recommendation-service

spring init \
	--boot-version=3.1.0 \
	--type=gradle-project \
	--java-version=17 \
	--packaging=jar \
	--name=msa-product-composite-service \
	--package-name=ma.msa.microservices.composite.product \
	--groupId=ma.msa.microservices.composite.product \
	--dependencies=actuator,webflux \
	--version=1.0.0-SNAPSHOT \
	product-composite-service

