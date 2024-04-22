# Use an official Maven image as a build stage
FROM maven:3.8.4-openjdk-11 AS build
WORKDIR /payment

# Copy the source code and POM file
COPY src ./src
COPY pom.xml .

# Build the application
RUN mvn clean install

# Use a smaller base image for the runtime stage
FROM openjdk:11-jre-slim
WORKDIR /payment

# Copy the built JAR file from the build stage
COPY --from=build /payment/target/payment-0.0.1-SNAPSHOT.jar ./app.jar

# Command to run the application
CMD ["java", "-jar", "app.jar", "-webAllowOthers"]