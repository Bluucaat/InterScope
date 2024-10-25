package hu.unideb.interscope;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

    @FXML
    public Button emailButton;
    @FXML
    private GridPane userSearchGrid;
    @FXML
    public GridPane locationSearchGrid;
    @FXML
    private GridPane mainGrid;

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
    public Text title;
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


        Font.loadFont(getClass().getResourceAsStream("/fonts/JuliusSansOne.ttf"), 18);
    }

    @FXML
    public void handleMenuButtonClick(ActionEvent event) {
        swapGrids(userSearchGrid);
        title.setText("Username Search");
        description.setText("The username search looks across a lot of famous websites. please be aware that the searches are conducted using your machine's resources, and from your IP address, so search accordingly.");
    }

    @FXML
    public void handleLocationButtonClick(ActionEvent event) {
        swapGrids(locationSearchGrid);
        title.setText("Location Search");
    }

    @FXML
    public void handleLogsButtonClick(ActionEvent event) {
        //TODO
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

        // On Mouse Entered: Scale Up
        if (event.getEventType() == MouseEvent.MOUSE_ENTERED) {
            scaleUp.play();
        }
        // On Mouse Exited: Scale Down
        else if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
            scaleDown.play();
        }
    }

    private void swapGrids(GridPane grid) {
        grid.setVisible(true);
        grid.setTranslateX(mainGrid.getTranslateX() - 100);
        grid.setTranslateY(-grid.getPrefHeight());
        slideGridPaneOffScreen(mainGrid);
        slideGridIn(grid);

    }

    private void slideGridPaneOffScreen(GridPane gridPane) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.3), gridPane);
        transition.setByY(660);
        transition.play();
    }

    private void slideGridIn(GridPane gridPane) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.3), gridPane);
        transition.setToY(mainGrid.getTranslateY() - 10);
        transition.play();
    }

    @FXML
    public void closeApplication() {
        System.exit(0);
    }
}
