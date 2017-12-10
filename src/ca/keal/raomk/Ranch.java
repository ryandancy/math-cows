package ca.keal.raomk;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

/**
 * Represents Gumdrop Joe's ranch where he raises his math cows. It's a cartesian plane.
 */
public class Ranch {
    
    private static final double GAP_BETWEEN_GRID_LINES = 30;
    
    private static final Color BACKGROUND_COLOR = Color.rgb(0x1e, 0x82, 0x00);
    private static final Color GRID_LINE_COLOR = Color.rgb(0x0f, 0x50, 0x00);
    private static final Color TEXT_COLOR = Color.BLACK;
    
    private Canvas layerBg; // background color, axes, grid, etc.
    private Canvas layer1; // probably cows
    private Canvas layer2; // probably fences
    
    private double offsetX = 0;
    private double offsetY = 0;
    
    public Ranch(Canvas layerBg, Canvas layer1, Canvas layer2) {
        this.layerBg = layerBg;
        this.layer1 = layer1;
        this.layer2 = layer2;
        
        redraw();
    
        // using layerBg as representative of layers as hack to draw the background correctly
        // height may change during drawing so this ensures proper drawing of the background
        layerBg.heightProperty().addListener((prop, oldHeight, newHeight) -> redraw());
    }
    
    public void shiftBy(double shiftX, double shiftY) {
        offsetX += shiftX;
        offsetY += shiftY;
        redraw();
    }
    
    private void redraw() {
        drawBackground();
    }
    
    private void drawBackground() {
        GraphicsContext gc = layerBg.getGraphicsContext2D();
        
        // Background
        gc.setFill(BACKGROUND_COLOR);
        gc.fillRect(0, 0, layerBg.getWidth(), layerBg.getHeight());
        
        // The coordinates of the origin - TODO make these dynamic if scrolling/dragging
        double originX = layerBg.getWidth() / 2 + offsetX;
        double originY = layerBg.getHeight() / 2 + offsetY;
        
        // Grid lines + numbers
        gc.setLineWidth(1);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.TOP);
        
        // vertical lines for x > 0
        int i = 1;
        for (double x = originX + GAP_BETWEEN_GRID_LINES; x < layerBg.getWidth(); x += GAP_BETWEEN_GRID_LINES) {
            gc.setStroke(GRID_LINE_COLOR);
            gc.strokeLine(x, 0, x, layerBg.getHeight());
            gc.setStroke(TEXT_COLOR);
            gc.strokeText(Integer.toString(i), x, originY);
            i++;
        }
        
        // vertical lines for x < 0
        i = -1;
        for (double x = originX - GAP_BETWEEN_GRID_LINES; x > 0; x -= GAP_BETWEEN_GRID_LINES) {
            gc.setStroke(GRID_LINE_COLOR);
            gc.strokeLine(x, 0, x, layerBg.getHeight());
            gc.setStroke(TEXT_COLOR);
            gc.strokeText(Integer.toString(i), x, originY);
            i--;
        }
        
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);
        
        // horizontal lines for y > 0 (note the JavaFX coordinate grid increases downwards)
        i = -1;
        for (double y = originY + GAP_BETWEEN_GRID_LINES; y < layerBg.getHeight(); y += GAP_BETWEEN_GRID_LINES) {
            gc.setStroke(GRID_LINE_COLOR);
            gc.strokeLine(0, y, layerBg.getWidth(), y);
            gc.setStroke(TEXT_COLOR);
            gc.strokeText(Integer.toString(i), originX + 3, y);
            i--;
        }
        
        // horizontal lines for y < 0
        i = 1;
        for (double y = originY - GAP_BETWEEN_GRID_LINES; y > 0; y -= GAP_BETWEEN_GRID_LINES) {
            gc.setStroke(GRID_LINE_COLOR);
            gc.strokeLine(0, y, layerBg.getWidth(), y);
            gc.setStroke(TEXT_COLOR);
            gc.strokeText(Integer.toString(i), originX + 3, y);
            i++;
        }
    
        // Axes
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.strokeLine(0, originY, layerBg.getWidth(), originY); // x-axis
        gc.strokeLine(originX, 0, originX, layerBg.getHeight()); // y-axis
    }
    
}