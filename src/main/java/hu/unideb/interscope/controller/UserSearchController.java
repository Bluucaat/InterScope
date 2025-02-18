package hu.unideb.interscope.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class UserSearchController {
    @FXML
    private TextField usernameField;

    @FXML
    private Button searchButton;

    @FXML
    private TableView<SearchResult> resultsTable;

    @FXML
    private TableColumn<SearchResult, String> siteColumn;

    @FXML
    private TableColumn<SearchResult, String> linkColumn;

    @FXML
    private RadioButton sfwRadio;

    @FXML
    private RadioButton nsfwRadio;

    private final ObservableList<SearchResult> searchResults = FXCollections.observableArrayList();

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

        Task<Void> searchTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                List<String> command = new ArrayList<>(List.of("docker", "exec", "interScopeContainer", "sherlock"));

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
                        Platform.runLater(() -> parseAndPopulateSherlockResults(finalLine));
                    }
                }

                process.waitFor();
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    searchButton.setDisable(false);
                    sfwRadio.setDisable(false);
                    nsfwRadio.setDisable(false);

                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    System.err.println("Search failed: " + getException().getMessage());
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
