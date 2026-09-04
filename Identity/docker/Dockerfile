# ==========================================
# Stage 1: Build using full reactor context
# ==========================================
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /project

# Copy the entire root repository (parent pom and all submodules)
COPY . .

# Build ONLY identity and any sibling submodules it depends on:
#   -pl identity  -> Target project list (identity module)
#   -am           -> "Also Make" (compiles sibling submodules identity depends on)
RUN mvn clean package -pl identity -am -DskipTests

# ==========================================
# Stage 2: Lightweight Runtime Image
# ==========================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the compiled identity fat JAR from the build stage
COPY --from=build /project/identity/target/identity-1.0-SNAPSHOT.jar ./target/identity-1.0-SNAPSHOT.jar

# Copy identity configuration and run script
COPY identity/config/base.conf .
COPY identity/scripts/run.sh .

RUN chmod +x run.sh

EXPOSE 8080 8081

CMD ["./run.sh"]