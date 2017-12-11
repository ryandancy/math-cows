package ca.keal.raomk;

import javafx.scene.image.Image;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Random;

/**
 * A flower on the field.
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class Flower extends FieldItem {
    
    private static final Image IMAGE1 = new Image("file:src/assets/flowers.png");
    private static final Image IMAGE2 = new Image("file:src/assets/flowers2.png");
    
    private Flower(Position position, Image image) {
        super(position, image, 33.33, 30);
    }
    
    public static Flower random(Position position, Random random) {
        return new Flower(position, random.nextBoolean() ? IMAGE1 : IMAGE2);
    }
    
}