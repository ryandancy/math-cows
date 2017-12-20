package ca.keal.raomk;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Represents Gumdrop Joe's ranch where he raises his math cows. It's a cartesian plane.
 */
@EqualsAndHashCode
@ToString
public class Ranch {
    
    private static final double GRID_LINE_GAP = 30;
    private static final double DRAWING_BUFFER = 10; // the buffer around the screen to be drawn to increase fluidity
    
    private static final Color BACKGROUND_COLOR = Color.rgb(0x1e, 0x82, 0x00);
    private static final Color GRID_LINE_COLOR = Color.rgb(0x0f, 0x50, 0x00);
    private static final Color TEXT_COLOR = Color.BLACK;
    
    private Flower[] flowers = new Flower[80];
    private static final double MAX_FLOWER_DIST = 40;
    
    @Getter private DomainRange domain = null;
    @Getter private DomainRange range = null;
    private List<Fence> fences = new ArrayList<>();
    
    private List<Cow> cows = new ArrayList<>();
    
    private GumdropJoe gumdropJoe = new GumdropJoe();
    private Queue<String> gumdropJoeQueue = new ArrayDeque<>();
    private boolean lastGumdropJoeClearable = true;
    
    private Canvas layerBg; // background color, axes, grid, etc.
    private Canvas layerCows;
    private Canvas layerFences;
    private Canvas layerGumdropJoe;
    
    @Getter private RanchView view;
    
    public Ranch(Canvas layerBg, Canvas layerCows, Canvas layerFences, Canvas layerGumdropJoe) {
        this.layerBg = layerBg;
        this.layerCows = layerCows;
        this.layerFences = layerFences;
        this.layerGumdropJoe = layerGumdropJoe;
        
        view = new RanchView(GRID_LINE_GAP, layerBg);
        
        // Generate flowers
        Random random = new Random();
        for (int i = 0; i < flowers.length; i++) {
            flowers[i] = Flower.random(new Position(
                    random.nextDouble() * MAX_FLOWER_DIST * (random.nextBoolean() ? 1 : -1),
                    random.nextDouble() * MAX_FLOWER_DIST * (random.nextBoolean() ? 1 : -1)), random);
        }
        
        redraw();
        
        // using layerBg as representative of layers as hack to draw the background correctly
        // height may change during drawing so this ensures proper drawing of the background
        layerBg.heightProperty().addListener((prop, oldHeight, newHeight) -> redraw());
        
        // Add listeners to all layers
        for (Canvas layer : new Canvas[] {layerBg, layerCows, layerFences, layerGumdropJoe}) {
            // Process the Gumdrop Joe queue on click
            layer.setOnMouseClicked(event -> processGumdropJoeQueue());
        }
    }
    
    public void addCow(@NonNull Cow cow) {
        cows.add(cow);
        drawCows();
    }
    
    public void addCows(Collection<Cow> cows) {
        this.cows.addAll(cows);
        drawCows();
    }
    
    public void setDomain(DomainRange domain) {
        this.domain = domain;
        updateFences();
    }
    
    public void setRange(DomainRange range) {
        this.range = range;
        updateFences();
    }
    
    private void updateFences() {
        fences = new ArrayList<>(); // clear fences
        
        // Domain fences are vertical, range fences are horizontal
        if (domain != null) {
            domain.getIntervals().stream()
                    .map(interval -> Fence.intervalToFences(interval, Fence.Orientation.VERTICAL))
                    .forEach(fences::addAll);
        }
        if (range != null) {
            range.getIntervals().stream()
                    .map(interval -> Fence.intervalToFences(interval, Fence.Orientation.HORIZONTAL))
                    .forEach(fences::addAll);
        }
        
        drawFences();
    }
    
    public void gumdropJoeSay(String text) {
        gumdropJoe.setText(text);
        drawGumdropJoe();
    }
    
    public void gumdropJoeSay(String text, boolean lastClearable) {
        lastGumdropJoeClearable = lastClearable;
        gumdropJoeSay(text);
    }
    
    public void gumdropJoeClear() {
        lastGumdropJoeClearable = true;
        gumdropJoeSay(null);
        drawGumdropJoe();
    }
    
