# ==========================================
# Stage 1: Build using full reactor context
# ==========================================
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /project

# Copy the entire root repository (parent pom and all submodules)
COPY . .

# Build ONLY twitter-data-plane and any sibling submodules it depends on:
#   -pl twitter-data-plane  -> Target project list (twitter-data-plane module)
#   -am           -> "Also Make" (compiles sibling submodules twitter-data-plane depends on)
RUN mvn clean package -pl twitter-data-plane -am -DskipTests

# ==========================================
# Stage 2: Lightweight Runtime Image
# ==========================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the compiled twitter-data-plane fat JAR from the build stage
COPY --from=build /project/twitter-data-plane/target/twitter-data-plane-1.0-SNAPSHOT.jar ./target/twitter-data-plane-1.0-SNAPSHOT.jar

# Copy twitter-data-plane configuration and run script
COPY twitter-data-plane/config/base.conf .
COPY twitter-data-plane/scripts/run.sh .

# The repository may be checked out with Windows line endings; normalize the
# shell script so Alpine can resolve its /bin/sh shebang.
RUN sed -i 's/\r$//' run.sh && chmod +x run.sh

EXPOSE 8080 8081

CMD ["./run.sh"]