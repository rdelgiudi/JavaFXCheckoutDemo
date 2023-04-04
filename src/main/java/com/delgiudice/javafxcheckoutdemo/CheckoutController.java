package com.delgiudice.javafxcheckoutdemo;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

// Klasa sterująca głównym ekranem aplikacji, wyświetla koszyk produktów oraz pozwala na ich dodawanie
public class CheckoutController {
    @FXML
    private ListView<String> itemField;
    @FXML
    private TextField weightField, codeField, cashPaymentField;
    @FXML
    private Label totalLabel, productError, productSuccess, weightSuggestionLabel, cashPaymentTotalLabel,
            cashPaymentPaidLabel, cardPaymentTotalLabel, cashPaymentCompleteLabel, cardPaymentPaidLabel,
            cardPaymentCompleteLabel;
    @FXML
    private Button productListButton, addProductButton, selectProductButton, payButton, cancelPaymentButton,
            addCashFundsButton, useCardButton;
    @FXML
    private VBox productInput, paymentBox, cashPaymentBox, cardPaymentBox;

    private final List<String> itemString;

    private Stage stage;
    private Scene loaderScene;
    private Parent root;

    private final ProductLoader loader;
    private final ProductCart cart;
    private long amountPaid;

    public CheckoutController() throws IOException {
        loader = new ProductLoader("products.txt");
        cart = new ProductCart(loader);
        itemString = new ArrayList<>();
        itemField = new ListView<>();

        amountPaid = 0;
    }
    @FXML
    private void initialize() {
        setupIntegerField(weightField, 7);
        setupIntegerField(codeField, 5);
        setupFloatField(cashPaymentField);
    }

    // Metoda resetująca stan aplikacji do stanu początkowego
    private void reset() {
        cart.clear();
        itemString.clear();
        itemField.setItems(FXCollections.observableArrayList(itemString));

        amountPaid = 0;
        codeField.clear();
        weightField.clear();

        cashPaymentBox.setVisible(false);
        cashPaymentField.setDisable(false);
        addCashFundsButton.setDisable(false);

        cardPaymentBox.setVisible(false);
        useCardButton.setDisable(false);

        totalLabel.setText("Razem:      0.00");

        cashPaymentPaidLabel.setText("Zapłacono: 0.00");
        cashPaymentCompleteLabel.setText("Dziękujemy za zakupy!");

        cardPaymentPaidLabel.setText("Zapłacono: 0.00");

        productInput.setVisible(true);
        payButton.setVisible(true);
        payButton.setDisable(true);
    }

