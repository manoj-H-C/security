FROM eclipse-temurin:17

LABEL mentainer="manojhc110@gmail.com"

WORKDIR /app

COPY target/security-0.0.1-SNAPSHOT.jar /app/security.jar

ENTRYPOINT ["java", "-jar", "security.jar", "--spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]