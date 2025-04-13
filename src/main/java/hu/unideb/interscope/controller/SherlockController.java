package hu.unideb.interscope.controller;

import hu.unideb.interscope.utils.ResultSaver;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SherlockController {
    @FXML private TextField usernameField;
    @FXML private Button searchButton;
    @FXML private TableView<SearchResult> resultsTable;
    @FXML private TableColumn<SearchResult, String> siteColumn;
    @FXML private TableColumn<SearchResult, String> linkColumn;
    @FXML private RadioButton sfwRadio;
    @FXML private RadioButton nsfwRadio;

    private static final Logger logger = LoggerFactory.getLogger(SherlockController.class);

    private final ObservableList<SearchResult> searchResults = FXCollections.observableArrayList();
    private final StringBuilder outputBuffer = new StringBuilder();

    @FXML
    public void initialize() {
        siteColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().site()));
        linkColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().link()));
        resultsTable.setItems(searchResults);

        searchButton.setOnAction(_ -> performSearch());
    }

    private void performSearch() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            return;
        }

        searchButton.setDisable(true);
        sfwRadio.setDisable(true);
        nsfwRadio.setDisable(true);
        searchResults.clear();
        outputBuffer.setLength(0);

        Task<Void> searchTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                List<String> command = new ArrayList<>(List.of("docker", "exec", "interscope-tools", "sherlock"));

                if (nsfwRadio.isSelected()) {
                    command.add("--nsfw");
                }

                command.add(username);

                ProcessBuilder processBuilder = new ProcessBuilder(command);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String finalLine = line;
                        outputBuffer.append(finalLine).append("\n");
                        Platform.runLater(() -> parseAndPopulateSherlockResults(finalLine));
                    }
                }

                process.waitFor();
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    if (!outputBuffer.isEmpty()) {
                        ResultSaver.saveResults("Sherlock_" + username, outputBuffer.toString());
                    }
                    
                    searchButton.setDisable(false);
                    sfwRadio.setDisable(false);
                    nsfwRadio.setDisable(false);
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    Throwable exception = getException();
                    logger.error("Search failed: {}", exception.getMessage(), exception);
                    searchButton.setDisable(false);
                    sfwRadio.setDisable(false);
                    nsfwRadio.setDisable(false);
                });
            }
        };

        Thread searchThread = new Thread(searchTask);
        searchThread.setDaemon(true);
        searchThread.start();
    }

    private void parseAndPopulateSherlockResults(String line) {
        if (line.trim().isEmpty()) {
            return;
        }

        if (line.contains("[+]")) {
            String[] parts = line.split(":\\s+");
            if (parts.length == 2) {
                String site = parts[0].replace("[+] ", "").trim();
                String link = parts[1].trim();
                searchResults.add(new SearchResult(site, link));
            }
        }
    }

    public record SearchResult(String site, String link) {
    }
}
