package fr.univartois.butinfo.ihm;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Random;

/**
 * The type Controller 2048.
 */
public class Controller2048 {
    private static final Random random = new Random();

    @FXML
    private GridPane gameGrid;

    private static final String HEXACOULEUR = "#f9f6f2";
    @FXML
    private Label scoreLabel;

    private Grid grid;

    private int score = 0;

    @FXML
    private TextField tailleGrille;

    @FXML
    private Button okTaille;

    private StackPane[][] tilePanes;

    /**
     * Initialize.
     */
    @FXML
    public void initialize() {
        // Initialisation du modèle
        grid = new Grid();
        tilePanes = new StackPane[Grid.SIZE][Grid.SIZE];

        // Création de la grille visuelle
        initializeGrid();

        // Ajout des premières tuiles
        addRandomTile();
        addRandomTile();

        // Gestion des événements clavier
        gameGrid.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPress);
            }
        });
    }

    private void initializeGrid() {
        for (int i = 0; i < Grid.SIZE; i++) {
            for (int j = 0; j < Grid.SIZE; j++) {
                StackPane tilePane = createTilePane();
                tilePanes[i][j] = tilePane;
                gameGrid.add(tilePane, j, i);
            }
        }
    }

    private StackPane createTilePane() {
        StackPane stack = new StackPane();
        Rectangle background = new Rectangle(80, 80);
        background.setFill(Color.rgb(238, 228, 218, 0.35));
        background.setArcHeight(10);
        background.setArcWidth(10);

        Label value = new Label("");
        value.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        value.setTextFill(Color.rgb(119, 110, 101));

        stack.getChildren().addAll(background, value);
        stack.setAlignment(Pos.CENTER);
        return stack;
    }

    private void updateDisplay() {
        for (int i = 0; i < Grid.SIZE; i++) {
            for (int j = 0; j < Grid.SIZE; j++) {
                Tile tile = grid.get(i, j);
                StackPane stack = tilePanes[i][j];
                Label label = (Label) stack.getChildren().get(1);
                Rectangle rect = (Rectangle) stack.getChildren().get(0);

                if (tile.isEmpty()) {
                    label.setText("");
                    rect.setFill(Color.rgb(238, 228, 218, 0.35));
                } else {
                    label.setText(String.valueOf(tile.getValue()));
                    updateTileStyle(tile.getValue(), label, rect);
                }
            }
        }
        scoreLabel.setText(String.valueOf(score));
    }

    private void updateTileStyle(int value, Label label, Rectangle rect) {
        switch (value) {
            case 2:    setTileStyle(label, rect, "#eee4da", "#776e65"); break;
            case 4:    setTileStyle(label, rect, "#ede0c8", "#776e65"); break;
            case 8:    setTileStyle(label, rect, "#f2b179", HEXACOULEUR); break;
            case 16:   setTileStyle(label, rect, "#f59563", HEXACOULEUR); break;
            case 32:   setTileStyle(label, rect, "#f67c5f", HEXACOULEUR); break;
            case 64:   setTileStyle(label, rect, "#f65e3b", HEXACOULEUR); break;
            case 128:  setTileStyle(label, rect, "#edcf72", HEXACOULEUR); break;
            case 256:  setTileStyle(label, rect, "#edcc61", HEXACOULEUR); break;
            case 512:  setTileStyle(label, rect, "#edc850", HEXACOULEUR); break;
            case 1024: setTileStyle(label, rect, "#edc53f", HEXACOULEUR); break;
            case 2048: setTileStyle(label, rect, "#edc22e", HEXACOULEUR); break;
            default:   setTileStyle(label, rect, "#3c3a32", HEXACOULEUR); break;
        }

        // Ajuster la taille de la police en fonction du nombre de chiffres
        if (value >= 1000) {
            label.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        } else if (value >= 100) {
            label.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        } else {
            label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        }
    }

    private void setTileStyle(Label label, Rectangle rect, String bgColor, String textColor) {
        rect.setFill(Color.web(bgColor));
        label.setTextFill(Color.web(textColor));
    }

    private void handleKeyPress(KeyEvent event) {
        MoveResult result = null;
        switch (event.getCode()) {
            case UP:    result = grid.moveUp(); break;
            case DOWN:  result = grid.moveDown(); break;
            case LEFT:  result = grid.moveLeft(); break;
            case RIGHT: result = grid.moveRight(); break;
            default: return;
        }



        if (result != null && result.hasMoved()) {
            // Mise à jour du score avec la valeur des tuiles fusionnée

            score += result.getMergeScore();
            // Forcer la mise à jour de l'affichage du score
            scoreLabel.setText(String.valueOf(score));

            addRandomTile();
            updateDisplay();

            if (grid.isBlocked()) {
                showGameOverDialog();
            }
        }
    }

    private void addRandomTile() {
        var availableTiles = grid.availableTiles();
        if (!availableTiles.isEmpty()) {
            int index = random.nextInt(availableTiles.size());
            Tile tile = availableTiles.get(index);
            tile.setValue(Math.random() < 0.9 ? 2 : 4);
        }
    }

    /**
     * Onreset button click.
     */
    @FXML
    protected void onResetButtonClick() {
        grid.clear();
        score = 0;
        addRandomTile();
        addRandomTile();
        updateDisplay();

    }

    private void showGameOverDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Partie terminée");
        alert.setHeaderText("Game Over!");
        alert.setContentText("Score final : " + score + "\nCliquez sur 'Nouvelle partie' pour recommencer.");
        alert.show();
    }

    @FXML
    private void setTailleGameGrid() {
        try {
            int nouvelleTaille = Integer.parseInt(tailleGrille.getText());
            if (nouvelleTaille < 3 || nouvelleTaille > 8) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Taille invalide");
                alert.setContentText("La taille de la grille doit être comprise entre 3 et 8");
                alert.show();
                return;
            }

            // Effacer la grille actuelle
            gameGrid.getChildren().clear();
            gameGrid.getColumnConstraints().clear();
            gameGrid.getRowConstraints().clear();

            // Mettre à jour les contraintes de colonnes
            for (int i = 0; i < nouvelleTaille; i++) {
                ColumnConstraints colConstraints = new ColumnConstraints();
                colConstraints.setHgrow(Priority.SOMETIMES);
                colConstraints.setMinWidth(90.0);
                colConstraints.setPrefWidth(90.0);
                gameGrid.getColumnConstraints().add(colConstraints);
            }

            // Mettre à jour les contraintes de lignes
            for (int i = 0; i < nouvelleTaille; i++) {
                RowConstraints rowConstraints = new RowConstraints();
                rowConstraints.setVgrow(Priority.SOMETIMES);
                rowConstraints.setMinHeight(90.0);
                rowConstraints.setPrefHeight(90.0);
                gameGrid.getRowConstraints().add(rowConstraints);
            }

            // Créer le nouveau tableau de StackPanes
            tilePanes = new StackPane[nouvelleTaille][nouvelleTaille];

            // Recréer la grille avec la nouvelle taille
            for (int i = 0; i < nouvelleTaille; i++) {
                for (int j = 0; j < nouvelleTaille; j++) {
                    StackPane tilePane = createTilePane();
                    tilePanes[i][j] = tilePane;
                    gameGrid.add(tilePane, j, i);
                }
            }

            // Réinitialiser le jeu avec la nouvelle taille
            grid = new Grid();
            score = 0;
            scoreLabel.setText("0");
            addRandomTile();
            addRandomTile();
            updateDisplay();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Format invalide");
            alert.setContentText("Veuillez entrer un nombre valide");
            alert.show();
        }
    }
}