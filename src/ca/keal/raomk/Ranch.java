package ca.keal.raomk;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.util.Random;

/**
 * Represents Gumdrop Joe's ranch where he raises his math cows. It's a cartesian plane.
 */
public class Ranch {
    
    private static final double GRID_LINE_GAP = 30;
    private static final double DRAWING_BUFFER = 10; // the buffer around the screen to be drawn to increase fluidity
    
    private static final Color BACKGROUND_COLOR = Color.rgb(0x1e, 0x82, 0x00);
    private static final Color GRID_LINE_COLOR = Color.rgb(0x0f, 0x50, 0x00);
    private static final Color TEXT_COLOR = Color.BLACK;
    
    // positions of flowers, as coordinates on the plane (not as screen/origin coordinates!)
    // alternate between flower1 and flower2
    private double[][] flowerPositions = new double[80][2];
    private static final double MAX_FLOWER_DIST = 40;
    private static final double FLOWER_WIDTH = 33.33;
    private static final double FLOWER_HEIGHT = 30;
    
    private Canvas layerBg; // background color, axes, grid, etc.
    private Canvas layer1; // probably cows
    private Canvas layer2; // probably fences
    
    private Image flower1;
    private Image flower2;
    
    private double offsetX = 0;
    private double offsetY = 0;
    
    public Ranch(Canvas layerBg, Canvas layer1, Canvas layer2) {
        this.layerBg = layerBg;
        this.layer1 = layer1;
        this.layer2 = layer2;
        
        // Generate flower positions
        Random random = new Random();
        for (int i = 0; i < flowerPositions.length; i++) {
            for (int j = 0; j < 2; j++) {
                flowerPositions[i][j] = random.nextDouble() * MAX_FLOWER_DIST * (random.nextBoolean() ? 1 : -1);
            }
        }
        
        flower1 = new Image("file:src/assets/flowers.png");
        flower2 = new Image("file:src/assets/flowers2.png");
        
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
        
        // The coordinates of the origin
        double originX = layerBg.getWidth() / 2 + offsetX;
        double originY = layerBg.getHeight() / 2 + offsetY;
        
        // Flowers
        boolean useFlower1 = true;
        for (double[] pos : flowerPositions) {
            double x = pos[0] * GRID_LINE_GAP + originX;
            double y = pos[1] * GRID_LINE_GAP + originY;
            if (x > -FLOWER_WIDTH && x < layerBg.getWidth() && y > -FLOWER_HEIGHT && y < layerBg.getHeight()) {
                gc.drawImage(useFlower1 ? flower1 : flower2, x, y, FLOWER_WIDTH, FLOWER_HEIGHT);
            }
            useFlower1 = !useFlower1;
        }
        
        // Grid lines + numbers
        gc.setLineWidth(1);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.TOP);
        
        // vertical lines for x > 0
        int i = 1;
        for (double x = originX + GRID_LINE_GAP; x < layerBg.getWidth() + DRAWING_BUFFER; x += GRID_LINE_GAP) {
            gc.setStroke(GRID_LINE_COLOR);
            gc.strokeLine(x, 0, x, layerBg.getHeight());
            gc.setStroke(TEXT_COLOR);
            gc.strokeText(Integer.toString(i), x, originY);
            i++;
        }
        
        // vertical lines for x < 0
        i = -1;
        for (double x = originX - GRID_LINE_GAP; x > -DRAWING_BUFFER; x -= GRID_LINE_GAP) {
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
        for (double y = originY + GRID_LINE_GAP; y < layerBg.getHeight() + DRAWING_BUFFER; y += GRID_LINE_GAP) {
            gc.setStroke(GRID_LINE_COLOR);
            gc.strokeLine(0, y, layerBg.getWidth(), y);
            gc.setStroke(TEXT_COLOR);
            gc.strokeText(Integer.toString(i), originX + 3, y);
            i--;
        }
        
        // horizontal lines for y < 0
        i = 1;
        for (double y = originY - GRID_LINE_GAP; y > -DRAWING_BUFFER; y -= GRID_LINE_GAP) {
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