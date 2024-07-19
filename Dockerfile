FROM bellsoft/liberica-runtime-container:jdk-21-slim-musl
COPY target/quiz-*.jar app.jar
EXPOSE 8080
CMD ["java", "-showversion", "-jar", "app.jar"]