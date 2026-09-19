package webcalculator;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CalculatorWebApplication {

    private static final List<String> history = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        int port = Integer.parseInt(
                System.getenv().getOrDefault("PORT", "8080")
        );

        HttpServer server = HttpServer.create(
                new InetSocketAddress("0.0.0.0", port),
                0
        );

        server.createContext(
                "/",
                CalculatorWebApplication::handleRequest
        );

        server.start();

        System.out.println(
                "Calculator web application started on port " + port
        );
    }

    private static void handleRequest(HttpExchange exchange)
            throws IOException {

        String query = exchange.getRequestURI().getQuery();

        String action = getParameter(query, "action");

        String display = "0";
        String expression = "";

        if ("calculate".equals(action)) {

            String first = getParameter(query, "first");
            String operator = getParameter(query, "operator");
            String second = getParameter(query, "second");

            try {

                double firstNumber =
                        Double.parseDouble(first);

                double secondNumber =
                        Double.parseDouble(second);

                double result = 0;

                expression =
                        formatNumber(firstNumber)
                        + " "
                        + operator
                        + " "
                        + formatNumber(secondNumber);

                switch (operator) {

                    case "+":

                        result = firstNumber + secondNumber;

                        break;

                    case "−":

                        result = firstNumber - secondNumber;

                        break;

                    case "×":

                        result = firstNumber * secondNumber;

                        break;

                    case "÷":

                        if (secondNumber == 0) {

                            display = "Error";

                            history.add(
                                    0,
                                    expression + " = Error"
                            );

                            break;
                        }

                        result = firstNumber / secondNumber;

                        break;

                    default:

                        display = "Error";

                        break;
                }

                if (!display.equals("Error")) {

                    display = formatNumber(result);

                    history.add(
                            0,
                            expression + " = " + display
                    );
                }

            } catch (Exception e) {

                display = "Error";
            }
        }

        String response =
                calculatorPage(expression, display);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/html; charset=UTF-8"
        );

        byte[] responseBytes =
                response.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(
                200,
                responseBytes.length
        );

        try (OutputStream output =
                     exchange.getResponseBody()) {

            output.write(responseBytes);
        }
    }

    private static String getParameter(
            String query,
            String name) {

        if (query == null) {
            return "";
        }

        for (String parameter : query.split("&")) {

            String[] parts =
                    parameter.split("=", 2);

            if (parts.length == 2 &&
                    parts[0].equals(name)) {

                String value =
                        URLDecoder.decode(
                                parts[1],
                                StandardCharsets.UTF_8
                        );

                if (name.equals("operator") &&
                        value.equals(" ")) {

                    return "+";
                }

                return value;
            }
        }

        return "";
    }

    private static String formatNumber(
            double number) {

        if (number == (long) number) {

            return String.valueOf(
                    (long) number
            );

        } else {

            return String.valueOf(number);
        }
    }

    private static String escapeHTML(
            String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private static String calculatorPage(
            String expression,
            String display) {

        String safeExpression =
                escapeHTML(expression);

        String safeDisplay =
                escapeHTML(display);

        StringBuilder historyHTML =
                new StringBuilder();

        for (String item : history) {

            historyHTML.append(
                    "<div class=\"history-item\">"
            );

            historyHTML.append(
                    escapeHTML(item)
            );

            historyHTML.append(
                    "</div>"
            );
        }

        return """
                <!DOCTYPE html>
                <html>

                <head>

                    <meta charset="UTF-8">

                    <meta name="viewport"
                          content="width=device-width, initial-scale=1.0">

                    <title>Simple Calculator</title>

                    <style>

                        * {
                            box-sizing: border-box;
                        }

                        body {

                            margin: 0;
                            padding: 0;

                            font-family: Arial, sans-serif;

                            background-color: #121212;

                            color: white;
                        }

                        .calculator {

                            width: 380px;

                            margin: 30px auto;

                            padding: 20px;

                            background-color: #121212;

                            border-radius: 12px;
                        }

                        .expression-display {

                            width: 100%%;

                            height: 45px;

                            background-color: #121212;

                            color: #aaaaaa;

                            border: none;

                            outline: none;

                            text-align: right;

                            font-size: 18px;

                            padding: 5px;
                        }

                        .main-display {

                            width: 100%%;

                            height: 60px;

                            background-color: #202124;

                            color: white;

                            border: 1px solid #555555;

                            border-radius: 8px;

                            outline: none;

                            text-align: right;

                            font-size: 30px;

                            padding: 10px;

                            margin-bottom: 15px;
                        }

                        .history-title {

                            font-size: 16px;

                            color: white;

                            margin-bottom: 8px;
                        }

                        .history {

                            height: 120px;

                            overflow-y: auto;

                            background-color: #202124;

                            border-radius: 8px;

                            margin-bottom: 15px;

                            padding: 5px;
                        }

                        .history-item {

                            color: white;

                            padding: 7px;

                            border-bottom: 1px solid #444444;

                            text-align: right;

                            font-size: 14px;
                        }

                        .buttons {

                            display: grid;

                            grid-template-columns:
                                repeat(4, 1fr);

                            gap: 8px;
                        }

                        button {

                            height: 60px;

                            border: none;

                            border-radius: 10px;

                            background-color: #303134;

                            color: white;

                            font-size: 20px;

                            cursor: pointer;
                        }

                        button:hover {

                            opacity: 0.85;
                        }

                        .clear {

                            background-color: #d93025;
                        }

                        .equals {

                            background-color: #1a73e8;
                        }

                        .operator {

                            background-color: #f9ab00;

                            color: black;
                        }

                        .zero {

                            grid-column: span 2;
                        }

                    </style>

                </head>

                <body>

                    <div class="calculator">

                        <input
                            class="expression-display"
                            type="text"
                            value="%s"
                            readonly
                        >

                        <input
                            class="main-display"
                            id="display"
                            type="text"
                            value="%s"
                            readonly
                        >

                        <div class="history-title">

                            Calculation History

                        </div>

                        <div class="history">

                            %s

                        </div>

                        <div class="buttons">

                            <button
                                class="clear"
                                onclick="clearDisplay()">

                                C

                            </button>

                            <button
                                onclick="deleteLast()">

                                ⌫

                            </button>

                            <button
                                class="operator"
                                onclick="percentage()">

                                %%

                            </button>

                            <button
                                class="operator"
                                onclick="appendOperator('÷')">

                                ÷

                            </button>

                            <button
                                onclick="appendNumber('7')">

                                7

                            </button>

                            <button
                                onclick="appendNumber('8')">

                                8

                            </button>

                            <button
                                onclick="appendNumber('9')">

                                9

                            </button>

                            <button
                                class="operator"
                                onclick="appendOperator('×')">

                                ×

                            </button>

                            <button
                                onclick="appendNumber('4')">

                                4

                            </button>

                            <button
                                onclick="appendNumber('5')">

                                5

                            </button>

                            <button
                                onclick="appendNumber('6')">

                                6

                            </button>

                            <button
                                class="operator"
                                onclick="appendOperator('−')">

                                −

                            </button>

                            <button
                                onclick="appendNumber('1')">

                                1

                            </button>

                            <button
                                onclick="appendNumber('2')">

                                2

                            </button>

                            <button
                                onclick="appendNumber('3')">

                                3

                            </button>

                            <button
                                class="operator"
                                onclick="appendOperator('+')">

                                +

                            </button>

                            <button
                                class="zero"
                                onclick="appendNumber('0')">

                                0

                            </button>

                            <button
                                onclick="appendNumber('.')">

                                .

                            </button>

                            <button
                                class="equals"
                                onclick="calculate()">

                                =

                            </button>

                        </div>

                    </div>

                    <script>

                        let expression = "";

                        function updateDisplay() {

                            document.getElementById(
                                "display"
                            ).value = expression;
                        }

                        function appendNumber(number) {

                            if (
                                expression === "Error" ||
                                expression === "0"
                            ) {

                                expression = "";
                            }

                            expression += number;

                            updateDisplay();
                        }

                        function appendOperator(operator) {

                            if (
                                expression === "" ||
                                expression === "Error"
                            ) {

                                return;
                            }

                            let parts =
                                expression.trim().split(" ");

                            if (parts.length >= 3) {

                                return;
                            }

                            expression =
                                expression.trim()
                                + " "
                                + operator
                                + " ";

                            updateDisplay();
                        }

                        function clearDisplay() {

                            expression = "";

                            window.location.href = "/";
                        }

                        function deleteLast() {

                            if (
                                expression === "" ||
                                expression === "Error"
                            ) {

                                return;
                            }

                            expression =
                                expression.trimEnd();

                            expression =
                                expression.slice(0, -1);

                            expression =
                                expression.trimEnd();

                            updateDisplay();
                        }

                        function percentage() {

                            if (
                                expression === "" ||
                                expression === "Error"
                            ) {

                                return;
                            }

                            let parts =
                                expression.trim().split(" ");

                            let number;

                            if (parts.length === 1) {

                                number =
                                    parseFloat(parts[0]);

                            } else if (parts.length === 3) {

                                number =
                                    parseFloat(parts[2]);

                            } else {

                                return;
                            }

                            number = number / 100;

                            if (parts.length === 1) {

                                expression =
                                    number.toString();

                            } else {

                                expression =
                                    parts[0]
                                    + " "
                                    + parts[1]
                                    + " "
                                    + number.toString();
                            }

                            updateDisplay();
                        }

                        function calculate() {

                            if (
                                expression === "" ||
                                expression === "Error"
                            ) {

                                return;
                            }

                            let parts =
                                expression.trim().split(" ");

                            if (parts.length !== 3) {

                                document.getElementById(
                                    "display"
                                ).value = "Error";

                                return;
                            }

                            window.location.href =
                                "/?action=calculate"
                                + "&first="
                                + encodeURIComponent(parts[0])
                                + "&operator="
                                + encodeURIComponent(parts[1])
                                + "&second="
                                + encodeURIComponent(parts[2]);
                        }

                    </script>

                </body>

                </html>
                """.formatted(
                safeExpression,
                safeDisplay,
                historyHTML.toString()
        );
    }
}
