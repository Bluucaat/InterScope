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

                Process runningProcess = new ProcessBuilder("docker", "ps", "--filter", "name=interscope-tools", "-q").start();
                String runningContainerId = new BufferedReader(new InputStreamReader(runningProcess.getInputStream())).readLine();

                if (runningContainerId != null && !runningContainerId.isEmpty()) {
                    return;
                }

                Process existingProcess = new ProcessBuilder("docker", "ps", "-a", "--filter", "name=interscope-tools", "-q").start();
                String existingContainerId = new BufferedReader(new InputStreamReader(existingProcess.getInputStream())).readLine();

                if (existingContainerId != null && !existingContainerId.isEmpty()) {
                    Process startProcess = new ProcessBuilder("docker", "start", "interscope-tools").start();
                    if (startProcess.waitFor() == 0) {
                        logger.info("Successfully started existing container 'interscope-tools'.");
                    }
                    return;
                }

                logger.info("Container 'interscope-tools' not found. Creating a new one.");

                Process imageProcess = new ProcessBuilder("docker", "images", "interscope-tools", "-q").start();
                String imageId = new BufferedReader(new InputStreamReader(imageProcess.getInputStream())).readLine();
                
                if (imageId == null || imageId.isEmpty()) {
                    logger.info("Building Docker image from Dockerfile...");
                    String projectRoot = System.getProperty("user.dir");
                    
                    ProcessBuilder imageBuilder = new ProcessBuilder("docker", "build", "-t", "interscope-tools", ".");
                    imageBuilder.directory(new java.io.File(projectRoot));
                    imageBuilder.redirectErrorStream(true);
                    Process imageBuildProcess = imageBuilder.start();

                    BufferedReader buildReader = new BufferedReader(new InputStreamReader(imageBuildProcess.getInputStream()));
                    String line;
                    while ((line = buildReader.readLine()) != null) {
                        logger.info("Docker build: {}", line);
                    }
                    
                    if (imageBuildProcess.waitFor() != 0) {
                        logger.error("Docker build failed");
                        return;
                    }
                }
                Process runProcess = new ProcessBuilder("docker", "run", "-d", "-p", "5001:5001", "--name", "interscope-tools", "interscope-tools").start();
                if (runProcess.waitFor() == 0) {
                    logger.info("Docker container created and started successfully");
                } else {
                    logger.error("Failed to create and start Docker container.");
                }
                
            } catch (Exception e) {
                logger.error("Error managing Docker container: {}", e.getMessage());
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
