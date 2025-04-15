package hu.unideb.interscope.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SpiderfootController {

    @FXML private Button startSpiderfootButton;
    @FXML private Button openInBrowserButton;
    @FXML private TextArea spiderfootOutputBox;

    private boolean isSpiderfootRunning = false;
    private final StringBuilder outputBuffer = new StringBuilder();

    @FXML
    private void initialize() {
        startSpiderfootButton.setOnAction(_ -> toggleSpiderfoot());
        openInBrowserButton.setOnAction(_ -> openInBrowser());
        openInBrowserButton.setDisable(true);
    }
    
    private void toggleSpiderfoot() {
        if (isSpiderfootRunning) {
            stopSpiderfoot();
        } else {
            startSpiderfoot();
        }
    }

    private void startSpiderfoot() {
        startSpiderfootButton.setDisable(true); 
        runDockerCommand("start-spiderfoot", true);
    }

    private void stopSpiderfoot() {
        startSpiderfootButton.setDisable(true);
        runDockerCommand("stop-spiderfoot", false);
    }

    private void runDockerCommand(String command, boolean setRunningWhenDone) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ProcessBuilder processBuilder = new ProcessBuilder("docker", "exec", "interscope-tools", command);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        final String text = line;
                        Platform.runLater(() -> addOutput(text));
                    }
                }

                process.waitFor();
                return null;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    setSpiderfootRunning(setRunningWhenDone);
                    startSpiderfootButton.setDisable(false);
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    startSpiderfootButton.setDisable(false);
                    addOutput("Failed to " + (setRunningWhenDone ? "start" : "stop") + " SpiderFoot: " + getException().getMessage());
                });
            }
        };
        
        new Thread(task).start();
    }

    private void openInBrowser() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win") || os.contains("mac")) {
                java.awt.Desktop.getDesktop().browse(new java.net.URI("http://localhost:5001"));
            } else {
                new ProcessBuilder("xdg-open", "http://localhost:5001").start();
            }
        } catch (Exception e) {
            addOutput("Error opening browser: " + e.getMessage());
        }
    }


    private void setSpiderfootRunning(boolean running) {
        isSpiderfootRunning = running;
        
        if (running) {
            startSpiderfootButton.setText("stop spiderfoot");
            openInBrowserButton.setDisable(false);
        } else {
            startSpiderfootButton.setText("start spiderfoot");
            openInBrowserButton.setDisable(true);
        }
    }

    private void addOutput(String line) {
        if (line != null && !line.trim().isEmpty()) {
            outputBuffer.append(line).append("\n");
            spiderfootOutputBox.appendText(line + "\n");
            spiderfootOutputBox.positionCaret(outputBuffer.length());
        }
    }

} 