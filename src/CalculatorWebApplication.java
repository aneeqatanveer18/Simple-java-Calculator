import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class CalculatorWebApplication {

    public static void main(String[] args) throws Exception {

        int port = Integer.parseInt(
                System.getenv().getOrDefault("PORT", "8080")
        );

        HttpServer server = HttpServer.create(
                new InetSocketAddress(port), 0
        );

        server.createContext("/", CalculatorWebApplication::handleRequest);

        server.start();

        System.out.println("Calculator web application started on port " + port);
    }

    private static void handleRequest(HttpExchange exchange)
            throws IOException {

        String path = exchange.getRequestURI().getPath();

        if (path.equals("/")) {
            sendResponse(exchange, calculatorPage("", "", "", ""));
            return;
        }

        if (path.equals("/calculate")) {

            String query = exchange.getRequestURI().getQuery();

            String a = getParameter(query, "a");
            String b = getParameter(query, "b");
            String operation = getParameter(query, "operation");

            String result;

            try {

                double number1 = Double.parseDouble(a);
                double number2 = Double.parseDouble(b);

                switch (operation) {

                    case "+":
                        result = format(number1 + number2);
                        break;

                    case "-":
                        result = format(number1 - number2);
                        break;

                    case "*":
                        result = format(number1 * number2);
                        break;

                    case "/":
                        if (number2 == 0) {
                            result = "Cannot divide by zero";
                        } else {
                            result = format(number1 / number2);
                        }
                        break;

                    case "%":
                        result = format(number1 / 100);
                        break;

                    default:
                        result = "Invalid operation";
                }

            } catch (Exception e) {
                result = "Please enter valid numbers";
            }

            sendResponse(
                    exchange,
                    calculatorPage(a, b, operation, result)
            );

            return;
        }

        sendResponse(exchange, "Page not found");
    }

    private static String getParameter(String query, String name) {

        if (query == null) {
            return "";
        }

        for (String parameter : query.split("&")) {

            String[] parts = parameter.split("=", 2);

            if (parts.length == 2 && parts[0].equals(name)) {

                return URLDecoder.decode(
                        parts[1],
                        StandardCharsets.UTF_8
                );
            }
        }

        return "";
    }

    private static String format(double number) {

        if (number == (long) number) {
            return String.valueOf((long) number);
        }

        return String.valueOf(number);
    }

    private static void sendResponse(
            HttpExchange exchange,
            String response) throws IOException {

        byte[] data = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/html; charset=UTF-8"
        );

        exchange.sendResponseHeaders(200, data.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(data);
        }
    }

    private static String calculatorPage(
            String a,
            String b,
            String operation,
            String result) {

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Simple Java Calculator</title>

                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background: #1e1e2e;
                            color: white;
                            text-align: center;
                            padding-top: 60px;
                        }

                        .calculator {
                            width: 350px;
                            margin: auto;
                            padding: 25px;
                            background: #303044;
                            border-radius: 15px;
                        }

                        input, select, button {
                            padding: 12px;
                            margin: 8px;
                            border-radius: 8px;
                            border: none;
                            font-size: 16px;
                        }

                        button {
                            cursor: pointer;
                            background: #4caf50;
                            color: white;
                        }

                        .result {
                            margin-top: 20px;
                            font-size: 22px;
                            font-weight: bold;
                        }
                    </style>
                </head>

                <body>

                    <div class="calculator">

                        <h1>Java Calculator</h1>

                        <form action="/calculate" method="get">

                            <input
                                type="number"
                                step="any"
                                name="a"
                                placeholder="First number"
                                value="%s"
                                required
                            >

                            <br>

                            <select name="operation">

                                <option value="+">+</option>
                                <option value="-">−</option>
                                <option value="*">×</option>
                                <option value="/">÷</option>
                                <option value="%%">%%</option>

                            </select>

                            <br>

                            <input
                                type="number"
                                step="any"
                                name="b"
                                placeholder="Second number"
                                value="%s"
                            >

                            <br>

                            <button type="submit">
                                Calculate
                            </button>

                        </form>

                        <div class="result">
                            Result: %s
                        </div>

                    </div>

                </body>
                </html>
                """.formatted(a, b, result);
    }
}
