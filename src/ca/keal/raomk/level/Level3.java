package ca.keal.raomk.level;

import ca.keal.raomk.dr.DomainRange;
import ca.keal.raomk.dr.Interval;
import ca.keal.raomk.ranch.Cow;
import ca.keal.raomk.ranch.Ranch;

public class Level3 extends Level {
    
    private static final int MAX_Y = 5;
    
    public Level3() {
        super(null, DomainRange.parseStatic("y<" + MAX_Y, 'y'));
    }
    
    @Override
    public void init(Ranch ranch) {
        ranch.gumdropJoeQueue(true,
                "These cows are even more grown-up! They can go left, right, and down, but just not up.",
                "\"Don't go up too far!\" an elder cow once lied. They can't even touch the fence, they're so " 
                        + "terrified!",
                "And just between us, on a secret I'll let you in: it's possible that the domain or range boxes need " 
                        + "not be filled in."
        );
        
        ranch.distributeCows(0.4, new Interval(-500, true, 500, true), new Interval(-500, true, MAX_Y, true),
                () -> Math.random() < 0.7 ? Cow.NORMAL : Cow.SURF);
    }
    
}