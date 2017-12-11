package ca.keal.raomk;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Random;

/**
 * A flower on the field.
 */
@AllArgsConstructor
public class Flower {
    
    @Getter private Position position;
    private Type type;
    
    /** Generate a random type */
    public Flower(Position position, Random random) {
        this(position, Type.values()[random.nextInt(Type.values().length)]);
    }
    
    public void draw(GraphicsContext gc, RanchView view, double width, double height) {
        gc.drawImage(type.image, position.getCanvasX(view), position.getCanvasY(view), width, height);
    }
    
    public enum Type {
        ONE("file:src/assets/flowers.png"), TWO("file:src/assets/flowers2.png");
        
        final Image image;
        
        Type(String url) {
            image = new Image(url);
        }
    }
    
}