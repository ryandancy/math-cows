package ca.keal.raomk.level;

import ca.keal.raomk.dr.DomainRange;
import ca.keal.raomk.dr.Interval;
import ca.keal.raomk.ranch.Cow;
import ca.keal.raomk.ranch.Position;
import ca.keal.raomk.ranch.Ranch;
import javafx.scene.image.Image;

// Root curve restricted
public class Level6 extends Level {
    
    private static final double MIN_X = 4;
    private static final double MAX_X = 20;
    private static final double Y_SHIFT = 3;
    
    public Level6() {
        super(new DomainRange(new Interval(MIN_X, true, MAX_X, true)),
                new DomainRange(new Interval(rootCurveNegative(MAX_X), true, rootCurvePositive(MAX_X), true)));
    }
    
    @Override
    public void init(Ranch ranch) {
        ranch.gumdropJoeQueue(true,
                "This root curve ends just to the right. Drag the field over to see the end with your eyesight."
        );
        
        for (double x = MIN_X; x <= MAX_X; x++) {
            double posY = rootCurvePositive(x);
            double negY = rootCurveNegative(x);
            Image cow = getCow(x);
            
            ranch.addCow(new Cow(new Position(x, posY), cow));
            if (negY != posY) {
                ranch.addCow(new Cow(new Position(x, negY), cow));
            }
        }
    }
    
    private static double rootCurvePositive(double x) {
        return Math.sqrt(x - MIN_X) + Y_SHIFT;
    }
    
    private static double rootCurveNegative(double x) {
        return -Math.sqrt(x - MIN_X) + Y_SHIFT;
    }
    
    private static Image getCow(double x) {
        if (x == MIN_X) {
            return Cow.DIN;
        } else if (x == MAX_X) {
            return Cow.SPIDER;
        } else if (Math.random() > 0.8) {
            return Cow.SURF;
        } else {
            return Cow.NORMAL;
        }
    }
    
}