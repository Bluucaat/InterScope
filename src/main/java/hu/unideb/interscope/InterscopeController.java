package hu.unideb.interscope;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class InterscopeController {

    public Text interscopeText;

    @FXML
    private ImageView appLogo;
    @FXML
    private ImageView userSearchLogo;
    @FXML
    private ImageView exitLogo;
    @FXML
    private Button userSearch;
    @FXML
    private Button button2;
    @FXML
    private Button button3;
    @FXML
    private Button button4;
    @FXML
    private Button button5;
    @FXML
    private Button exitButton;



    @FXML
    private Text title;
    @FXML
    private Text description;

    @FXML
    public void initialize() {

        appLogo.setImage(new Image("/MenuLogo.png"));
        exitLogo.setImage(new Image("exitLogo.png"));
        userSearchLogo.setImage(new Image("/userSearchLogo.png"));
        Font.loadFont(getClass().getResourceAsStream("/fonts/JuliusSansOne.ttf"), 18);
        exitButton.setGraphic(exitLogo);
    }

    @FXML
    public void closeApplication(){
            System.exit(0);
    }
}

