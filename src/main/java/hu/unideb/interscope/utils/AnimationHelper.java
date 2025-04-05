package hu.unideb.interscope.utils;

import javafx.animation.TranslateTransition;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class AnimationHelper {
    public static void slideGridAbove(GridPane gridPane, boolean slideOut) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.3), gridPane);
        transition.setByY(slideOut ? 850 : -850);
        transition.play();
    }

    public static void slideGridBelow(GridPane gridPane, boolean slideOut) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.3), gridPane);
        transition.setByY(slideOut ? -850 : 850);
        transition.play();
    }
}
