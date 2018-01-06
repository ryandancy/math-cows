package ca.keal.raomk.level;

import ca.keal.raomk.dr.DomainRange;
import ca.keal.raomk.ranch.Cow;
import ca.keal.raomk.ranch.Position;
import ca.keal.raomk.ranch.Ranch;

public class Level5 extends Level {
    
    private static final double ZERO1 = -3;
    private static final double ZERO2 = 3;
    private static final double MAX_Y = 0;
    
    public Level5() {
        super(DomainRange.parseStatic(String.format("x<=%s or x>=%s", ZERO1, ZERO2), 'x'),
                DomainRange.parseStatic("y<=" + MAX_Y, 'y'));
    }
    
    @Override
    public void init(Ranch ranch) {
        ranch.gumdropJoeQueue(false,
                "Restricted, it seems like this parabola has been! You can tell by the restricting spider-cows that " 
                        + "can be seen.",
                "The spider-cows can touch the fences, they can! Factor those spider-cows into your plan.",
                "A hint: if you need two fences in opposite directions, use \"or\" to separate them. " 
                        + "You'll make the connection."
        );
    
        for (int x = -500; x < 500; x++) {
            double y = quadraticFunction(x);
            if (y <= MAX_Y && y > -500) {
                ranch.addCow(new Cow(new Position(x, y), y == MAX_Y ? Cow.SPIDER : Cow.NORMAL));
            }
        }
    }
    
    private double quadraticFunction(double x) {
        return -0.15 * (x - ZERO1) * (x - ZERO2);
    }
    
}