module hu.unideb.interscope {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    requires static lombok;


    opens hu.unideb.interscope to javafx.fxml;
    exports hu.unideb.interscope;
    exports hu.unideb.interscope.controller;
    opens hu.unideb.interscope.controller to javafx.fxml;
}