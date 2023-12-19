FROM eclipse-temurin:17.0.5_8-jre-focal as step_one_extractor
WORKDIR extracted
ADD ./build/libs/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:17.0.5_8-jre-focal
WORKDIR application
COPY --from=step_one_extractor extracted/dependencies/          ./
COPY --from=step_one_extractor extracted/spring-boot-loader/    ./
COPY --from=step_one_extractor extracted/snapshot-dependencies/ ./
COPY --from=step_one_extractor extracted/application/           ./

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]