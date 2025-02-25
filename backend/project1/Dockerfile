FROM openjdk:21
WORKDIR /project1
ARG SPRING_PROFILES_ACTIVE
ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE
CMD ["./gradlew", "clean", "bootJar"]
COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
