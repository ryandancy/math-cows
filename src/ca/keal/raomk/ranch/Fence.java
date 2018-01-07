package ca.keal.raomk.ranch;

import ca.keal.raomk.Utils;
import ca.keal.raomk.dr.Interval;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Fence {
    
    private static final Image[] ARROWS = {
            getArrowImage("right_exclusive"),
            getArrowImage("right_inclusive"),
            getArrowImage("left_exclusive"),
            getArrowImage("left_inclusive"),
            getArrowImage("up_exclusive"),
            getArrowImage("up_inclusive"),
            getArrowImage("down_exclusive"),
            getArrowImage("down_inclusive")
    };
    private static final double ARROW_LENGTH = 10;
    private static final double ARROW_BUFFER = 2.5;
    
    private static Image getArrowImage(String name) {
        return Utils.getImageAsset("fence_arrow/" + name);
    }
    
    private static final double PCT_GRID_LINE_GAP_FENCE_TAKES_UP = 0.85;
    
    private final Orientation orientation;
    private final double coord;
    private final boolean upperBound;
    private final boolean inclusive;
    
    /** Convert an {@link Interval} to a list of {@link Fence}s. Orientation corresponds to domain/range. */
    public static List<Fence> intervalToFences(Interval interval, Orientation orientation) {
        List<Fence> fences = new ArrayList<>();
        
        // Add fences for non-infinity bounds
        if (!interval.getLowerBound().equals(Interval.Bound.NEG_INFINITY)) {
            fences.add(new Fence(orientation, interval.getLowerBound().getNumber(), false,
                    interval.getLowerBound().isInclusive()));
        }
        
        if (!interval.getUpperBound().equals(Interval.Bound.INFINITY)) {
            fences.add(new Fence(orientation, interval.getUpperBound().getNumber(), true,
                    interval.getUpperBound().isInclusive()));
        }
        
        return fences;
    }
    
    public void draw(Canvas canvas, RanchView view) {
        double heightOneSegment = PCT_GRID_LINE_GAP_FENCE_TAKES_UP * view.getGridLineGap() * orientation.scaleFactor;
        double widthOneSegment = (orientation.image.getWidth() / orientation.image.getHeight()) * heightOneSegment;
        
        if (!isOnScreen(canvas, view, Math.max(heightOneSegment, widthOneSegment))) return;
        
        GraphicsContext gc = canvas.getGraphicsContext2D();
        if (orientation == Orientation.HORIZONTAL) {
            // horizontal fences for x > 0
            for (double x = view.getOriginX(); x < canvas.getWidth() + widthOneSegment; x += widthOneSegment) {
                drawHorizontal(x, gc, view, widthOneSegment, heightOneSegment);
            }
            
            // horizontal fences for x < 0
            for (double x = view.getOriginX(); x > -widthOneSegment; x -= widthOneSegment) {
                drawHorizontal(x, gc, view, widthOneSegment, heightOneSegment);
            }
        } else {
            // vertical fences for y > 0
            for (double y = view.getOriginY(); y < canvas.getHeight() + heightOneSegment; y += heightOneSegment) {
                drawVertical(y, gc, view, widthOneSegment, heightOneSegment);
            }
            
            // vertical fences for y < 0
            for (double y = view.getOriginY(); y > -heightOneSegment; y -= heightOneSegment) {
                drawVertical(y, gc, view, widthOneSegment, heightOneSegment);
            }
        }
    }
    
    private void drawHorizontal(double x, GraphicsContext gc, RanchView view,
                                double widthOneSegment, double heightOneSegment) {
        double adjustedY = Position.cartesianToCanvasY(coord, view) - heightOneSegment;
        
        // draw arrow
        Image arrow = getArrow();
        double arrowHeight = ARROW_LENGTH;
        double arrowWidth = arrowHeight * (arrow.getWidth() / arrow.getHeight());
        double arrowCenteredX = x + widthOneSegment/2 - arrowWidth/2;
        double arrowY = adjustedY;
        if (upperBound) {
            arrowY += heightOneSegment + ARROW_BUFFER;
        } else {
            arrowY -= arrowHeight + ARROW_BUFFER;
        }
        gc.drawImage(arrow, arrowCenteredX, arrowY, arrowWidth, arrowHeight);
        
        gc.drawImage(orientation.image, x, adjustedY, widthOneSegment, heightOneSegment);
    }
    
    private void drawVertical(double y, GraphicsContext gc, RanchView view,
                              double widthOneSegment, double heightOneSegment) {
        double adjustedX = Position.cartesianToCanvasX(coord, view) - (widthOneSegment / 2);
        double adjustedY = y - heightOneSegment;
        
        // draw arrow
        Image arrow = getArrow();
        double arrowWidth = ARROW_LENGTH;
        double arrowHeight = arrowWidth / (arrow.getWidth() / arrow.getHeight());
        double arrowX = adjustedX;
        if (upperBound) {
            arrowX -= arrowWidth + ARROW_BUFFER;
        } else {
            arrowX += widthOneSegment + ARROW_BUFFER;
        }
        double arrowCenteredY = y + heightOneSegment/2 - arrowHeight/2;
        gc.drawImage(arrow, arrowX, arrowCenteredY, arrowWidth, arrowHeight);
        
        gc.drawImage(orientation.image, adjustedX, adjustedY, widthOneSegment, heightOneSegment);
    }
    
    private boolean isOnScreen(Canvas canvas, RanchView view, double buffer) {
        if (orientation == Orientation.VERTICAL) {
            double canvasX = Position.cartesianToCanvasX(coord, view);
            return canvasX >= -buffer && canvasX <= canvas.getWidth() + buffer;
        } else {
            double canvasY = Position.cartesianToCanvasY(coord, view);
            return canvasY >= -buffer && canvasY <= canvas.getHeight() + buffer;
        }
    }
    
    private Image getArrow() {
        // Use the particular order of the arrow images list to hack with the domain
        int index = 0;
        if (orientation == Orientation.HORIZONTAL) index += 4;
        if (upperBound) index += 2;
        if (inclusive) index++;
        return ARROWS[index];
    }
    
    enum Orientation {
        
        VERTICAL("file:src/assets/fence_vertical.png", 1.0),
        HORIZONTAL("file:src/assets/fence_horizontal.png", 0.65);
        
        private final Image image;
        private final double scaleFactor; // adjust to make vertical and horizontal fences look the same size
        
        Orientation(String url, double scaleFactor) {
            image = new Image(url);
            this.scaleFactor = scaleFactor;
        }
        
    }
    
}