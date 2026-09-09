# ==========================================
# Stage 1: Build using full reactor context
# ==========================================
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /project

# Copy the entire root repository (parent pom and all submodules)
COPY . .

# Build ONLY Identity and any sibling submodules it depends on:
#   -pl identity  -> Target project list (identity module)
#   -am           -> "Also Make" (compiles sibling submodules identity depends on)
RUN mvn clean package -pl identity -am -DskipTests

# ==========================================
# Stage 2: Lightweight Runtime Image
# ==========================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the compiled identity fat JAR from the build stage
COPY --from=build /project/Identity/target/identity-1.0-SNAPSHOT.jar ./target/identity-1.0-SNAPSHOT.jar

# Copy identity configuration and run script
COPY Identity/config/base.conf .
COPY Identity/scripts/run.sh .

# The repository may be checked out with Windows line endings; normalize the
# shell script so Alpine can resolve its /bin/sh shebang.
RUN sed -i 's/\r$//' run.sh && chmod +x run.sh

EXPOSE 8080 8081

CMD ["./run.sh"]
