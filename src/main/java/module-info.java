module com.delgiudice.javafxcheckoutdemo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.delgiudice.javafxcheckoutdemo to javafx.fxml;
    exports com.delgiudice.javafxcheckoutdemo;
}