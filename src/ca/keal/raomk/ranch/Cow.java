package ca.keal.raomk.ranch;

import ca.keal.raomk.Utils;
import javafx.scene.image.Image;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * A "cow" graphic on the field.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class Cow extends FieldItem {
    
    public static final Image NORMAL = Utils.getImageAsset("cow/normal");
    public static final Image SURF = Utils.getImageAsset("cow/surf");
    public static final Image DIN = Utils.getImageAsset("cow/din");
    public static final Image SPIDER = Utils.getImageAsset("cow/spider");
    
    public Cow(Position position, Image image) {
        super(position, image, 40, 40);
    }
    
}