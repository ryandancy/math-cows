package ca.keal.raomk.ranch;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * A class representing the roll-your-own "play again" button. Please register with canvases as an onClick listener.
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
class PlayAgainButton implements EventHandler<MouseEvent> {
    
    private static final Image IMAGE = new Image("file:src/assets/play_again.png");
    
    private static final double BOTTOM_BUFFER = 15;
    private static final double HEIGHT = 50;
    private static final double WIDTH;
    static {
        WIDTH = HEIGHT * (IMAGE.getWidth() / IMAGE.getHeight());
    }
    
    private final Runnable onClick;
    
    @Getter private boolean active = false;
    
    public void activate() {
        active = true;
    }
    
    /** Draw the button, but only when it's active. */
    public void drawIfActive(Canvas canvas) {
        if (!active) return;
        
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(IMAGE, canvas.getWidth()/2 - WIDTH/2, canvas.getHeight() - HEIGHT - BOTTOM_BUFFER, WIDTH, HEIGHT);
    }
    
    @Override
    public void handle(MouseEvent event) {
        // Ignore all non-click events, ignore if not active
        if (!active || event.getEventType() != MouseEvent.MOUSE_CLICKED) return;
        
        // Source has to be a canvas, otherwise this is registered on the wrong thing
        if (!(event.getSource() instanceof Canvas)) {
            throw new IllegalStateException("PlayAgainButton registered on a Node that isn't a Canvas!");
        }
        Canvas canvas = (Canvas) event.getSource();
        
        // Detect if the click was on the button
        double minX = canvas.getWidth()/2 - WIDTH/2;
        double maxX = canvas.getWidth()/2 + WIDTH/2;
        double minY = canvas.getHeight() - HEIGHT - BOTTOM_BUFFER;
        double maxY = canvas.getHeight() - BOTTOM_BUFFER;
        if (minX <= event.getX() && event.getX() <= maxX && minY <= event.getY() && event.getY() <= maxY) {
            active = false; // hide button
            onClick.run();
            event.consume();
        }
    }
    
}