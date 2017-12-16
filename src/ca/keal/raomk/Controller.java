package ca.keal.raomk;

import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class Controller {
    
    // padding between the canvases and the D/R grid
    private static final int CANVAS_DRGRID_PADDING = 20;
    
    @FXML private Pane root;
    
    // Canvas layers
    @FXML private Canvas layerBg;
    @FXML private Canvas layerCows;
    @FXML private Canvas layerFences;
    
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
        ranch = new Ranch(layerBg, layerCows, layerFences);
        
        // Allow dragging the ranch
        for (Canvas layer : new Canvas[] {layerBg, layerCows, layerFences}) {
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
    
    // Fix expansion of nodes that can't be expanded via FXML
    private void setupExpandNodes() {
        // Let the domain and range text boxes expand across the bottom of the screen
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        drgrid.getColumnConstraints().addAll(col1, col2);
    
        // Expand the canvas layers manually
        Canvas[] layers = {layerBg, layerCows, layerFences};
        // workaround for Main.HEIGHT - drgrid.height - CANVAS_DRGRID_PADDING
        DoubleBinding heightBinding = drgrid.heightProperty().negate().add(Main.HEIGHT).subtract(CANVAS_DRGRID_PADDING);
        for (Canvas layer : layers) {
            layer.setWidth(Main.WIDTH);
            layer.heightProperty().bind(heightBinding);
        }
    }
    
}