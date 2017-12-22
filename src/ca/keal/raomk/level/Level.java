package ca.keal.raomk.level;

import ca.keal.raomk.dr.DomainRange;
import ca.keal.raomk.ranch.Ranch;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A level in the game.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Level {
    
    @Getter private final DomainRange victoryDomain;
    @Getter private final DomainRange victoryRange;
    
    /**
     * Initialize the level. Place cows, setup Gumdrop Joe, etc.
     */
    public abstract void init(Ranch ranch);
    
}