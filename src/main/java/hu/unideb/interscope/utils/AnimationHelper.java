package hu.unideb.interscope.utils;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
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

    public static void scaleUpTransition(ImageView imageView) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), imageView);
        scaleUp.setToX(1.3);
        scaleUp.setToY(1.3);
        scaleUp.play();
    }

    public static void scaleDownTransition(ImageView imageView) {
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), imageView);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);
        scaleDown.play();
    }
}
