module hu.unideb.interscope {
    requires javafx.controls;
    requires javafx.fxml;


    opens hu.unideb.interscope to javafx.fxml;
    exports hu.unideb.interscope;
}