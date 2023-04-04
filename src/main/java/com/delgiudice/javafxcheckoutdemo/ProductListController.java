package com.delgiudice.javafxcheckoutdemo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

    @FXML
    private Button allButton, breadButton, fruitButton, vegetableButton, otherFoodButton, otherButton, backButton;
    @FXML
    private GridPane productGrid;

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
    public void populateProductGrid(ProductType type) {
        int i = 0, j = 0;
        for (ProductTemplate product : productList) {
            if (product.getProductType() == type || type == ProductType.ANY) {
                addProductToGrid(product, i, j);
                i++;
                if (i > 6) {
                    i = 0;
                    j++;
                }
            }
        }
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
        populateProductGrid(ProductType.ANY);
        allButton.setDisable(true);
    }

    @FXML
    public void gridDisplayBread() {
        clearGrid();
        populateProductGrid(ProductType.BREAD);
        breadButton.setDisable(true);
    }

    @FXML
    public void gridDisplayFruit() {
        clearGrid();
        populateProductGrid(ProductType.FRUIT);
        fruitButton.setDisable(true);
    }

    @FXML
    public void gridDisplayVegetable() {
        clearGrid();
        populateProductGrid(ProductType.VEGETABLE);
        vegetableButton.setDisable(true);
    }

    @FXML
    public void gridDisplayOtherFood() {
        clearGrid();
        populateProductGrid(ProductType.GENERAL_FOOD);
        otherFoodButton.setDisable(true);
    }

    @FXML
    public void gridDisplayOther() {
        clearGrid();
        populateProductGrid(ProductType.OTHER);
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


        button.setPrefWidth(170);
        button.setMinWidth(170);
        button.setPrefHeight(90);
        button.setMinHeight(90);

        button.setOnAction(actionEvent -> {
            codeField.setText(product.getProductCode());
            CheckoutController.displayReturnMessage(successLabel);
            returnToMain(actionEvent);
        });

        productGrid.add(button, i, j);
    }

}
