FROM maven:3.9-eclipse-temurin-21
WORKDIR /app

COPY pom.xml .

#Caches the pom.xml dependencies so in other builds it doesnt have to take that long.
RUN mvn dependency:go-offline

COPY . .

RUN mvn -B -DskipTests package

RUN mv target/*.jar target/app.jar

CMD ["java", "-jar", "target/app.jar"]

