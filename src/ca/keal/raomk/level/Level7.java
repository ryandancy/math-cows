package ca.keal.raomk.level;

import ca.keal.raomk.Utils;
import ca.keal.raomk.dr.DomainRange;
import ca.keal.raomk.dr.Interval;
import ca.keal.raomk.ranch.Cow;
import ca.keal.raomk.ranch.Position;
import ca.keal.raomk.ranch.Ranch;
import javafx.scene.image.Image;

// Sinusoidal function
public class Level7 extends Level {
    
    private static final double AXIS = 3;
    private static final double AMPLITUDE = 4;
    private static final double PERIOD = 24;
    
    public Level7() {
        super(null, new DomainRange(new Interval(AXIS - AMPLITUDE, true, AXIS + AMPLITUDE, true)));
    }
    
    @Override
    public void init(Ranch ranch) {
        ranch.gumdropJoeQueue(true,
                "Oh look oh look, it's not just a line! The math cows are forming a function like sine!",
                "In case you were wondering, I'm sure you'll agree: the function is y = 4sin(15x) + 3."
        );
        
        for (int x = -500; x <= 500; x++) {
            double y = sinusoidalFunction(x);
            ranch.addCow(new Cow(new Position(x, y), getCow(y)));
        }
    }
    
    private static double sinusoidalFunction(double x) {
        return AMPLITUDE * Math.sin((2*Math.PI / PERIOD) * x) + AXIS;
    }
    
    private static Image getCow(double y) {
        if (Utils.approxEqual(y, AXIS - AMPLITUDE) || Utils.approxEqual(y, AXIS + AMPLITUDE)) {
            // dins on the max and min
            return Cow.DIN;
        } else if (Utils.approxEqual(y, AXIS)) {
            // surfs in the middle
            return Cow.SURF;
        } else {
            // all others normal
            return Cow.NORMAL;
        }
    }
    
}