spring init \
	--boot-version=3.1.0 \
	--type=gradle-project \
	--java-version=17 \
	--packaging=jar \
	--name=msa-api \
	--package-name=ma.msa.microservices.api \
	--groupId=ma.msa.microservices.api \
	--dependencies=actuator,webflux \
	--version=1.0.0-SNAPSHOT \
	api


spring init \
	--boot-version=3.1.0 \
	--type=gradle-project \
	--java-version=17 \
	--packaging=jar \
	--name=msa-util \
	--package-name=ma.msa.microservices.util \
	--groupId=ma.msa.microservices.util \
	--dependencies=actuator,webflux \
	--version=1.0.0-SNAPSHOT \
	util

