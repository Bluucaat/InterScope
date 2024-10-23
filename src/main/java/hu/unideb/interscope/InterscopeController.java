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
    private Button button1;
    @FXML
    private Button button2;
    @FXML
    private Button button3;
    @FXML
    private Button button4;
    @FXML
    private Button button5;
    @FXML
    private Button button6;

    @FXML
    private ImageView logo;

    @FXML
    private Text title;
    @FXML
    private Text description;

    @FXML
    public void initialize() {
        logo.setImage(new Image("/MenuLogo.png"));
        Font.loadFont(getClass().getResourceAsStream("/JuliusSansOne.ttf"), 18);
        interscopeText.setFont(Font.font("Julius Sans One", 18));
        description.setFont(Font.font("Julius Sans One", 11));
    }
    }

