FROM openjdk:8
#ADD target/bitly-url.jar bitly-url.jar
ADD *.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
