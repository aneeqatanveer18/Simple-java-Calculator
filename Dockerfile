FROM eclipse-temurin:25-jdk

WORKDIR /app

COPY src/webcalculator/CalculatorWebApplication.java webcalculator/CalculatorWebApplication.java

RUN javac --add-modules jdk.httpserver -d out webcalculator/CalculatorWebApplication.java

EXPOSE 8080

CMD ["java", "--add-modules", "jdk.httpserver", "-cp", "out", "webcalculator.CalculatorWebApplication"]
