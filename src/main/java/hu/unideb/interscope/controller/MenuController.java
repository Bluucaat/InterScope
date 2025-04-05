package hu.unideb.interscope.controller;

import hu.unideb.interscope.utils.AnimationHelper;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.Getter;

public class MenuController {


    public GridPane theHarvesterSettings;

    private enum AppPart {
        MAIN_MENU, SIDE_MENU
    }

    @FXML
    private Button userSearchButton;
    @FXML
    private Button locationSearchButton;
    @FXML
    private Button emailSearchButton;
    @FXML
    private Button logsButton;
    @FXML
    private Button backButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button exitButton;

    @FXML
    private GridPane mainGrid;
    @FXML
    private GridPane userSearchGrid;
    @FXML
    private GridPane locationSearchGrid;
    @FXML
    private GridPane emailSearchGrid;
    @FXML
    private GridPane logsGrid;
    @FXML
    private GridPane creditsGrid;
    @Getter
    @FXML
    private GridPane activeGrid;
    @FXML
    private Label title;
    @FXML
    private Text description;

    @FXML
    public void initialize() {
        backButton.setTranslateY(100);
        activeGrid = mainGrid;
        Font.loadFont(getClass().getResourceAsStream("/fonts/JuliusSansOne.ttf"), 18);
    }

    @FXML
    public void handleMenuButtonClick(ActionEvent event) {
        if (event.getSource() == userSearchButton) {
            swapGrids(userSearchGrid, AppPart.MAIN_MENU);
            title.setText("Sherlock");
            description.setText("The username search looks across a lot of famous websites. Please be aware that the searches " +
                    "are conducted using your machine's resources and from your IP address, so search accordingly.");
        } else if (event.getSource() == locationSearchButton) {
            swapGrids(locationSearchGrid, AppPart.MAIN_MENU);
            title.setText("theHarvester");
            description.setText("a Python based tool used in reconnaissance to gather e-mail addresses and subdomains related to a target, " +
                    "enabling the identification of potential network usernames for further security testing.");
        } else if (event.getSource() == emailSearchButton) {
            swapGrids(emailSearchGrid, AppPart.MAIN_MENU);
            title.setText("Email Search");
            description.setText("The Domain Search returns all the email addresses found using a given domain name, with sources. " +
                    "The Email Finder determines the most likely email address from a domain name, first name, and last name.");
        } else if (event.getSource() == logsButton) {
            swapGrids(logsGrid, AppPart.MAIN_MENU);
            title.setText("Previous\nSearches");
            description.setText("Here you can find your previous searches. Maybe I'll also implement a way to " +
                    "re-do the searches with the same parameters, though it's unlikely.");
        } else if (event.getSource() == settingsButton) {
            swapGrids(creditsGrid, AppPart.MAIN_MENU);
        } else if (event.getSource() == exitButton) {
            System.exit(0);
        }
    }

    @FXML
    public void handleBackButtonClick() {
        swapGrids(activeGrid, AppPart.SIDE_MENU);
        title.setText("InterScope");
        description.setText("Welcome to InterScope! InterScope is an application that consolidates the functionality of OSINT " +
                "tools into a single, user-friendly platform, making it accessible without prior expertise. Developed by Ribár Krisztián " +
                "as part of a thesis project.");
    }

    @FXML
    public void handleButtonHover(MouseEvent event) {
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
            AnimationHelper.slideGridAbove(mainGrid, true);
            AnimationHelper.slideGridBelow(grid, false);
            slideBackButton(false);
            activeGrid = grid;
        } else if (appPart == AppPart.SIDE_MENU) {
            AnimationHelper.slideGridBelow(grid, true);
            AnimationHelper.slideGridAbove(mainGrid, false);
            slideBackButton(true);
            activeGrid = mainGrid;
        }
    }

    private void slideBackButton(boolean slideOut) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.3), backButton);
        transition.setToY(slideOut ? 100 : 5);
        backButton.setDisable(slideOut);
        transition.play();
    }
}
