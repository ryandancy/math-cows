package ca.keal.raomk;

import javafx.scene.canvas.Canvas;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An ordered pair representing a position on the cartesian plane
 */
@Data
@AllArgsConstructor
public class Position {
    
    private double x;
    private double y;
    
    public double getCanvasX(double originX, double pxPerUnit) {
        return x * pxPerUnit + originX;
    }
    
    public double getCanvasY(double originY, double pxPerUnit) {
        return y * pxPerUnit + originY;
    }
    
    public boolean isOnScreen(Canvas canvas, double originX, double originY, double pxPerUnit,
                              double bufferX, double bufferY) {
        double cx = getCanvasX(originX, pxPerUnit);
        double cy = getCanvasY(originY, pxPerUnit);
        return cx > -bufferX && cy > -bufferY && cx < canvas.getWidth() + bufferX && cy < canvas.getHeight() + bufferY;
    }
    
}