package ca.keal.raomk.ranch;

import ca.keal.raomk.Utils;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
class GumdropJoe {
    
    private static final Image IMAGE = Utils.getImageAsset("gumdrop_joe");
    
    private static final double BUFFER = 10; // the buffer from the bottom left to draw
    private static final double WIDTH = 150; // width of the final GDJ image - height will be calculated
    
    // Constants for drawing the speech bubble
    private static final double PCT_GDJ_X_SPEECH_BUBBLE_START = 0.8; // % x of GDJ image where speech bubble will start
    private static final double PCT_GDJ_Y_SPEECH_BUBBLE_START = 0.57; // % y of GDJ image where speech bubble will start
    private static final double SPEECH_BUBBLE_TRI_X_OFFSET1 = 30;
    private static final double SPEECH_BUBBLE_TRI_Y_OFFSET1 = 50;
    private static final double SPEECH_BUBBLE_TRI_X_OFFSET2 = 60;
    private static final double SPEECH_BUBBLE_TRI_Y_OFFSET2 = 40;
    private static final double SPEECH_BUBBLE_CIRCLE_X_OFFSET = 90;
    private static final double SPEECH_BUBBLE_CIRCLE_Y_OFFSET = 75;
    private static final double SPEECH_BUBBLE_CIRCLE_RADIUS = 80;
    private static final double APPROX_HEIGHT_ONE_CHARACTER = 12.5;
    private static final int MAX_CHARACTERS_BETWEEN_BREAKS = 18;
    
    // Using cosine law with isosceles triangle: x^2 = 2r^2 - 2r^2cos(theta)
    private static final double MAX_TEXT_WIDTH = Math.sqrt(
            2*SPEECH_BUBBLE_CIRCLE_RADIUS*SPEECH_BUBBLE_CIRCLE_RADIUS*(1 - Math.cos(2*Math.PI/3)));
    
    private double aspectRatio;
    
    @Getter private String text;
    private int lines = 0;
    
    GumdropJoe() {
        aspectRatio = IMAGE.getWidth() / IMAGE.getHeight();
    }
    
    void setText(String text) {
        if (text == null) {
            this.text = null;
            return;
        }
        
        // Wrap the text by replacing ' ' with '\n'
        char[] charArray = text.toCharArray();
        lines = 1;
        for (int i = 0; i < charArray.length; i += MAX_CHARACTERS_BETWEEN_BREAKS) {
            for (int j = i; j > 0 && charArray[j] != '\n'; j--) {
                if (charArray[j] == ' ') {
                    charArray[j] = '\n';
                    lines++;
                    break;
                }
            }
        }
        this.text = String.valueOf(charArray);
    }
    
    void draw(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        // Draw the image
        double height = WIDTH / aspectRatio;
        gc.drawImage(IMAGE, BUFFER, canvas.getHeight() - (height + BUFFER), WIDTH, height);
        
        // Draw a speech bubble if there is text
        if (text != null && !text.isEmpty()) {
            gc.setFill(Color.WHITE);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(3);
            
            // Draw triangle pointing to GDJ's mouth
            double triX = PCT_GDJ_X_SPEECH_BUBBLE_START * WIDTH;
            double triY = canvas.getHeight() - PCT_GDJ_Y_SPEECH_BUBBLE_START * height;
            double[] triXPoints = {triX, triX + SPEECH_BUBBLE_TRI_X_OFFSET1, triX + SPEECH_BUBBLE_TRI_Y_OFFSET1};
            double[] triYPoints = {triY, triY - SPEECH_BUBBLE_TRI_X_OFFSET2, triY - SPEECH_BUBBLE_TRI_Y_OFFSET2}; 
            gc.fillPolygon(triXPoints, triYPoints, 3);
            gc.strokePolygon(triXPoints, triYPoints, 3);
            
            // Draw circle
            double circleX = triX + SPEECH_BUBBLE_CIRCLE_X_OFFSET;
            double circleY = triY - SPEECH_BUBBLE_CIRCLE_Y_OFFSET;
            double circleAdjustedX = circleX - SPEECH_BUBBLE_CIRCLE_RADIUS;
            double circleAdjustedY = circleY - SPEECH_BUBBLE_CIRCLE_RADIUS;
            gc.fillOval(circleAdjustedX, circleAdjustedY, SPEECH_BUBBLE_CIRCLE_RADIUS*2, SPEECH_BUBBLE_CIRCLE_RADIUS*2);
            gc.strokeOval(circleAdjustedX, circleAdjustedY, SPEECH_BUBBLE_CIRCLE_RADIUS*2,
                    SPEECH_BUBBLE_CIRCLE_RADIUS*2);
            
            // Draw text
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.BASELINE);
            gc.setLineWidth(1);
            gc.setFill(Color.BLACK);
            gc.setFont(new Font(12));
            
            // Try to center the text
            double adjustedTextY = circleY - ((lines * APPROX_HEIGHT_ONE_CHARACTER) / 2);
            
            gc.fillText(text, circleX, adjustedTextY, MAX_TEXT_WIDTH);
        }
    }
    
}