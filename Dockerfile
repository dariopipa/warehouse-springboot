FROM maven:3.9-eclipse-temurin-21

WORKDIR /app

ARG jartocopy

COPY pom.xml .

# Cache dependencies
RUN mvn -B -DskipTests dependency:go-offline

COPY . .

RUN mvn -B -DskipTests package

RUN mv target/${jartocopy} app.jar

EXPOSE 6565

CMD ["java", "-jar", "app.jar"]
