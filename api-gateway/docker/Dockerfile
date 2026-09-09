# ==========================================
# Stage 1: Build using full reactor context
# ==========================================
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /project

# Copy the entire root repository (parent pom and all submodules)
COPY . .

# Build ONLY api-gateway and any sibling submodules it depends on:
#   -pl api-gateway  -> Target project list (api-gateway module)
#   -am           -> "Also Make" (compiles sibling submodules api-gateway depends on)
RUN mvn clean package -pl api-gateway -am -DskipTests

# ==========================================
# Stage 2: Lightweight Runtime Image
# ==========================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the compiled api-gateway fat JAR from the build stage
COPY --from=build /project/api-gateway/target/api-gateway-1.0-SNAPSHOT.jar ./target/api-gateway-1.0-SNAPSHOT.jar

# Copy api-gateway configuration and run script
COPY api-gateway/config/base.conf .
COPY api-gateway/scripts/run.sh .

RUN sed -i 's/\r$//' run.sh && chmod +x run.sh

EXPOSE 8082 8083

CMD ["./run.sh"]