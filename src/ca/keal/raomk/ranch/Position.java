package ca.keal.raomk.ranch;

import javafx.scene.canvas.Canvas;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An ordered pair representing a position on the cartesian plane. Immutable.
 */
@Data
@AllArgsConstructor
public class Position {
    
    private final double x;
    private final double y;
    
    /**
     * Creates a Position from coordinates on a canvas.
     */
    public static Position fromCanvasCoords(RanchView view, double canvasX, double canvasY) {
        return new Position(canvasToCartesianX(canvasX, view), canvasToCartesianY(canvasY, view));
    }
    
    public static double cartesianToCanvasX(double x, RanchView view) {
        return x * view.getGridLineGap() + view.getOriginX();
    }
    
    public static double cartesianToCanvasY(double y, RanchView view) {
        // inverted y because the canvas y is flipped from the cartesian y
        return -y * view.getGridLineGap() + view.getOriginY();
    }
    
    public static double canvasToCartesianX(double x, RanchView view) {
        return (x - view.getOriginX()) / view.getGridLineGap();
    }
    
    public static double canvasToCartesianY(double y, RanchView view) {
        // inverted because the canvas y is flipped from the cartesian y
        return -(y - view.getOriginY()) / view.getGridLineGap();
    }
    
    public double getCanvasX(RanchView view) {
        return cartesianToCanvasX(x, view);
    }
    
    public double getCanvasY(RanchView view) {
        return cartesianToCanvasY(y, view);
    }
    
    /**
     * Get the canvas x coordinate shifted such that a box of {@code width} would be centred when drawn at the return
     * value x.
     */
    public double getCenteredCanvasX(RanchView view, double width) {
        return getCanvasX(view) - (width/2);
    }
    
    /**
     * Get the canvas y coordinate shifted such that a box of {@code height} would be centred when drawn at the return
     * value y.
     */
    public double getCenteredCanvasY(RanchView view, double height) {
        return getCanvasY(view) - (height/2);
    }
    
    public boolean isOnScreen(Canvas canvas, RanchView view, double bufferX, double bufferY) {
        double cx = getCanvasX(view);
        double cy = getCanvasY(view);
        return cx > -bufferX && cy > -bufferY && cx < canvas.getWidth() + bufferX && cy < canvas.getHeight() + bufferY;
    }
    
}