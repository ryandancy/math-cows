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
        try {
            updater.accept(DomainRange.parse(textBox.getText(), var));
            textBox.pseudoClassStateChanged(ERROR_CLASS, false);
        } catch (ParseException | Interval.EqualBoundsException e) {
            updater.accept(null);
            if (!textBox.getText().isEmpty()) {
                // the user cleared the text box, not an error
                textBox.pseudoClassStateChanged(ERROR_CLASS, true);
            }
        }
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