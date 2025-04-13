package hu.unideb.interscope;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class InterScopeLauncher extends Application {
    
    private static final Logger logger = LoggerFactory.getLogger(InterScopeLauncher.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        startDockerContainer();
        
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/mainGrid.fxml")));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/main.css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/submenus.css")).toExternalForm());
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/InterScopeAppLogo.png"))));
        primaryStage.setTitle("InterScope");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> stopContainer());
    }

    public static void customExit() {
        stopContainer();
        Platform.exit();
        System.exit(0);
    }
    
    // Single method to stop the container, both for window close and exit button
    private static void stopContainer() {
        try {
            new ProcessBuilder("docker", "stop", "interscope-tools").start();
        } catch (Exception e) {
            logger.error("Error stopping container: {}", e.getMessage());
        }
    }

    private void startDockerContainer() {
        CompletableFuture.runAsync(() -> {
            try {
                // Check if Docker is running with a simple command
                try {
                    Process dockerCheck = new ProcessBuilder("docker", "info").redirectErrorStream(true).start();
                    int exitCode = dockerCheck.waitFor();
                    if (exitCode != 0) {
                        logger.error("Docker is not running or is not installed. Please start/install Docker first, and run the application again.");
                        return;
                    }
                } catch (Exception e) {
                    logger.error("Error while checking for Docker: {}", e.getMessage());
                    return;
                }
                
                // Check if the container is running
                Process runningProcess = new ProcessBuilder("docker", "ps", "--filter", "name=interscope-tools", "-q").start();
                String containerId = new BufferedReader(new InputStreamReader(runningProcess.getInputStream())).readLine();
                
                if (containerId != null && !containerId.isEmpty()) {
                    return;
                }
                // Check if the image exists
                Process imageProcess = new ProcessBuilder("docker", "images", "interscope-tools", "-q").start();
                String imageId = new BufferedReader(new InputStreamReader(imageProcess.getInputStream())).readLine();
                
                if (imageId == null || imageId.isEmpty()) {
                    // Build the image
                    logger.info("Building Docker image from Dockerfile...");
                    String projectRoot = System.getProperty("user.dir");
                    
                    ProcessBuilder buildBuilder = new ProcessBuilder("docker", "build", "-t", "interscope-tools", ".");
                    buildBuilder.directory(new java.io.File(projectRoot));
                    buildBuilder.redirectErrorStream(true);
                    Process buildProcess = buildBuilder.start();
                    
                    // Log the build output
                    BufferedReader buildReader = new BufferedReader(new InputStreamReader(buildProcess.getInputStream()));
                    String line;
                    while ((line = buildReader.readLine()) != null) {
                        logger.info("Docker build: {}", line);
                    }
                    
                    if (buildProcess.waitFor() != 0) {
                        logger.error("Docker build failed");
                        return;
                    }
                }
                Process startProcess = new ProcessBuilder("docker", "run", "-d", "-p", "5001:5001", "--name", "interscope-tools", "interscope-tools").start();
                startProcess.waitFor();
                logger.info("Docker container started successfully");
                
            } catch (Exception e) {
                logger.error("Error managing Docker container: {}", e.getMessage());
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
