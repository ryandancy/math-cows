package ca.keal.raomk.level;

import ca.keal.raomk.Utils;
import ca.keal.raomk.dr.DomainRange;
import ca.keal.raomk.ranch.Cow;
import ca.keal.raomk.ranch.Position;
import ca.keal.raomk.ranch.Ranch;
import javafx.scene.image.Image;

// Tangent graph, one period
public class Level9 extends Level {
    
    private static final double PERIOD = 16;
    private static final double MIN_X = 2;
    private static final double MAX_X = MIN_X + PERIOD;
    
    public Level9() {
        super(DomainRange.parseStatic(MIN_X + "<x<" + MAX_X, 'x'), null);
    }
    
    @Override
    public void init(Ranch ranch) {
        ranch.gumdropJoeQueue(true,
                "You're almost done, just one more level! This is one period of tan's graph, oh mathy webble!",
                "Remember this (not to shove it down your throat): where cows trail off is likely an asymptote.",
                "Both boxes need not be filled in, it won't give you a hex! And the equation? It's y = tan(22.5x)."
        );
        
        // More cows at the edges to better show the asymptote
        for (double x = MIN_X; x <= MAX_X; x += 0.5) maybePlaceCow(ranch, x);
        for (double x = MIN_X; x <= MIN_X + 0.65; x += 0.05) maybePlaceCow(ranch, x);
        for (double x = MAX_X; x >= MAX_X - 0.65; x -= 0.05) maybePlaceCow(ranch, x);
    }
    
    private static void maybePlaceCow(Ranch ranch, double x) {
        double y = tanFunction(x);
        if (!Double.isNaN(y) && y > -500 && y < 500) {
            ranch.addCow(new Cow(new Position(x, y), getCow(y)));
        }
    }
    
    private static double tanFunction(double x) {
        return Math.tan((Math.PI / PERIOD) * (x - MIN_X + PERIOD/2));
    }
    
    private static Image getCow(double y) {
        if (Utils.approxEqual(y, 0)) {
            // surf at the middle
            return Cow.SURF;
        } else {
            // all else normal
            return Cow.NORMAL;
        }
    }
    
}