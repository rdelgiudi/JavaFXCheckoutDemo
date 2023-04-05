package com.delgiudice.javafxcheckoutdemo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;

// Klasa sterująca listą produktów, która pozwala na znalezenie produktu bez znajomości jego kodu,
// zastępuje także skaner kodów kreskowych w prawdziwej kasie
public class ProductListController {

    private List<ProductTemplate> productList;
    private Scene previousScene;
    private TextField codeField;
    private Label successLabel;

    private ProductType currentType = ProductType.ANY;

    @FXML
    private Button allButton, breadButton, fruitButton, vegetableButton, otherFoodButton, otherButton, backButton;
    @FXML
    private GridPane productGrid;
    @FXML
    private ScrollPane scrollPane;

    public void loadProductList(List<ProductTemplate> productList) {
        this.productList = productList;
    }

    public void setCodeField(TextField field) {
        codeField = field;
    }

    public void setPreviousScene(Scene scene) {
        previousScene = scene;
    }

    public void setSuccessLabel(Label label) {
        successLabel = label;
    }

    public void initialize() {
        scrollPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            productGrid.getChildren().clear();
            populateProductGrid();
        });

        allButton.setDisable(true);
    }

    // Metoda aktywująca wszystkie guziki
    private void enableAllButtons() {
        allButton.setDisable(false);
        breadButton.setDisable(false);
        fruitButton.setDisable(false);
        vegetableButton.setDisable(false);
        otherFoodButton.setDisable(false);
        otherButton.setDisable(false);
    }

    // Metoda pozwalająca powrócić do głównego ekranu
    @FXML
    private void returnToMain(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(previousScene);
        stage.show();
    }

    // Metoda zapełniająca siatkę produktami, zależnie od sprecyzowanego typu
    public void populateProductGrid() {
        double space = scrollPane.getWidth();
        int maxI;
        if (space > 0) {
            maxI = (int) Math.floor((space - 20) / 200);
            productGrid.setPrefWidth((maxI * 200) + 10);
        }
        else
            maxI = 6;
        int i = 0, j = 0;
        for (ProductTemplate product : productList) {
            if (product.getProductType() == currentType || currentType == ProductType.ANY) {
                addProductToGrid(product, i, j);
                i++;
                if (i >= maxI) {
                    i = 0;
                    j++;
                }
            }
        }
        if (j == 0)
            productGrid.setPrefWidth(i * 180);
    }

    // Metoda czyszcząca siatkę
    public void clearGrid() {
        enableAllButtons();
        productGrid.getChildren().clear();
    }

    // Zestaw metod przypisanych do guzików kategorii, pozwalające na wyświetlenie przez użytkownika szukanej kategorii
    @FXML
    public void gridDisplayAll() {
        clearGrid();
        currentType = ProductType.ANY;
        populateProductGrid();
        allButton.setDisable(true);
    }

    @FXML
    public void gridDisplayBread() {
        clearGrid();
        currentType = ProductType.BREAD;
        populateProductGrid();
        breadButton.setDisable(true);
    }

    @FXML
    public void gridDisplayFruit() {
        clearGrid();
        currentType = ProductType.FRUIT;
        populateProductGrid();
        fruitButton.setDisable(true);
    }

    @FXML
    public void gridDisplayVegetable() {
        clearGrid();
        currentType = ProductType.VEGETABLE;
        populateProductGrid();
        vegetableButton.setDisable(true);
    }

    @FXML
    public void gridDisplayOtherFood() {
        clearGrid();
        currentType = ProductType.GENERAL_FOOD;
        populateProductGrid();
        otherFoodButton.setDisable(true);
    }

    @FXML
    public void gridDisplayOther() {
        clearGrid();
        currentType = ProductType.OTHER;
        populateProductGrid();
        otherButton.setDisable(true);
    }

    // Metoda dodająca guzik reprezentująca dany produkt
    private void addProductToGrid(ProductTemplate product, int i, int j) {
        Button button = new Button();

        button.setText(product.getProductName());
        button.setFont(Font.font("Verdana", 11));
        Image icon = new Image(product.getIconPath(), 30, 30, true,
               true, true);
        ImageView imageView = new ImageView(icon);
        button.setGraphic(imageView);
        button.setContentDisplay(ContentDisplay.TOP);
        button.setAlignment(Pos.CENTER);
        button.setStyle("-fx-background-color: #ADD8E6;");

        //button.setMaxWidth(170);
        //button.setMinWidth(170);
        //button.setMaxHeight(90);
        //button.setMinHeight(90);
        button.setPrefSize(190, 90);

        button.setOnAction(actionEvent -> {
            codeField.setText(product.getProductCode());
            CheckoutController.displayReturnMessage(successLabel);
            returnToMain(actionEvent);
        });

        productGrid.add(button, i, j);
    }

}
