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
    
    public double getCanvasX(RanchView view) {
        return x * view.getGridLineGap() + view.getOriginX();
    }
    
    public double getCanvasY(RanchView view) {
        return y * view.getGridLineGap() + view.getOriginY();
    }
    
    public boolean isOnScreen(Canvas canvas, RanchView view, double bufferX, double bufferY) {
        double cx = getCanvasX(view);
        double cy = getCanvasY(view);
        return cx > -bufferX && cy > -bufferY && cx < canvas.getWidth() + bufferX && cy < canvas.getHeight() + bufferY;
    }
    
}