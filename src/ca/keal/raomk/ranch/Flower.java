package ca.keal.raomk.ranch;

import ca.keal.raomk.Utils;
import javafx.scene.image.Image;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Random;

/**
 * A flower on the field.
 */
@EqualsAndHashCode(callSuper = true)
@ToString
class Flower extends FieldItem {
    
    private static final Image IMAGE1 = Utils.getImageAsset("flowers");
    private static final Image IMAGE2 = Utils.getImageAsset("flowers2");
    
    private Flower(Position position, Image image) {
        super(position, image, 33.33, 30);
    }
    
    static Flower random(Position position, Random random) {
        return new Flower(position, random.nextBoolean() ? IMAGE1 : IMAGE2);
    }
    
}