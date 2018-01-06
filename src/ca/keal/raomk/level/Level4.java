package ca.keal.raomk.level;

import ca.keal.raomk.dr.DomainRange;
import ca.keal.raomk.dr.Interval;
import ca.keal.raomk.ranch.Cow;
import ca.keal.raomk.ranch.Position;
import ca.keal.raomk.ranch.Ranch;

// Parabola opening up
public class Level4 extends Level {
    
    private static final int PARABOLA_OV = -1;
    
    public Level4() {
        super(null, new DomainRange(new Interval(new Interval.Bound(PARABOLA_OV, true), Interval.Bound.INFINITY)));
    }
    
    @Override
    public void init(Ranch ranch) {
        ranch.gumdropJoeQueue(false,
                "Oh hey, watch out you math guy! The cows are forming a parabola all the way to the sky!",
                "Just one sec, a word of warning for you: the cows appear in the middle of their coordinates, true!",
                "I'll tell you something else that's fun: the equation of this parabola is y = 0.2x² - 1."
        );
        
        for (int x = -500; x <= 500; x++) {
            double y = quadraticFunction(x);
            if (y < 500) {
                ranch.addCow(new Cow(new Position(x, y), x == 0 ? Cow.DIN : Cow.NORMAL));
            }
        }
    }
    
    private double quadraticFunction(double x) {
        return 0.2*x*x + PARABOLA_OV;
    }
    
}