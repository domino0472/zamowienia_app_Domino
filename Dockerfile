# ETAP 1: BUILDER
FROM maven:3.9.11-amazoncorretto-25 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ETAP 2: RUNTIME
FROM maven:3.9.11-amazoncorretto-25
WORKDIR /app
COPY --from=builder /app/target/main-exec.jar /app/main.jar

# KOREKTA (BŁĄD W LINII 9 POPRZEDNIEGO LOGU):
# Pliki zasobów skopiowane w etapie BUILDER są dostępne w /app/src/main/resources
RUN cp /app/src/main/resources/Proporties/appsettings.docker.properties /app/appsettings.properties
RUN cp /app/src/main/resources/cops.xml /app/cops.xml

ENTRYPOINT ["java", "-jar", "/app/main.jar"]