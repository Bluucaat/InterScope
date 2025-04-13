package hu.unideb.interscope.controller;

import hu.unideb.interscope.InterScopeLauncher;
import hu.unideb.interscope.utils.AnimationHelper;
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

    public GridPane theHarvesterSettingsGrid;

    private enum AppPart {
        MAIN_MENU, SIDE_MENU
    }

    @FXML private Button sherlockButton;
    @FXML private Button theHarvesterButton;
    @FXML private Button spiderFootButton;
    @FXML private Button logsButton;
    @FXML private Button backButton;
    @FXML private Button creditsButton;
    @FXML private Button exitButton;
    @FXML private GridPane mainGrid;
    @FXML private GridPane sherlockGrid;
    @FXML private GridPane theHarvesterGrid;
    @FXML private GridPane spiderfootGrid;
    @FXML private GridPane previousSearchesGrid;
    @FXML private GridPane creditsGrid;
    @Getter
    @FXML private GridPane activeGrid;
    @FXML Label title;
    @FXML private Text description;

    @FXML
    public void initialize() {
        backButton.setTranslateY(100);
        activeGrid = mainGrid;
        Font.loadFont(getClass().getResourceAsStream("/fonts/JuliusSansOne.ttf"), 18);
    }

    @FXML
    public void handleMenuButtonClick(ActionEvent event) {
        if (event.getSource() == sherlockButton) {
            swapGrids(sherlockGrid, AppPart.MAIN_MENU);
            title.setText("Sherlock");
            description.setText("Sherlock is an advanced Open Source Intelligence tool designed for username enumeration. It systematically " +
                    "searches over 300 social media platforms and websites to identify accounts linked to a specific username, helping to map an individual's digital footprint.");
        } else if (event.getSource() == theHarvesterButton) {
            swapGrids(theHarvesterGrid, AppPart.MAIN_MENU);
            title.setText("theHarvester");
            description.setText("a Python based tool used in reconnaissance to gather e-mail addresses and subdomains related to a target, " +
                    "enabling the identification of potential network usernames for further security testing.");
        } else if (event.getSource() == spiderFootButton) {
            swapGrids(spiderfootGrid, AppPart.MAIN_MENU);
            title.setText("SpiderFoot");
            description.setText("SpiderFoot is an open source intelligence automation tool. Its goal is to automate the process of gathering intelligence about a given target.");
        } else if (event.getSource() == logsButton) {
            swapGrids(previousSearchesGrid, AppPart.MAIN_MENU);
            title.setText("Previous\nSearches");
            description.setText("Here you can find your previous searches. Maybe I'll also implement a way to " +
                    "re-do the searches with the same parameters, though it's unlikely.");
        } else if (event.getSource() == creditsButton) {
            swapGrids(creditsGrid, AppPart.MAIN_MENU);
        } else if (event.getSource() == exitButton) {
            InterScopeLauncher.customExit();
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

        if (event.getEventType() == MouseEvent.MOUSE_ENTERED) {
            AnimationHelper.scaleUpTransition(imageView);
        } else if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
            AnimationHelper.scaleDownTransition(imageView);
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
