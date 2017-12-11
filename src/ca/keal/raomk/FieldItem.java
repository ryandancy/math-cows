package ca.keal.raomk;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class FieldItem {
    
    @Getter private Position position;
    @Getter private Image image;
    
    private final double maxWidth;
    private final double maxHeight;
    
    public void draw(Canvas canvas, RanchView view) {
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();
        double aspectRatio = imageWidth / imageHeight;
        
        double width, height;
        if (imageWidth > imageHeight) {
            width = maxWidth;
            height = width / aspectRatio;
        } else {
            height = maxHeight;
            width = height * aspectRatio;
        }
        
        if (position.isOnScreen(canvas, view, width, height)) {
            canvas.getGraphicsContext2D().drawImage(
                    image, position.getCanvasX(view), position.getCanvasY(view), width, height);
        }
    }
    
}