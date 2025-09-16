FROM openjdk:17
EXPOSE 8080
ADD target/after-market-db-tool.jar after-market-db-tool.jar
ENTRYPOINT ["java","-jar","/after-market-db-tool.jar"]