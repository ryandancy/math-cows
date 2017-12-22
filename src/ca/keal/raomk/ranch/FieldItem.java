package ca.keal.raomk.ranch;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
abstract class FieldItem {
    
    @Getter private Position position;
    @Getter private Image image;
    
    private final double maxWidth;
    private final double maxHeight;
    
    /** Draw the FieldItem. Note: draws it centered. */
    void draw(Canvas canvas, RanchView view) {
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();
        double aspectRatio = imageWidth / imageHeight;
        
        // don't let the width or height exceed maxWidth/maxHeight by setting one to it
        double width, height;
        if (imageWidth > imageHeight) {
            width = maxWidth;
            height = width / aspectRatio;
        } else {
            height = maxHeight;
            width = height * aspectRatio;
        }
        
        if (position.isOnScreen(canvas, view, width, height)) {
            // center the image on position
            canvas.getGraphicsContext2D().drawImage(image,
                    position.getCenteredCanvasX(view, width),
                    position.getCenteredCanvasY(view, height),
                    width, height);
        }
    }
    
}