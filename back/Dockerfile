# Stage 1: Build the application using Maven
FROM maven:3.9.5-eclipse-temurin-17 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .

# Download project dependencies
RUN mvn dependency:go-offline -B

# Copy the entire project
COPY .. .

# Package the application as a WAR file
RUN mvn package -DskipTests

# Stage 2: Deploy the application in a Tomcat server
FROM tomcat:10.1.13-jdk17-temurin

# Remove the default Tomcat webapps to make room for your app
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the WAR file from the build stage to Tomcat's webapps directory
COPY --from=build /app/target/web_lab4_1042.war /usr/local/tomcat/webapps/ROOT.war

# Expose the default Tomcat port
EXPOSE 8080

# Start the Tomcat server
CMD ["catalina.sh", "run"]
