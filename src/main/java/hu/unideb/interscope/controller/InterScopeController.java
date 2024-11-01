package hu.unideb.interscope.controller;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;

public class InterScopeController {
    private enum AppPart {
        MAIN_MENU, SIDE_MENU
    }

    private @FXML Button userSearchButton;
    private @FXML Button locationSearchButton;
    private @FXML Button emailSearchButton;
    private @FXML Button logsButton;
    private @FXML Button backButton;
    private @FXML Button settingsButton;
    private @FXML Button exitButton;

    private @FXML GridPane mainGrid;
    private @FXML GridPane userSearchGrid;
    private @FXML GridPane locationSearchGrid;
    private @FXML GridPane emailSearchGrid;
    private @FXML GridPane logsGrid;
    private @FXML GridPane preferencesGrid;
    private GridPane activeGrid;

    private @FXML Label title;
    private @FXML Text description;

    @FXML
    public void initialize() {
        backButton.setTranslateY(100);
        backButton.setTranslateX(mainGrid.getWidth() - backButton.getWidth() - 20);
        activeGrid = mainGrid;
        Font.loadFont(getClass().getResourceAsStream("/fonts/JuliusSansOne.ttf"), 18);
    }

    @FXML
    public void handleMenuButtonClick(ActionEvent event) {
        if (event.getSource() == userSearchButton) {
            swapGrids(userSearchGrid, AppPart.MAIN_MENU);
            title.setText("Username\nSearch");
            description.setText("The username search looks across a lot of famous websites. please be aware that the searches " +
                    "are conducted using your machine's resources, and from your IP address, so search accordingly.");
        } else if (event.getSource() == locationSearchButton) {
            swapGrids(locationSearchGrid, AppPart.MAIN_MENU);
            title.setText("Location\nSearch");
            description.setText("The Location search uses AI to analyze an image uploaded to the application and attempts to approximate where the photo is taken." +
                    "In the absence of GPS EXIF data, the system provides GPS estimation.");
        } else if (event.getSource() == emailSearchButton) {
            swapGrids(emailSearchGrid, AppPart.MAIN_MENU);
            title.setText("Email Search");
            description.setText("The Domain Search returns all the email addresses found using one given domain name, with sources.\n" +
                    "The Email Finder finds the most likely email address from a domain name, a first name and a last name.");
        } else if (event.getSource() == logsButton) {
            swapGrids(logsGrid, AppPart.MAIN_MENU);
            title.setText("Previous\nSearches");
            description.setText("Here you can find your previous searches. Maybe i will also implement a way to" +
                    "re-do the searches with the same parameters, although its unlikely.");
        } else if (event.getSource() == settingsButton) {
            swapGrids(preferencesGrid, AppPart.MAIN_MENU);
        } else if (event.getSource() == exitButton) {
            System.exit(0);
        }
    }

    @FXML
    public void handleBackButtonClick() {
        swapGrids(activeGrid, AppPart.SIDE_MENU);
        title.setText("InterScope");
        description.setText("Welcome to InterScope! InterScope is an application that consolidates the functionality of OSINT" +
                " tools into a single, user-friendly platform, making it accessible without prior expertise. Developed by Ribár Krisztián " +
                "as part of a thesis project.");
    }

    @FXML
    public void handleLogoHover(MouseEvent event) {
        Button source = (Button) event.getSource();
        ImageView imageView = null;
        if (source.getGraphic() instanceof VBox vbox) {
            for (Node node : vbox.getChildren()) {
                if (node instanceof ImageView) {
                    imageView = (ImageView) node;
                    break;
                }
            }
        }
        if (imageView == null) {
            return;
        }

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), imageView);
        scaleUp.setToX(1.3);
        scaleUp.setToY(1.3);
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), imageView);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        if (event.getEventType() == MouseEvent.MOUSE_ENTERED) {
            scaleUp.play();
        } else if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
            scaleDown.play();
        }
    }

    private void swapGrids(GridPane grid, AppPart appPart) {
        if (appPart == AppPart.MAIN_MENU) {
            slideMainGrid(true);
            slideSideGrid(grid, false);
            slideBackButton(false);
            activeGrid = grid;
        } else if (appPart == AppPart.SIDE_MENU) {
            slideSideGrid(grid, true);
            slideMainGrid(false);
            slideBackButton(true);
            activeGrid = mainGrid;
        }
    }

    private void slideMainGrid(boolean slideOut) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.3), mainGrid);
        if (slideOut) transition.setByY(660);
        else transition.setByY(-660);
        transition.play();
    }

    private void slideSideGrid(GridPane gridPane, boolean slideOut) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.3), gridPane);
        if (slideOut) transition.setToY(-(mainGrid.getTranslateY()));
        else transition.setToY(mainGrid.getTranslateY());
        transition.play();
    }

    private void slideBackButton(boolean slideOut) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.3), backButton);
        if (slideOut) transition.setToY(100);
        else transition.setToY(5);
        transition.play();
    }
}
