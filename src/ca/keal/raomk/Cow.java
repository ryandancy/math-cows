package ca.keal.raomk;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * A "cow" graphic on the field.
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Cow {
    
    private static final double MAX_WIDTH = 40;
    private static final double MAX_HEIGHT = 40;
    
    private Position position;
    private Type type;
    
    public void draw(Canvas canvas, RanchView view) {
        double imageWidth = type.image.getWidth();
        double imageHeight = type.image.getHeight();
        double aspectRatio = imageWidth / imageHeight;
        
        double width, height;
        if (imageWidth > imageHeight) {
            width = MAX_WIDTH;
            height = width / aspectRatio;
        } else {
            height = MAX_HEIGHT;
            width = height * aspectRatio;
        }
        
        if (position.isOnScreen(canvas, view, width, height)) {
            canvas.getGraphicsContext2D()
                    .drawImage(type.image, position.getCanvasX(view), position.getCanvasY(view), width, height);
        }
    }
    
    public enum Type {
        NORMAL("normal.png"), SURF("surf.png"), DIN("din.png");
        
        final Image image;
        
        Type(String url) {
            image = new Image("file:src/assets/cow/" + url);
        }
    }
    
}