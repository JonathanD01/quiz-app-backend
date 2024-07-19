# Quiz App API
This Spring boot application lets you create quizzes with the help of AI. The AI used is ChatGPT. The app allows you to create, share and edit quizzes.

Used with `https://github.com/JonathanD01/quiz-app-frontend`

## Requirements
* JDK 21
* Maven
* Docker (for containerization)


## Getting started

1. Clone the repository
```
git clone https://github.com/JonathanD01/quiz-app-backend.git
```

2. Build the project
```
cd quiz-app-api
mvn clean install
```

3. Run the Application
```
java -jar target/quiz-0.0.1-SNAPSHOT.jar
```

4. Access the API
   Once the application is running, you can access the API(s).

## Configuration
* **AI configuration**: Set your ${OPENAI_API_KEY} value in the .env file or provide it at runtime.
* **Database configuration**: Configure database connection in properties in the `application.yml` file.