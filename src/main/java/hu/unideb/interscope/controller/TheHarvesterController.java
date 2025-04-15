package hu.unideb.interscope.controller;

import hu.unideb.interscope.repository.LogRepository;
import hu.unideb.interscope.utils.AnimationHelper;
import hu.unideb.interscope.utils.JSONResultWriter;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TheHarvesterController {
    @FXML private Button theHarvesterSettingsButton;
    @FXML private TextField domainNameField;
    @FXML private Button searchButton;
    @FXML private GridPane theHarvesterGrid;
    @FXML private TextArea theHarvesterOutputBox;

    private static final Logger logger = LoggerFactory.getLogger(TheHarvesterController.class);
    private TheHarvesterSettingsController.Settings currentSettings;
    private StringBuilder outputBuffer = new StringBuilder();

    @FXML
    public void initialize() {
        currentSettings = new TheHarvesterSettingsController.Settings(
                "anubis", "0", "500", false);
        theHarvesterSettingsButton.setOnAction(_ -> openSettingsWindow());

        searchButton.setOnAction(_ -> performSearch());
    }

    @FXML
    private void openSettingsWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/theHarvesterSettings.fxml"));
            GridPane settingsPane = loader.load();


            TheHarvesterSettingsController settingsController = loader.getController();
            settingsController.setSettings(currentSettings);

            settingsController.setOnSave(settings -> currentSettings = settings);

            theHarvesterGrid.add(settingsPane, 0, 0);

            AnimationHelper.slideGridBelow(settingsPane, false);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void performSearch() {
        String domainName = domainNameField.getText().trim();
        if (domainName.isEmpty()) {
            return;
        }

        outputBuffer = new StringBuilder();
        theHarvesterOutputBox.clear();

        searchButton.setDisable(true);
        domainNameField.setDisable(true);
        theHarvesterSettingsButton.setDisable(true);

        Task<Void> searchTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                List<String> command = getStrings(domainName);
                ProcessBuilder processBuilder = new ProcessBuilder(command);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String finalLine = line;
                        Platform.runLater(() -> addOutput(finalLine));
                    }
                }

                process.waitFor();
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    if (!outputBuffer.isEmpty() && !(outputBuffer.toString().toLowerCase().charAt(0) == 'e')){
                        LogRepository.parseLogs();
                        JSONResultWriter.saveResults("TheHarvester_" + domainNameField.getText(), outputBuffer.toString());
                    }
                    
                    searchButton.setDisable(false);
                    domainNameField.setDisable(false);
                    theHarvesterSettingsButton.setDisable(false);
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    System.err.println("Search failed: " + getException().getMessage());
                    addOutput("Search failed: " + getException().getMessage());
                    searchButton.setDisable(false);
                    domainNameField.setDisable(false);
                    theHarvesterSettingsButton.setDisable(false);
                });
            }
        };

        Thread searchThread = new Thread(searchTask);
        searchThread.setDaemon(true);
        searchThread.start();
    }

    private List<String> getStrings(String domainName) {
        List<String> command = new ArrayList<>(List.of("docker", "exec", "interscope-tools", "theHarvester"));

        if (currentSettings != null) {
            command.add("-b");
            command.add(currentSettings.dataSource());
            command.add("-l");
            command.add(currentSettings.limit());
            command.add("-S");
            command.add(currentSettings.start());
            if (currentSettings.shodan()) {
                command.add("-s");
            }
        }

        command.add("-d");
        command.add(domainName);
        return command;
    }

    public void addOutput(String line) {
        if (line != null && !line.trim().isEmpty()) {
            outputBuffer.append(line).append("\n");
            theHarvesterOutputBox.setText(outputBuffer.toString());
            theHarvesterOutputBox.positionCaret(outputBuffer.length());
        }
    }
} 