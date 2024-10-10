package application;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BingoGame extends Application {

    private static final int BOARD_SIZE = 5;
    private final int[][] bingoBoard = new int[BOARD_SIZE][BOARD_SIZE];
    private final boolean[][] marked = new boolean[BOARD_SIZE][BOARD_SIZE]; // To track marked cells
    private final List<Integer> calledNumbers = new ArrayList<>();
    private final List<Button> boardButtons = new ArrayList<>(); // To store board buttons
    private Text calledNumberText;

    @Override
    public void start(Stage primaryStage) {
        // Create and shuffle Bingo numbers (1-75)
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 75; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);

        // Initialize the Bingo board with random numbers
        initializeBingoBoard(numbers);

        // Create the GUI components
        GridPane gridPane = createBingoBoardUI();
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        
        calledNumberText = new Text("Next Number: Click 'Draw Number' or Press Enter");
        calledNumberText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        calledNumberText.setId("called-number"); // Setting the ID for CSS

        Button drawButton = new Button("Draw Number");
        drawButton.setOnAction(e -> drawNumber());
        drawButton.getStyleClass().add("control-button");

        Button restartButton = new Button("Restart Game");
        restartButton.setOnAction(e -> restartGame());
        restartButton.getStyleClass().add("control-button");

        HBox buttonBox = new HBox(20, drawButton, restartButton);
        buttonBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(calledNumberText, gridPane, buttonBox);

        Scene scene = new Scene(root, 600, 700);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm()); // Add CSS

        // Handle Enter key press for drawing number
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                drawNumber();
            }
        });

        primaryStage.setTitle("Bingo Game with CSS");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Initializes the Bingo board with random numbers
    private void initializeBingoBoard(List<Integer> numbers) {
        int index = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (i == 2 && j == 2) {
                    bingoBoard[i][j] = 0; // Free space
                    marked[i][j] = true;  // Marked by default
                } else {
                    bingoBoard[i][j] = numbers.get(index++);
                    marked[i][j] = false; // Initially not marked
                }
            }
        }
    }

    // Creates the Bingo board UI and sets up click handlers for each cell
    private GridPane createBingoBoardUI() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Button button = new Button();
                button.setPrefSize(100, 100);
                int value = bingoBoard[i][j];
                if (value == 0) {
                    button.setText("Free"); // Free space
                    button.getStyleClass().add("free-space");
                } else {
                    button.setText(String.valueOf(value));
                    button.getStyleClass().add("bingo-button");
                }

                int row = i;
                int col = j;
                button.setOnAction(e -> markCell(button, row, col));
                boardButtons.add(button);
                gridPane.add(button, j, i);
            }
        }

        return gridPane;
    }

    // Draws a new number and displays it
    private void drawNumber() {
        Random rand = new Random();
        int newNumber;
        do {
            newNumber = rand.nextInt(75) + 1;
        } while (calledNumbers.contains(newNumber)); // Ensure no repeated numbers
        calledNumbers.add(newNumber);

        calledNumberText.setText("Next Number: " + newNumber);
    }

    // Marks a cell if the number matches the called number
    private void markCell(Button button, int row, int col) {
        if (marked[row][col]) {
            return; // Cell is already marked
        }

        int cellValue = bingoBoard[row][col];
        String calledNumberText = this.calledNumberText.getText();
        if (calledNumberText.contains(String.valueOf(cellValue)) || cellValue == 0) {
            button.getStyleClass().add("marked-cell"); // Use CSS class for marked cell
            marked[row][col] = true;
        }

        if (checkWin()) {
            showAlert("Bingo!", "You have a winning pattern!");
        }
    }

    // Checks if the player has a winning Bingo pattern
    private boolean checkWin() {
        // Check rows, columns, and diagonals for a winning pattern
        return checkRows() || checkColumns() || checkDiagonals();
    }

    private boolean checkRows() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            boolean rowComplete = true;
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (!marked[i][j]) {
                    rowComplete = false;
                    break;
                }
            }
            if (rowComplete) return true;
        }
        return false;
    }

    private boolean checkColumns() {
        for (int j = 0; j < BOARD_SIZE; j++) {
            boolean colComplete = true;
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (!marked[i][j]) {
                    colComplete = false;
                    break;
                }
            }
            if (colComplete) return true;
        }
        return false;
    }

    private boolean checkDiagonals() {
        boolean mainDiagonalComplete = true;
        boolean secondaryDiagonalComplete = true;
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (!marked[i][i]) {
                mainDiagonalComplete = false;
            }
            if (!marked[i][BOARD_SIZE - i - 1]) {
                secondaryDiagonalComplete = false;
            }
        }
        return mainDiagonalComplete || secondaryDiagonalComplete;
    }

    // Displays a message when a player wins
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Resets the game state for a new round
    private void restartGame() {
        calledNumbers.clear();
        initializeBingoBoard(new ArrayList<>(calledNumbers));
        boardButtons.forEach(button -> {
            button.getStyleClass().remove("marked-cell");
            button.getStyleClass().remove("bingo-button");
            button.getStyleClass().remove("free-space");
            button.getStyleClass().add("bingo-button");
            if (button.getText().equals("Free")) {
                button.getStyleClass().add("free-space");
            }
        });
        calledNumberText.setText("Next Number: Click 'Draw Number' or Press Enter");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
