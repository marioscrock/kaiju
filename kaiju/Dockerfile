FROM maven:3.5.3-jdk-8 AS build  
COPY src /usr/src/app/src  
COPY pom.xml /usr/src/app  
RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:8  
COPY --from=build /usr/src/app/target/kaiju-0.0.1-SNAPSHOT-jar-with-dependencies.jar /usr/app/kaiju-0.0.1-SNAPSHOT-jar-with-dependencies.jar 
EXPOSE 2042
#ENTRYPOINT ["java","-jar","/usr/app/kaiju-0.0.1-SNAPSHOT-jar-with-dependencies.jar"] 