    public void gumdropJoeQueue(boolean lastClearable, String... textsToQueue) {
        lastGumdropJoeClearable = lastClearable;
        
        // Add "(click)" to all texts to queue, including last if it's clearable
        for (int i = 0; i < (lastClearable ? textsToQueue.length : textsToQueue.length - 1); i++) {
            textsToQueue[i] += " (click)";
        }
        
        gumdropJoeQueue.addAll(Arrays.asList(textsToQueue));
        processGumdropJoeQueue();
    }
    
    // Pop the first element of the Gumdrop Joe queue, gumdropJoeSay() it
    private void processGumdropJoeQueue() {
        if (gumdropJoeQueue.isEmpty()) {
            if (lastGumdropJoeClearable) {
                gumdropJoeClear();
            }
            return;
        }
        
        gumdropJoeSay(gumdropJoeQueue.remove());
    }
    
    public void shiftBy(double shiftX, double shiftY) {
        view.shift(shiftX, shiftY);
        redraw();
    }
    
    private void redraw() {
        drawBackground();
        drawCows();
        drawFences();
        drawGumdropJoe();
    }
    
    private void drawBackground() {
        GraphicsContext gc = layerBg.getGraphicsContext2D();
        
        // Background
        gc.setFill(BACKGROUND_COLOR);
        gc.fillRect(0, 0, layerBg.getWidth(), layerBg.getHeight());
        
        // Flowers
        for (Flower flower : flowers) {
            flower.draw(layerBg, view);
        }
        
        // Grid lines + numbers
        gc.setLineWidth(1);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.TOP);
        
        // vertical lines for x > 0
        int i = 1;
        for (double x = view.getOriginX() + view.getGridLineGap(); x < layerBg.getWidth() + DRAWING_BUFFER;
             x += view.getGridLineGap()) {
            gc.setStroke(GRID_LINE_COLOR);
            gc.strokeLine(x, 0, x, layerBg.getHeight());
            gc.setStroke(TEXT_COLOR);
            gc.strokeText(Integer.toString(i), x, view.getOriginY());
            i++;
        }
        
        // vertical lines for x < 0
        i = -1;
        for (double x = view.getOriginX() - view.getGridLineGap(); x > -DRAWING_BUFFER; x -= view.getGridLineGap()) {
            gc.setStroke(GRID_LINE_COLOR);
            gc.strokeLine(x, 0, x, layerBg.getHeight());
            gc.setStroke(TEXT_COLOR);
            gc.strokeText(Integer.toString(i), x, view.getOriginY());
            i--;
        }
        
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);
        
        // horizontal lines for y > 0 (note the JavaFX coordinate grid increases downwards)
        i = -1;
        for (double y = view.getOriginY() + view.getGridLineGap(); y < layerBg.getHeight() + DRAWING_BUFFER;
             y += view.getGridLineGap()) {
            gc.setStroke(GRID_LINE_COLOR);
            gc.strokeLine(0, y, layerBg.getWidth(), y);
            gc.setStroke(TEXT_COLOR);
            gc.strokeText(Integer.toString(i), view.getOriginX() + 3, y);
            i--;
        }
        
        // horizontal lines for y < 0
        i = 1;
        for (double y = view.getOriginY() - view.getGridLineGap(); y > -DRAWING_BUFFER; y -= view.getGridLineGap()) {
            gc.setStroke(GRID_LINE_COLOR);
            gc.strokeLine(0, y, layerBg.getWidth(), y);
            gc.setStroke(TEXT_COLOR);
            gc.strokeText(Integer.toString(i), view.getOriginX() + 3, y);
            i++;
        }
        
        // Axes
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.strokeLine(0, view.getOriginY(), layerBg.getWidth(), view.getOriginY()); // x-axis
        gc.strokeLine(view.getOriginX(), 0, view.getOriginX(), layerBg.getHeight()); // y-axis
    }
    
    private void drawCows() {
        layerCows.getGraphicsContext2D().clearRect(0, 0, layerCows.getWidth(), layerCows.getHeight());
        cows.forEach(cow -> cow.draw(layerCows, view));
    }
    
    private void drawFences() {
        layerFences.getGraphicsContext2D().clearRect(0, 0, layerFences.getWidth(), layerFences.getHeight());
        fences.forEach(fence -> fence.draw(layerFences, view));
    }
    
    private void drawGumdropJoe() {
        layerGumdropJoe.getGraphicsContext2D().clearRect(0, 0, layerGumdropJoe.getWidth(), layerGumdropJoe.getHeight());
        gumdropJoe.draw(layerGumdropJoe);
    }
    
}