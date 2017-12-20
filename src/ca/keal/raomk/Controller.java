package ca.keal.raomk;

import javafx.beans.binding.DoubleBinding;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

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
    
    private Ranch ranch;
    
    private Level[] levels = {
            new Level0()
    };
    private int levelNum;
    
    // for dragging
    private double startDragX;
    private double startDragY;
    
    @FXML
    public void initialize() {
        setupExpandNodes();
        ranch = new Ranch(layerBg, layerCows, layerFences, layerGumdropJoe);
        
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
    
    // In a separate method so as to allow restarting at the end if we decide to implement that
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
        
        if (levelNum >= levels.length) {
            // Beyond the last level: TODO find something to do beyond the last level
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
            textBox.pseudoClassStateChanged(ERROR_CLASS, true);
            return;
        }
        
        try {
            updater.accept(DomainRange.parse(textBox.getText(), var));
            textBox.pseudoClassStateChanged(ERROR_CLASS, false);
            checkVictory();
        } catch (ParseException | Interval.EqualBoundsException e) {
            updater.accept(null);
            textBox.pseudoClassStateChanged(ERROR_CLASS, true);
        }
    }
    
    private void checkVictory() {
        if (areDRsEqual(ranch.getDomain(), levels[levelNum].getVictoryDomain())
                && areDRsEqual(ranch.getRange(), levels[levelNum].getVictoryRange())) {
            // Victory: TODO do some animation or something to signify victory
            System.out.println("Victory!");
            levelNum++;
            startLevel();
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