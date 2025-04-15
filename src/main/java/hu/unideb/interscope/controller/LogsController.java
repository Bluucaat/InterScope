package hu.unideb.interscope.controller;

import hu.unideb.interscope.model.Log;
import hu.unideb.interscope.repository.LogRepository;
import hu.unideb.interscope.utils.AnimationHelper;
import hu.unideb.interscope.utils.JSONResultWriter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.text.SimpleDateFormat;

public class LogsController {
    private static final Logger logger = LoggerFactory.getLogger(LogsController.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
    @FXML private TableView<Log> logsTable;
    @FXML private TableColumn<Log, String> toolColumn;
    @FXML private TableColumn<Log, String> targetColumn;
    @FXML private TableColumn<Log, String> dateColumn;
    @FXML private TableColumn<Log, Void> actionsColumn;
    @FXML private Button clearButton;
    @FXML private TextArea detailsTextArea;
    
    private final ObservableList<Log> logEntries = FXCollections.observableArrayList();
    private boolean isDetailsViewActive = false;
    
    @FXML
    public void initialize() {
        setupTableColumns();

        if (detailsTextArea != null) {
            detailsTextArea.setVisible(false);
            detailsTextArea.setEditable(false);
            detailsTextArea.setWrapText(true);
        }

        refreshLogsTable();
    }
    
    private void setupTableColumns() {
        toolColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().tool()));
        targetColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().searchTarget()));
        dateColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().searchDate() != null 
                ? DATE_FORMAT.format(data.getValue().searchDate()) : "Unknown"));

        toolColumn.setCellFactory(_ -> getStyledCell());
        targetColumn.setCellFactory(_ -> getStyledCell());
        dateColumn.setCellFactory(_ -> getStyledCell());

        actionsColumn.setCellFactory(createDetailsCellFactory());
        logsTable.setItems(logEntries);
    }
    
    private TableCell<Log, String> getStyledCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                setStyle("-fx-text-fill: white;");
                setAlignment(javafx.geometry.Pos.CENTER);
                
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item);
                }
            }
        };
    }
    
    private Callback<TableColumn<Log, Void>, TableCell<Log, Void>> createDetailsCellFactory() {
        return _ -> new TableCell<>() {
            private final Button detailsButton = new Button("Details");
            
            {
                detailsButton.getStyleClass().add("search-button");
                detailsButton.setPrefWidth(80);
                detailsButton.setMaxHeight(25);
                detailsButton.setStyle("-fx-background-color: #1B1B28;");
                
                detailsButton.setOnMouseEntered(_ ->
                    detailsButton.setStyle("-fx-background-color: #232334;"));
                detailsButton.setOnMouseExited(_ ->
                    detailsButton.setStyle("-fx-background-color: #1B1B28;"));
                
                detailsButton.setOnAction(_ -> {
                    Log log = getTableView().getItems().get(getIndex());
                    showDetails(log);
                });
            }
            
            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : detailsButton);
                if (!empty) setAlignment(javafx.geometry.Pos.CENTER);
            }
        };
    }
    
    private void showDetails(Log log) {
        if (detailsTextArea != null) {
            detailsTextArea.setText(log.description());
            clearButton.setText("all results");
            AnimationHelper.crossFade(logsTable, detailsTextArea, 180, null);
            isDetailsViewActive = true;
        }
    }
    
    private void returnToTableView() {
        if (detailsTextArea != null) {
            clearButton.setText("Clear");
            AnimationHelper.crossFade(detailsTextArea, logsTable, 180, null);
            isDetailsViewActive = false;
        }
    }
    
    @FXML
    private void handleClearButtonClick() {
        if (isDetailsViewActive) {
            returnToTableView();
        } else {
            clearSearchHistory();
        }
    }

    private void clearSearchHistory() {
        try {
            Files.write(JSONResultWriter.LOG_PATH, "[]".getBytes());
            refreshLogsTable();
        } catch (Exception e) {
            logger.error("Error clearing search history: {}", e.getMessage());
        }
    }


    public void refreshLogsTable() {
        logEntries.clear();
        logEntries.addAll(LogRepository.parseLogs());
    }
}