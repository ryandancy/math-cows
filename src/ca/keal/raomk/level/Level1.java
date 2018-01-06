package ca.keal.raomk.level;

import ca.keal.raomk.dr.DomainRange;
import ca.keal.raomk.dr.Interval;
import ca.keal.raomk.ranch.Cow;
import ca.keal.raomk.ranch.Ranch;

public class Level1 extends Level {
    
    private static final Interval X_INTERVAL = new Interval(-4, true, 2, true);
    private static final Interval Y_INTERVAL = new Interval(-9, true, -3, true);
    
    public Level1() {
        super(new DomainRange(X_INTERVAL), new DomainRange(Y_INTERVAL));
    }
    
    @Override
    public void init(Ranch ranch) {
        ranch.gumdropJoeQueue(true,
                "Oh wowza, it looks like you've cleared that level! Here's another one that'll test your mettle.",
                "These cows can't roam all over the screen! Drag the field down to see what I mean.",
                "Fence them in now, fence them in tight! We wouldn't want them to escape, right?",
                "Just one more thing: these cows can touch the fence! Use '<=' or '>=' to be inclusive, hence."
        );
        
        ranch.distributeCows(X_INTERVAL, Y_INTERVAL, () -> Cow.NORMAL);
    }
    
}