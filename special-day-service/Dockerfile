FROM openjdk:17
ARG JAR_FILE=./target/*.jar
COPY ${JAR_FILE} special-day-service.jar
ENTRYPOINT ["java","-jar","./special-day-service.jar"]