package ca.keal.raomk.ranch;

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
    
    public static final Image NORMAL = new Image("file:src/assets/cow/normal.png");
    public static final Image SURF = new Image("file:src/assets/cow/surf.png");
    public static final Image DIN = new Image("file:src/assets/cow/din.png");
    public static final Image SPIDER = new Image("file:src/assets/cow/spider.png");
    
    public Cow(Position position, Image image) {
        super(position, image, 40, 40);
    }
    
}