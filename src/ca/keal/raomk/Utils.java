package ca.keal.raomk;

import javafx.scene.image.Image;

public final class Utils {
    
    public static boolean approxEqual(double a, double b) {
        return Math.abs(a - b) < 0.005;
    }
    
    public static Image getImageAsset(String path) {
        return new Image("/assets/" + path + ".png");
    }
    
}