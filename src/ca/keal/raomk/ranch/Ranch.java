package ca.keal.raomk.ranch;

import ca.keal.raomk.dr.DomainRange;
import ca.keal.raomk.dr.Interval;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
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
import java.util.function.Supplier;

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
    
    private static final double COW_DISTRIBUTION_DENSITY = 0.65; // cows per 1x1 box
    private static final double COW_DISTRIBUTION_BUFFER = 0.5;
    private static final Random COW_DISTRIBUTION_RANDOM = new Random();
    
    private Flower[] flowers = new Flower[80];
    private static final double MAX_FLOWER_DIST = 40;
    
    @Getter private DomainRange domain = null;
    @Getter private DomainRange range = null;
    private List<Fence> fences = new ArrayList<>();
    
    private List<Cow> cows = new ArrayList<>();
    
    private GumdropJoe gumdropJoe = new GumdropJoe();
    private Queue<String> gumdropJoeQueue = new ArrayDeque<>();
    private boolean lastGumdropJoeClearable = true;
    
    @Getter private PlayAgainButton playAgainButton;
    
    private Canvas layerBg; // background color, axes, grid, etc.
    private Canvas layerCows;
    private Canvas layerFences;
    private Canvas layerGumdropJoe;
    
    @Getter private RanchView view;
    
    public Ranch(Runnable restartFunc, Canvas layerBg, Canvas layerCows, Canvas layerFences, Canvas layerGumdropJoe) {
        this.layerBg = layerBg;
        this.layerCows = layerCows;
        this.layerFences = layerFences;
        this.layerGumdropJoe = layerGumdropJoe;
        
        view = new RanchView(GRID_LINE_GAP, layerBg);
        
        playAgainButton = new PlayAgainButton(restartFunc);
        
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
            layer.setOnMouseClicked(event -> {
                if (event.isStillSincePress()) {
                    processGumdropJoeQueue();
                    playAgainButton.handle(event);
                }
            });
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
    
    /** Distribute cows throughout the range defined by xSpread/ySpread. */
    public void distributeCows(Interval xSpread, Interval ySpread, Supplier<Image> cowImageSupplier) {
        double left = xSpread.getLowerBound().getNumber() + COW_DISTRIBUTION_BUFFER;
        double right = xSpread.getUpperBound().getNumber() - COW_DISTRIBUTION_BUFFER;
        double bottom = ySpread.getLowerBound().getNumber() + COW_DISTRIBUTION_BUFFER;
        double top = ySpread.getUpperBound().getNumber() - COW_DISTRIBUTION_BUFFER;
        
        double width = right - left;
        double height = top - bottom;
        
        // Put at least one cow on corners - randomly choose between top-left/bottom-right and top-right/bottom-left
        // This ensures that the domain/range is always respected
        Position bottomCornerCowPos, topCornerCowPos;
        if (COW_DISTRIBUTION_RANDOM.nextBoolean()) {
            topCornerCowPos = new Position(left, top);
            bottomCornerCowPos = new Position(right, bottom);
        } else {
            topCornerCowPos = new Position(right, top);
            bottomCornerCowPos = new Position(left, bottom);
        }
        cows.add(new Cow(bottomCornerCowPos, cowImageSupplier.get()));
        cows.add(new Cow(topCornerCowPos, cowImageSupplier.get()));
        
        // Find the area of the intervals, use that to calculate number of cows based on density
        double area = width * height;
        int numCows = (int) (COW_DISTRIBUTION_DENSITY * area);
        
        // Randomly distribute the cows - TODO maybe use the Halton sequence or something else?
        for (int i = 0; i < numCows; i++) {
            cows.add(new Cow(new Position(
                    width * COW_DISTRIBUTION_RANDOM.nextDouble() + left,
                    height * COW_DISTRIBUTION_RANDOM.nextDouble() + bottom
            ), cowImageSupplier.get()));
        }
        
        drawCows();
    }
    
    public void clearCows() {
        cows.clear();
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
    
    public void clearGumdropJoeQueue() {
        gumdropJoeQueue.clear();
        gumdropJoeClear();
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
    
    public void activatePlayAgainButton() {
        playAgainButton.activate();
        drawGumdropJoe();
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
        playAgainButton.drawIfActive(layerGumdropJoe);
    }
    
}