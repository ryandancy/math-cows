package ca.keal.raomk.level;

import ca.keal.raomk.dr.DomainRange;
import ca.keal.raomk.dr.Interval;
import ca.keal.raomk.ranch.Cow;
import ca.keal.raomk.ranch.Position;
import ca.keal.raomk.ranch.Ranch;
import javafx.scene.image.Image;

import java.util.Arrays;

// Exponential function
public class Level8 extends Level {
    
    private static final double FACTOR = 1.3;
    private static final double MULT_FACTOR = 3;
    private static final double XSHIFT = 2.5;
    private static final double YSHIFT = -1;
    
    private static final Interval[] INTERVALS = {
            new Interval(Interval.Bound.NEG_INFINITY, new Interval.Bound(-4, true)),
            new Interval(-1, true, 3, true),
            new Interval(new Interval.Bound(5, true), Interval.Bound.INFINITY)
    };
    
    public Level8() {
        super(new DomainRange(INTERVALS), DomainRange.parseStatic("y>" + YSHIFT, 'y'));
    }
    
    @Override
    public void init(Ranch ranch) {
        ranch.gumdropJoeQueue(true,
                "Oh good golly, there's something, yup! An exponential curve all sliced up!",
                "There's a couple things you must remember to make sense: the spider-cows can touch the math fence;",
                "BUT! However low the leftmost cows appear, they can never touch their asymptote right here.",
                "And a final thing just before you're done: the cows' function is y = 3(1.3)^(x-2.5) - 1."
        );
        
        for (double x = -500; x <= 500; x++) {
            final double finalX = x; // frikkin java
            if (Arrays.stream(INTERVALS).anyMatch(interval -> interval.contains(finalX))) {
                double y = exponentialFunction(x);
                if (y < 500) {
                    ranch.addCow(new Cow(new Position(x, y), getCow(x)));
                }
            }
        }
    }
    
    private static double exponentialFunction(double x) {
        return MULT_FACTOR * Math.pow(FACTOR, x - XSHIFT) + YSHIFT;
    }
    
    private static Image getCow(double x) {
        if (Arrays.stream(INTERVALS).anyMatch(interval -> x == interval.getUpperBound().getNumber()
                || x == interval.getLowerBound().getNumber())) {
            // Spider cows at ends of segments
            return Cow.SPIDER;
        } else if (Math.random() > 0.975) {
            // Rare surf
            return Cow.SURF;
        } else {
            return Cow.NORMAL;
        }
    }
    
}