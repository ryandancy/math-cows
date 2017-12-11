package ca.keal.raomk;

import javafx.scene.canvas.Canvas;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Contains the data involving the view of the ranch (i.e. origin position, gap between grid lines, etc.)
 */
@EqualsAndHashCode
@ToString
public class RanchView {
    
    @Getter private final double gridLineGap; // final because no zooming currently
    @Getter private double offsetX = 0;
    @Getter private double offsetY = 0;
    
    private double originalOriginX;
    private double originalOriginY;
    
    public RanchView(double gridLineGap, double originX, double originY) {
        this.gridLineGap = gridLineGap;
        this.originalOriginX = originX;
        this.originalOriginY = originY;
    }
    
    /**
     * Infers the originalOriginX and originY as center of canvas. Also sets up this RanchView
     * as a listener to the canvas' height and width property to automatically position properly.
     */
    public RanchView(double gridLineGap, Canvas canvas) {
        this(gridLineGap, canvas.getWidth() / 2, canvas.getHeight() / 2);
        canvas.widthProperty().addListener((property, oldWidth, newWidth) ->
                originalOriginX = newWidth.doubleValue() / 2);
        canvas.heightProperty().addListener((property, oldHeight, newHeight) ->
                originalOriginY = newHeight.doubleValue() / 2);
    }
    
    public double getOriginX() {
        return originalOriginX + offsetX;
    }
    
    public double getOriginY() {
        return originalOriginY + offsetY;
    }
    
    public void shift(double shiftX, double shiftY) {
        offsetX += shiftX;
        offsetY += shiftY;
    }
    
}