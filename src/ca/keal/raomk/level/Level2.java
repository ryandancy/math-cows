package ca.keal.raomk.level;

import ca.keal.raomk.dr.DomainRange;
import ca.keal.raomk.dr.Interval;
import ca.keal.raomk.ranch.Cow;
import ca.keal.raomk.ranch.Ranch;

// Rectangle infinite to the right, introduce unbounded
public class Level2 extends Level {
    
    private static final int MIN_X = 3;
    private static final Interval Y_INTERVAL = new Interval(-2, true, 2, true);
    
    public Level2() {
        super(DomainRange.parseStatic("x>=" + MIN_X, 'x'), new DomainRange(Y_INTERVAL));
    }
    
    @Override
    public void init(Ranch ranch) {
        ranch.gumdropJoeQueue(true,
                "As you can see, some of these cows can surf. That means they get to cover more turf!",
                "These cows can touch the fences all night, and they can go as far as they want to the right!",
                "So make sure not to put an upper bound on the domain, and then your efforts won't be in vain."
        );
        
        ranch.distributeCows(0.7, new Interval(MIN_X, true, 500, true), Y_INTERVAL,
                () -> Math.random() < 0.75 ? Cow.NORMAL : Cow.SURF);
    }
    
}