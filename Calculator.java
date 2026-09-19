package SE;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Calculato extends Application {

    private TextField display;
    private TextField expressionDisplay;
    private ListView<String> historyList;

    private double firstNumber = 0;
    private String operator = "";
    private boolean newNumber = true;

    @Override
    public void start(Stage stage) {

        // Expression display
        expressionDisplay = new TextField("");
        expressionDisplay.setEditable(false);
        expressionDisplay.setAlignment(Pos.CENTER_RIGHT);
        expressionDisplay.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-background-color: #121212;" +
                "-fx-text-fill: #aaaaaa;" +
                "-fx-border-color: transparent;"
        );

        // Main display
        display = new TextField("0");
        display.setEditable(false);
        display.setAlignment(Pos.CENTER_RIGHT);
        display.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-background-color: #202124;" +
                "-fx-text-fill: white;" +
                "-fx-border-color: #555555;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        // =========================
        // CALCULATION HISTORY
        // =========================

        Label historyLabel = new Label("Calculation History");

        historyLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-text-fill: white;"
        );

        historyList = new ListView<>();

        historyList.setPrefHeight(120);

        historyList.setStyle(
                "-fx-background-color: #202124;" +
                "-fx-control-inner-background: #202124;" +
                "-fx-text-fill: white;"
        );

        // =========================
        // GRID FOR BUTTONS
        // =========================

        GridPane grid = new GridPane();

        grid.setHgap(8);
        grid.setVgap(8);
        grid.setAlignment(Pos.CENTER);

        // Button names
        String[] buttons = {
                "C", "⌫", "%", "÷",
                "7", "8", "9", "×",
                "4", "5", "6", "−",
                "1", "2", "3", "+",
                "0", ".", "="
        };

        int index = 0;

        for (String text : buttons) {

            Button button = new Button(text);

            button.setPrefSize(75, 60);

            // Normal button style
            button.setStyle(
                    "-fx-font-size: 20px;" +
                    "-fx-background-color: #303134;" +
                    "-fx-text-fill: white;" +
                    "-fx-background-radius: 10;"
            );

            // Clear button
            if (text.equals("C")) {

                button.setStyle(
                        "-fx-font-size: 20px;" +
                        "-fx-background-color: #d93025;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 10;"
                );
            }

            // Equals button
            if (text.equals("=")) {

                button.setStyle(
                        "-fx-font-size: 20px;" +
                        "-fx-background-color: #1a73e8;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 10;"
                );
            }

            // Operator buttons
            if (text.equals("+") ||
                text.equals("−") ||
                text.equals("×") ||
                text.equals("÷") ||
                text.equals("%")) {

                button.setStyle(
                        "-fx-font-size: 20px;" +
                        "-fx-background-color: #f9ab00;" +
                        "-fx-text-fill: black;" +
                        "-fx-background-radius: 10;"
                );
            }

            // Button action
            button.setOnAction(e -> handleButton(text));

            // Add buttons to grid
            if (text.equals("0")) {

                grid.add(button, 0, 4, 2, 1);

            } else if (text.equals(".")) {

                grid.add(button, 2, 4);

            } else if (text.equals("=")) {

                grid.add(button, 3, 4);

            } else {

                int row = index / 4;
                int column = index % 4;

                grid.add(button, column, row);
            }

            index++;
        }

        // =========================
        // MAIN LAYOUT
        // =========================

        VBox root = new VBox(12);

        root.setPadding(new Insets(20));

        root.setAlignment(Pos.CENTER);

        root.setStyle(
                "-fx-background-color: #121212;"
        );

        // Add everything
        root.getChildren().addAll(
                expressionDisplay,
                display,
                historyLabel,
                historyList,
                grid
        );

        // =========================
        // SCENE
        // =========================

        Scene scene = new Scene(
                root,
                380,
                650
        );

        stage.setTitle("Simple Calculator");

        stage.setScene(scene);

        stage.setResizable(false);

        stage.show();
    }

    private void handleButton(String value) {

        // =========================
        // CLEAR
        // =========================

        if (value.equals("C")) {

            display.setText("0");

            expressionDisplay.setText("");

            firstNumber = 0;

            operator = "";

            newNumber = true;

            return;
        }

        // =========================
        // DELETE
        // =========================

        if (value.equals("⌫")) {

            String current = display.getText();

            if (current.length() > 1) {

                display.setText(
                        current.substring(
                                0,
                                current.length() - 1
                        )
                );

            } else {

                display.setText("0");
            }

            // Update expression
            if (!operator.equals("")) {

                expressionDisplay.setText(
                        formatNumber(firstNumber)
                        + " "
                        + operator
                        + " "
                        + display.getText()
                );
            }

            return;
        }

        // =========================
        // NUMBERS
        // =========================

        if (value.matches("[0-9]")) {

            if (newNumber ||
                display.getText().equals("0")) {

                display.setText(value);

                newNumber = false;

            } else {

                display.setText(
                        display.getText() + value
                );
            }

            // Show complete expression
            if (!operator.equals("")) {

                expressionDisplay.setText(
                        formatNumber(firstNumber)
                        + " "
                        + operator
                        + " "
                        + display.getText()
                );
            }

            return;
        }

        // =========================
        // DECIMAL
        // =========================

        if (value.equals(".")) {

            if (newNumber) {

                display.setText("0.");

                newNumber = false;

            } else if (!display.getText().contains(".")) {

                display.setText(
                        display.getText() + "."
                );
            }

            // Update expression
            if (!operator.equals("")) {

                expressionDisplay.setText(
                        formatNumber(firstNumber)
                        + " "
                        + operator
                        + " "
                        + display.getText()
                );
            }

            return;
        }

        // =========================
        // PERCENTAGE
        // =========================

        if (value.equals("%")) {

            double number = Double.parseDouble(
                    display.getText()
            );

            number = number / 100;

            display.setText(
                    formatNumber(number)
            );

            // Update expression
            if (!operator.equals("")) {

                expressionDisplay.setText(
                        formatNumber(firstNumber)
                        + " "
                        + operator
                        + " "
                        + formatNumber(number)
                );
            }

            return;
        }

        // =========================
        // OPERATORS
        // =========================

        if (value.equals("+") ||
            value.equals("−") ||
            value.equals("×") ||
            value.equals("÷")) {

            firstNumber = Double.parseDouble(
                    display.getText()
            );

            operator = value;

            expressionDisplay.setText(
                    formatNumber(firstNumber)
                    + " "
                    + operator
            );

            newNumber = true;

            return;
        }

        // =========================
        // EQUALS
        // =========================

        if (value.equals("=")) {

            // If no operator was selected
            if (operator.equals("")) {

                return;
            }

            double secondNumber =
                    Double.parseDouble(
                            display.getText()
                    );

            double result = 0;

            // Show complete expression
            String calculation =
                    formatNumber(firstNumber)
                    + " "
                    + operator
                    + " "
                    + formatNumber(secondNumber);

            expressionDisplay.setText(calculation);

            // Calculate
            switch (operator) {

                case "+":

                    result =
                            firstNumber + secondNumber;

                    break;

                case "−":

                    result =
                            firstNumber - secondNumber;

                    break;

                case "×":

                    result =
                            firstNumber * secondNumber;

                    break;

                case "÷":

                    if (secondNumber == 0) {

                        display.setText("Error");

                        expressionDisplay.setText(
                                calculation
                        );

                        // Add error to history
                        historyList.getItems().add(
                                0,
                                calculation + " = Error"
                        );

                        newNumber = true;

                        operator = "";

                        return;
                    }

                    result =
                            firstNumber / secondNumber;

                    break;

                default:

                    return;
            }

            // Display result
            display.setText(
                    formatNumber(result)
            );

            // =========================
            // ADD TO HISTORY
            // =========================

            historyList.getItems().add(
                    0,
                    calculation
                    + " = "
                    + formatNumber(result)
            );

            newNumber = true;

            operator = "";
        }
    }

    // =========================
    // FORMAT NUMBER
    // =========================

    private String formatNumber(double number) {

        if (number == (long) number) {

            return String.valueOf(
                    (long) number
            );

        } else {

            return String.valueOf(number);
        }
    }

    // =========================
    // MAIN
    // =========================

    public static void main(String[] args) {

        launch(args);
    }
}