    // Metoda zmieniąjca scenę na listę produktów
    @FXML
    public void openProductList(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("fxml/productlist-view.fxml")));
        loaderScene = new Scene(fxmlLoader.load());
        stage = (Stage) productListButton.getScene().getWindow();

        // Przesłanie kontrollerowi niezbędnych danych oraz inicjalizacja
        ProductListController controller = fxmlLoader.getController();
        controller.setPreviousScene(productListButton.getScene());
        controller.loadProductList(loader.getProductList());
        controller.setCodeField(codeField);
        controller.setSuccessLabel(productSuccess);
        controller.populateProductGrid(ProductType.ANY);

        stage.setScene(loaderScene);
        stage.show();
    }

    //https://stackoverflow.com/questions/22714268/how-to-limit-the-amount-of-characters-a-javafx-textfield
    //https://stackoverflow.com/questions/40472668/numeric-textfield-for-integers-in-javafx-8-with-textformatter-and-or-unaryoperat
    // Metoda ustawiająca TextFormatter do wskazanego TextField, nastawia on limit wpisanych znaków oraz ogranicza znaki
    // wyłącznie do cyfr
    private void setupIntegerField(TextField textField, int characterLimit) {

        Pattern pattern = Pattern.compile(String.format(".{0,%d}", characterLimit));

        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String input = change.getText();
            if (input.matches("[0-9]*")) {
                return pattern.matcher(change.getControlNewText()).matches() ? change : null;
            }
            return null;
        };

        textField.setTextFormatter(new TextFormatter<String>(integerFilter));
    }

    // Metoda podobno do setupIntegerField, ale zezwalająca na liczby zmiennoprzecinkowe
    private void setupFloatField(TextField textField) {
        UnaryOperator<TextFormatter.Change> floatFilter = change -> {
            String input = change.getControlNewText();
            if (input.matches("-?\\d*(\\.\\d{0,2})?")) {
                return change;
            }
            return null;
        };
        textField.setTextFormatter(new TextFormatter<String>(floatFilter));
    }

    // Metoda generująca String dodawany do wyświetlaniej listy zakupów w ListView
    private String generateProductString(ProductTemplate product) {
        String productString;

        productString = String.format("%-40s                       = %9.2f",
                    product.getProductName().trim(),
                    product.getDisplayProductPrice());

        if (product.isPriceByWeight())
            productString += "/kg";
        //System.out.println(productString);
        return productString;
    }

    // Metoda generująca pełny String (tzn. wraz z masą produktu oraz obliczoną cenę, dotyczy tylko produktów na wagę)
    // dodawana do wyświetlanej listy zakupów w ListView
    private String generateFullProductString(Product product, int weight) {
        String productString;

        float weightGrams = (float)weight / 1000.0f;

        productString = String.format("%-40s %9.3f x %-9.2f = %9.2f",
                product.getProductTemplate().getProductName().trim(), weightGrams,
                product.getProductTemplate().getDisplayProductPrice(),
                product.getDisplayMeasuredPrice());

        //System.out.println(productString);
        return productString;
    }

    // Przełącza tryb select, czyli moment w aplikacji, w którym został wprowadzony produkt i aplikacja czeka na
    // wprowadzenie jego masy
    private void setSelectMode(boolean enable) {
        addProductButton.setDisable(!enable);
        selectProductButton.setDisable(enable);
        productListButton.setDisable(enable);
        codeField.setDisable(enable);

        //itemField.getSelectionModel().getSelectedIndex();
    }

    // Metoda wywoływana po naciśnieciu guzika "Wybierz" lub selectProductButton, dodaje produkt do ListView oraz
    // wyświetla dozwoloną masę produktu
    @FXML
    public void selectProduct() {
        ProductTemplate product = cart.getProductWithCode(codeField.getText());

        if (product != null) {
            //Product last = cart.getLast();
            String productString = generateProductString(product);

            itemString.add(productString);
            itemField.setItems(FXCollections.observableArrayList(itemString));
            setSelectMode(true);

            if (product.isPriceByWeight())
                weightSuggestionLabel.setText("Oczekiwana masa: dowolna");
            else
                weightSuggestionLabel.setText("Oczekiwana masa: " + product.getProductWeight());

            payButton.setDisable(true);
        }

        else {
            displayReturnMessage(productError);
        }
    }

    // Metoda wywoływana po naciśnięciu guzika "Do koszyka" lub addProductButton, dodaje zaaktualizowany String do
    // ListView (tylko produkty na wagę) oraz dodaje produkt do koszyka
    @FXML
    public void addProduct() {
        if (!Objects.equals(codeField.getText(), "") && !Objects.equals(weightField.getText(), "")) {

            int weight = Integer.parseInt(weightField.getText());

            boolean success = cart.addToCart(codeField.getText(), weight);

            // Jeśli dodanie do koszyka powiedzie się, aktualizowany jest ListView oraz licznik łącznej ceny za zakupy
            if (success && weight > 0) {
                Product last = cart.getLast();
                if (last.getProductTemplate().isPriceByWeight()) {
                     String productString = generateFullProductString(last, weight);
                     itemString.remove(itemString.size() - 1);
                     itemString.add(productString);
                     itemField.setItems(FXCollections.observableArrayList(itemString));
                }

                totalLabel.setText(String.format("Razem: %9.2f", cart.getDisplayTotalPrice()));
                payButton.setDisable(false);
            }
            else {
                displayReturnMessage(productError);
                itemString.remove(itemString.size() - 1);
                itemField.setItems(FXCollections.observableArrayList(itemString));
            }
        }

        else {
            displayReturnMessage(productError);
            itemString.remove(itemString.size() - 1);
            itemField.setItems(FXCollections.observableArrayList(itemString));
        }

        setSelectMode(false);
        weightSuggestionLabel.setText("Oczekiwana masa:");
    }

    // Metoda uruchamiana po rozpoczęciu płacenia za zakupy, ukrywa elementy interfejsu odpowiedzialne za dodawanie
    // zakupów i wyświetla interfejs płatności
    @FXML
    public void beginPayment() {
        // Funkcje obsługujące płatność, jeżeli byłaby to prawdziwa kasa

        productInput.setVisible(false);
        payButton.setVisible(false);
        paymentBox.setVisible(true);
        cancelPaymentButton.setVisible(true);

        cashPaymentTotalLabel.setText(String.format("Do zapłaty: %.2f", cart.getDisplayTotalPrice()));
        cardPaymentTotalLabel.setText(String.format("Do zapłaty: %.2f", cart.getDisplayTotalPrice()));
    }

    // Metoda symulująca płatność gotówką
    @FXML
    public void processCashPayment() {
        // Funkcje obsługujące płatność gotówką

        paymentBox.setVisible(false);
        cancelPaymentButton.setVisible(false);
        cashPaymentBox.setVisible(true);

    }

    // Metoda uruchamiana, gdy następuje wzrost wprowadzonych środków, jeżeli należność została opłacona, kończone jest
    // działanie aplikacji
    @FXML
    public void addCashFunds() {
        float cashAdded = Float.parseFloat(cashPaymentField.getText());
        long cashAddedLong = Math.round(cashAdded * 100);

        amountPaid += cashAddedLong;

        cashPaymentField.clear();

        cashPaymentPaidLabel.setText(String.format("Zapłacono: %.2f", amountPaid / 100.0f));

        if (amountPaid > cart.getTotalPrice()) {
            cashPaymentCompleteLabel.setText(String.format("Dziękujemy za zakupy! Reszta: %.2f",
                    ((amountPaid - cart.getTotalPrice())) / 100.0f));
        }
        if (amountPaid >= cart.getTotalPrice()) {
            cashPaymentField.setDisable(true);
            addCashFundsButton.setDisable(true);
            displayExitMessage(cashPaymentCompleteLabel);
            //+ drukowanie paragonu i inne operacje wykonywane przez prawdziwą kasę
        }

    }

    // Metoda symulująca płatność kartą
    @FXML
    public void processCardPayment() {
        cancelPaymentButton.setVisible(false);
        paymentBox.setVisible(false);

        cardPaymentBox.setVisible(true);
        //+obsługa komunikacji z terminalem płatniczym
    }

    // Metoda symulująca przesłanie środków kartą i działania po otrzymaniu należności
    @FXML
    public void addCardFunds() {
        //karta pomyślnie obciążona żądaną sumą
        useCardButton.setDisable(true);
        amountPaid = cart.getTotalPrice();
        cardPaymentPaidLabel.setText(String.format("Zapłacono: %.2f", amountPaid / 100.0f));
        displayExitMessage(cardPaymentCompleteLabel);
    }

    // Metoda przypisana do guzika anulowania płatności, w przypadku rozmyślenia się użytkownika i wyrażenia chęci
    // kontynuuowania zakupów
    @FXML
    public void cancelPayment() {
        paymentBox.setVisible(false);
        cancelPaymentButton.setVisible(false);
        payButton.setVisible(true);
        productInput.setVisible(true);
    }

    // Metoda odkrywająca daną wiadomość i ukrywająca ją po 4 sekundach
    public static void displayReturnMessage(Label label) {
        label.setVisible(true);
        final KeyFrame kf1 = new KeyFrame(Duration.seconds(4), e -> label.setVisible(false));
        final Timeline timeline = new Timeline(kf1);
        timeline.play();
    }

    // Metoda odkrywająca daną wiadomość, ukrywająca ją po 4 sekundach i resetująca stan programu do początkowego
    public void displayExitMessage(Label label) {
        label.setVisible(true);
        final KeyFrame kf1 = new KeyFrame(Duration.seconds(4), e -> label.setVisible(false));
        final KeyFrame kf2 = new KeyFrame(Duration.seconds(5), e -> {reset();});
        final Timeline timeline = new Timeline(kf1, kf2);
        timeline.play();
    }
}