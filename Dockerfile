FROM openjdk:8
ADD target/bitly-url.jar bitly-url.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "bitly-url.jar"]
