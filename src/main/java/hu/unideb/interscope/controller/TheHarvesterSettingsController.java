package hu.unideb.interscope.controller;

import hu.unideb.interscope.utils.AnimationHelper;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import java.util.function.Consumer;

import lombok.Setter;

public class TheHarvesterSettingsController {
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private GridPane settingsPane;
    @FXML private ComboBox<String> dataSourceComboBox;
    @FXML private TextField startTextField;
    @FXML private ComboBox<String> limitComboBox;
    @FXML private RadioButton shodanOn;
    @FXML private RadioButton shodanOff;

    @Setter
    private Node originalContent;
    private Consumer<Settings> onSaveCallback;

    @FXML
    public void initialize() {
        dataSourceComboBox.setValue("all");
        startTextField.setText("0");
        limitComboBox.setValue("500");
        shodanOff.setSelected(true);

        startTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            newValue = newValue.replaceAll("\\D", "");
            
            // If empty, allow it (will be handled by the next validation)
            if (newValue.isEmpty()) {
                startTextField.setText("");
                return;
            }

            try {
                int number = Integer.parseInt(newValue);
                if (number > Integer.parseInt(limitComboBox.getValue())) {
                    startTextField.setText(limitComboBox.getValue());
                } else {
                    startTextField.setText(newValue);
                }
            } catch (NumberFormatException e) {
                startTextField.setText(oldValue);
            }
        });

        saveButton.setOnAction(event -> {
            if (onSaveCallback != null) {
                Settings settings = new Settings(
                    dataSourceComboBox.getValue(),
                    startTextField.getText(),
                    limitComboBox.getValue(),
                    shodanOn.isSelected()
                );
                onSaveCallback.accept(settings);
            }
            returnToMainView();
        });

        cancelButton.setOnAction(event -> returnToMainView());
    }

    private void returnToMainView() {
        try {
            AnimationHelper.slideGridBelow(settingsPane, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSettings(Settings settings) {
        if (settings != null) {
            dataSourceComboBox.setValue(settings.dataSource());
            startTextField.setText(settings.start());
            limitComboBox.setValue(settings.limit());
            if (settings.shodan()) {
                shodanOn.setSelected(true);
            } else {
                shodanOff.setSelected(true);
            }
        }
    }

    public void setOnSave(Consumer<Settings> callback) {
        this.onSaveCallback = callback;
    }

    public record Settings(
        String dataSource,
        String start,
        String limit,
        boolean shodan
    ) {}
}