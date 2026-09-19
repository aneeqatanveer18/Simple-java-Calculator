FROM eclipse-temurin:25-jdk

WORKDIR /app

COPY src/CalculatorWebApplication.java .

RUN javac --add-modules jdk.httpserver CalculatorWebApplication.java

CMD ["java", "--add-modules", "jdk.httpserver", "CalculatorWebApplication"]
