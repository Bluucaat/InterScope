package hu.unideb.interscope.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Button;

public class TheHarvesterSettingsController {
    
    @FXML
    private ComboBox<String> dataSourceComboBox;
    
    @FXML
    private TextField paginationTextField;
    
    @FXML
    private ComboBox<String> limitComboBox;
    
    @FXML
    private ToggleGroup verboseToggleGroup;
    
    @FXML
    private Button cancelButton;
    
    @FXML
    private Button saveButton;
    
    @FXML
    public void initialize() {
        // Define the data sources
        ObservableList<String> dataSources = FXCollections.observableArrayList(
            "all", "anubis", "baidu", "bing", "crtsh", "dnsdumpster", 
            "duckduckgo", "github", "google", "hunter", "intelx", 
            "linkedin", "otx", "securityTrails", "threatminer", "yahoo"
        );
        
        // Set the items to the ComboBox
        dataSourceComboBox.setItems(dataSources);
        
        // Set a default value
        dataSourceComboBox.setValue("all");
        
        // Set a default value for pagination offset
        paginationTextField.setText("0");
        
        // Add validation to pagination offset (only numbers allowed)
        paginationTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                paginationTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        
        // Define limit values (already done in FXML, but you can still do it here if preferred)
        // ObservableList<String> limitValues = FXCollections.observableArrayList(
        //     "10", "50", "100", "200", "500"
        // );
        // limitComboBox.setItems(limitValues);
        
        // Set a default value for limit
        limitComboBox.setValue("100");
        
        // Setup button actions
        cancelButton.setOnAction(event -> closeSettings());
        saveButton.setOnAction(event -> saveSettings());
    }
    
    private void closeSettings() {
        // Logic to close the settings panel
        // For example: ((GridPane) cancelButton.getScene().getRoot()).setTranslateY(850);
    }
    
    private void saveSettings() {
        // Get values from all fields
        String dataSource = dataSourceComboBox.getValue();
        String paginationOffset = paginationTextField.getText();
        String limit = limitComboBox.getValue();
        boolean verbose = ((RadioButton)verboseToggleGroup.getSelectedToggle()).getText().equals("ON");
        
        // Process and save the settings
        System.out.println("Settings saved: " + dataSource + ", " + paginationOffset + ", " + limit + ", " + verbose);
        
        // Close settings after saving
        closeSettings();
    }
    
    // ...rest of your controller code...
} 