package ca.keal.raomk.level;

import ca.keal.raomk.dr.DomainRange;
import ca.keal.raomk.dr.Interval;
import ca.keal.raomk.ranch.Cow;
import ca.keal.raomk.ranch.Ranch;

// Mostly a test level, rectangle, introduce domain/range boxes
public class Level0 extends Level {
    
    private static Interval X_INTERVAL = new Interval(1, false, 5, false);
    private static Interval Y_INTERVAL = new Interval(0, false, 4, false);
    
    public Level0() {
        super(new DomainRange(X_INTERVAL), new DomainRange(Y_INTERVAL));
    }
    
    @Override
    public void init(Ranch ranch) {
        ranch.gumdropJoeQueue(true,
                "Oh yippidy doo, oh yippidy doh! Hi everyone, I'm Gumdrop Joe!",
                "Now these here math cows, they like to roam. I need your help so they don't run away from home.",
                "Look to the bottom of the screen, my dude. Type some domains and some ranges to restrict"
                    + " the math cows' moves. \u2193\u2193",
                "Make sure the cows are compressed as tight as can be! Cows can't have too much freedom, you see.",
                "Now I'll leave you with this, just before you commence: these cows can get close, but never touch" 
                    + " the math fence."
        );
        
        ranch.distributeCows(1.3, X_INTERVAL, Y_INTERVAL, () -> Cow.NORMAL);
    }
    
}