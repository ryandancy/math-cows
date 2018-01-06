package ca.keal.raomk;

import ca.keal.raomk.dr.DomainRange;
import ca.keal.raomk.dr.Interval;
import ca.keal.raomk.dr.ParseException;
import ca.keal.raomk.level.Level;
import ca.keal.raomk.level.Level0;
import ca.keal.raomk.level.Level1;
import ca.keal.raomk.level.Level2;
import ca.keal.raomk.level.Level3;
import ca.keal.raomk.level.Level4;
import ca.keal.raomk.level.Level5;
import ca.keal.raomk.level.Level6;
import ca.keal.raomk.level.Level7;
import ca.keal.raomk.ranch.Ranch;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.beans.binding.DoubleBinding;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

import java.util.function.Consumer;

public class Controller {
    
    // padding between the canvases and the D/R grid
    private static final int CANVAS_DRGRID_PADDING = 20;
    
    // for signifying parsing errors on the text boxes
    private static final PseudoClass ERROR_CLASS = PseudoClass.getPseudoClass("error");
    
    // Canvas layers
    @FXML private Canvas layerBg;
    @FXML private Canvas layerCows;
    @FXML private Canvas layerFences;
    @FXML private Canvas layerGumdropJoe;
    
    // The domain and range entry grid
    @FXML private GridPane drgrid;
    @FXML private TextField domainTextBox;
    @FXML private TextField rangeTextBox;
    
    // Victory image
    @FXML private ImageView victoryImg;
    
    private Ranch ranch;
    
    private Level[] levels = {
            new Level0(),
            new Level1(),
            new Level2(),
            new Level3(),
            new Level4(),
            new Level5(),
            new Level6(),
            new Level7(),
    };
    private int levelNum;
    
    // for dragging
    private double startDragX;
    private double startDragY;
    
    @FXML
    public void initialize() {
        setupExpandNodes();
        ranch = new Ranch(this::restart, layerBg, layerCows, layerFences, layerGumdropJoe);
        
        // Allow dragging the ranch
        for (Canvas layer : new Canvas[] {layerBg, layerCows, layerFences, layerGumdropJoe}) {
            layer.setOnMousePressed(event -> {
                startDragX = event.getScreenX();
                startDragY = event.getScreenY();
                event.consume();
            });
            layer.setOnMouseDragged(event -> {
                ranch.shiftBy(event.getScreenX() - startDragX, event.getScreenY() - startDragY);
                startDragX = event.getScreenX();
                startDragY = event.getScreenY();
                event.consume();
            });
        }
        
        restart();
    }
    
    private void restart() {
        levelNum = 0;
        startLevel();
    }
    
    private void startLevel() {
        ranch.clearCows();
        ranch.setDomain(null);
        ranch.setRange(null);
        ranch.gumdropJoeClear();
        ranch.clearGumdropJoeQueue();
        domainTextBox.setText("");
        rangeTextBox.setText("");
        
        if (levelNum >= levels.length) {
            // Last level has been beaten: show the play again button
            ranch.gumdropJoeQueue(false, "Good golly oh my, all the levels you've won! Click below to play again if " 
                    + "you'd like some more math fun.");
            ranch.activatePlayAgainButton();
            return;
        }
        
        levels[levelNum].init(ranch);
    }
    
    @FXML
    public void updateDomain() {
        updateDR(domainTextBox, 'x', ranch::setDomain);
    }
    
    @FXML
    public void updateRange() {
        updateDR(rangeTextBox, 'y', ranch::setRange);
    }
    
    private void updateDR(TextField textBox, char var, Consumer<DomainRange> updater) {
        if (textBox.getText().isEmpty()) {
            // text box was cleared, not an error
            updater.accept(null);
            checkVictory();
            textBox.pseudoClassStateChanged(ERROR_CLASS, false);
            return;
        }
        
        try {
            updater.accept(DomainRange.parse(textBox.getText(), var));
            textBox.pseudoClassStateChanged(ERROR_CLASS, false);
            checkVictory();
        } catch (ParseException | Interval.EqualBoundsException e) {
            updater.accept(null);
            textBox.pseudoClassStateChanged(ERROR_CLASS, true);
        } catch (ArrayIndexOutOfBoundsException e) {
            // checkVictory() threw because we're past the last level, it's fine so ignore it
        }
    }
    
    private void checkVictory() {
        if (areDRsEqual(ranch.getDomain(), levels[levelNum].getVictoryDomain())
                && areDRsEqual(ranch.getRange(), levels[levelNum].getVictoryRange())) {
            // Victory - animate/show image, wait for 3 seconds, then unanimate/hide image and proceed
            
            // In animation - come up from bottom and overshoot
            Path path = new Path();
            path.getElements().addAll(
                    new MoveTo(Main.WIDTH/2 - 190, Main.HEIGHT + 300),
                    new LineTo(Main.WIDTH/2 - 190, Main.HEIGHT/2 - 110)
            );
            PathTransition anim = new PathTransition();
            anim.setPath(path);
            anim.setNode(victoryImg);
            anim.setInterpolator(Interpolator.EASE_OUT);
            anim.setDuration(Duration.millis(1500));
            
            // Play the animation
            victoryImg.setVisible(true);
            anim.play();
            
            // Wait for 3 seconds after start of animation
            Task<Void> sleeper = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(4000);
                    return null;
                }
                
                @Override
                protected void succeeded() {
                    // Reverse the animation, play it double speed!
                    anim.setRate(-2);
                    anim.setOnFinished(event -> {
                        // Hide image, go to next level
                        victoryImg.setVisible(false);
                        levelNum++;
                        startLevel();
                    });
                    anim.play();
                }
            };
            new Thread(sleeper).start();
        }
    }
    
    private boolean areDRsEqual(DomainRange one, DomainRange two) {
        if (one == null) return two == null;
        return one.equals(two);
    }
    
    // Fix expansion of nodes that can't be expanded via FXML
    private void setupExpandNodes() {
        // Let the domain and range text boxes expand across the bottom of the screen
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setHgrow(Priority.ALWAYS);
        drgrid.getColumnConstraints().addAll(col1, col2, col3);
    
        // Expand the canvas layers manually
        Canvas[] layers = {layerBg, layerCows, layerFences, layerGumdropJoe};
        // workaround for Main.HEIGHT - drgrid.height - CANVAS_DRGRID_PADDING
        DoubleBinding heightBinding = drgrid.heightProperty().negate().add(Main.HEIGHT).subtract(CANVAS_DRGRID_PADDING);
        for (Canvas layer : layers) {
            layer.setWidth(Main.WIDTH);
            layer.heightProperty().bind(heightBinding);
        }
    }
    
}