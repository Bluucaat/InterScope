package hu.unideb.interscope;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;

public class InterscopeController {
    private enum AppPart {
        MAINMENU, SIDEMENU
    }

    @FXML
    public Button backButton;

    @FXML
    private GridPane mainGrid;
    @FXML
    private GridPane userSearchGrid;
    @FXML
    public GridPane locationSearchGrid;
    @FXML
    public GridPane emailSearchGrid;
    @FXML
    public GridPane logsGrid;
    @FXML
    public GridPane preferencesGrid;

    public GridPane activeGrid;

    @FXML
    private ImageView appLogo;
    @FXML
    private ImageView locationLogo;
    @FXML
    private ImageView emailLogo;
    @FXML
    private ImageView logsLogo;
    @FXML
    private ImageView userLogo;
    @FXML
    private ImageView preferencesLogo;
    @FXML
    private ImageView exitLogo;

    @FXML
    public Label title;
    @FXML
    private Text description;

    @FXML
    public void initialize() {
        appLogo.setImage(new Image("/MenuLogo.png"));
        userLogo.setImage(new Image("/userLogo.png"));
        locationLogo.setImage(new Image("/locationLogo.png"));
        emailLogo.setImage(new Image("/emailLogo.png"));
        logsLogo.setImage(new Image("/logsLogo.png"));
        preferencesLogo.setImage(new Image("/preferencesLogo.png"));
        exitLogo.setImage(new Image("exitLogo.png"));
        backButton.setTranslateY(100);
        backButton.setTranslateX(mainGrid.getWidth() - backButton.getWidth() - 20);
        activeGrid = mainGrid;
        Font.loadFont(getClass().getResourceAsStream("/fonts/JuliusSansOne.ttf"), 18);
    }

    @FXML
    public void handleMenuButtonClick(ActionEvent event) {
        swapGrids(userSearchGrid, AppPart.MAINMENU);
        title.setText("Username\nSearch");
        description.setText("The username search looks across a lot of famous websites. please be aware that the searches " +
                "are conducted using your machine's resources, and from your IP address, so search accordingly.");
    }

    @FXML
    public void handleLocationButtonClick() {
        swapGrids(locationSearchGrid, AppPart.MAINMENU);
        title.setText("Location\nSearch");
        description.setText("The Location search uses AI to analyze an image uploaded to the application and attempts to approximate where the photo is taken." +
                "In the absence of GPS EXIF data, the system provides GPS estimation.");
    }

    @FXML
    public void handleEmailButtonClick() {
        swapGrids(emailSearchGrid, AppPart.MAINMENU);
        title.setText("Email Search");
        description.setText("The Domain Search returns all the email addresses found using one given domain name, with sources.\n" +
                "The Email Finder finds the most likely email address from a domain name, a first name and a last name.");
    }

    @FXML
    public void handleLogButtonClick() {
        swapGrids(logsGrid, AppPart.MAINMENU);
        title.setText("Previous\nSearches");
        description.setText("Here you can find your previous searches. Maybe i will also implement a way to" +
                "re-do the searches with the same parameters, although its unlikely.");
    }
    @FXML
    public void handlePreferencesButtonClick() {
        swapGrids(preferencesGrid, AppPart.MAINMENU);
    }

    @FXML
    public void handleBackButtonClick() {
        swapGrids(activeGrid, AppPart.SIDEMENU);
        title.setText("InterScope");
        description.setText("Welcome to InterScope! InterScope is an application that consolidates the functionality of OSINT" +
                " tools into a single, user-friendly platform, making it accessible without prior expertise. Developed by Ribár Krisztián " +
                "as part of a thesis project.");
    }

    @FXML
    public void handleLogoHover(MouseEvent event) {
        Node source = (Node) event.getSource();
        ImageView imageView = null;
        if (source instanceof Button button) {
            if (button.getGraphic() instanceof VBox vbox) {
                for (Node node : vbox.getChildren()) {
                    if (node instanceof ImageView) {
                        imageView = (ImageView) node;
                        break;
                    }
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
        if (appPart == AppPart.MAINMENU) {
            slideMainGridOut(mainGrid);
            slideSideGridIn(grid);
            slideBackButtonUp();
            activeGrid = grid;
        } else if (appPart == AppPart.SIDEMENU) {
            slideSideGridOut(grid);
            slideMainGridIn(mainGrid);
            slideBackButtonDown();
            activeGrid = mainGrid;
        }
    }

    private void slideMainGridOut(GridPane gridPane) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.3), gridPane);
        transition.setByY(660);
        transition.play();
    }

    private void slideMainGridIn(GridPane gridPane) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.3), gridPane);
        transition.setByY(-660);
        transition.play();
    }

    private void slideSideGridIn(GridPane gridPane) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.3), gridPane);
        transition.setToY(mainGrid.getTranslateY());
        transition.play();
    }

    private void slideSideGridOut(GridPane gridPane) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.3), gridPane);
        transition.setToY(-(mainGrid.getTranslateY()));
        transition.play();
    }

    private void slideBackButtonUp() {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.3), backButton);
        transition.setToY(5);
        transition.play();
    }

    private void slideBackButtonDown() {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.3), backButton);
        transition.setToY(100);
        transition.play();
    }

    @FXML
    public void closeApplication() {
        System.exit(0);
    }
}
