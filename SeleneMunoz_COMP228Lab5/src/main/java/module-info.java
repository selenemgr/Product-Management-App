module exercise1.selenemunoz_comp228lab5 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens exercise1.selenemunoz_comp228lab5 to javafx.fxml;
    exports exercise1.selenemunoz_comp228lab5;
}