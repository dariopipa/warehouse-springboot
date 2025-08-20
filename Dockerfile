FROM maven:3-eclipse-temurin-24

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
