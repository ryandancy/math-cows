package ca.keal.raomk;

import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class Controller {
    
    // padding between the canvases and the D/R grid
    private static final int CANVAS_DRGRID_PADDING = 20;
    
    // Canvas layers
    @FXML private Canvas layerBg;
    @FXML private Canvas layer1;
    @FXML private Canvas layer2;
    
    // The domain and range entry grid
    @FXML private GridPane drgrid;
    @FXML private TextField domainTextBox;
    @FXML private TextField rangeTextBox;
    
    private Ranch ranch;
    
    @FXML
    public void initialize() {
        setupExpandNodes();
        ranch = new Ranch(layerBg, layer1, layer2);
    }
    
    // Fix expansion of nodes that can't be expanded via FXML
    private void setupExpandNodes() {
        // Let the domain and range text boxes expand across the bottom of the screen
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        drgrid.getColumnConstraints().addAll(col1, col2);
    
        // Expand the canvas layers manually
        Canvas[] layers = {layerBg, layer1, layer2};
        // workaround for Main.HEIGHT - drgrid.height - CANVAS_DRGRID_PADDING
        DoubleBinding heightBinding = drgrid.heightProperty().negate().add(Main.HEIGHT).subtract(CANVAS_DRGRID_PADDING);
        for (Canvas layer : layers) {
            layer.setWidth(Main.WIDTH);
            layer.heightProperty().bind(heightBinding);
        }
    }
    
}