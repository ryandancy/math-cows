package ca.keal.raomk;

import lombok.Data;

/**
 * Represents an interval; that is, a range of real numbers, each side being inclusive or exclusive. Used for
 * domain and range. Immutable.
 */
@Data
public class Interval {
    
    private final Bound lowerBound;
    private final Bound upperBound;
    
    /** You can pass lower & upper bounds in whichever order (don't let them be the same though) */
    public Interval(Bound bound1, Bound bound2) throws EqualBoundsException {
        switch (bound1.compareTo(bound2)) {
            case -1:
                // bound1 < bound2
                lowerBound = bound1;
                upperBound = bound2;
                break;
            case 1:
                // bound1 > bound2
                lowerBound = bound2;
                upperBound = bound1;
                break;
            default:
                // bound1 == bound2
                throw new EqualBoundsException("Bounds cannot be equal!");
        }
    }
    
    public boolean contains(double number) {
        return lowerBound.lessThan(number) && upperBound.greaterThan(number);
    }
    
    /**
     * Each bound of an interval - inclusive or exclusive, orderable.
     */
    @Data
    public static class Bound implements Comparable<Bound> {
        
        private final double number;
        private final boolean inclusive;
        
        public boolean lessThan(double other) {
            if (inclusive) {
                return number <= other;
            } else {
                return number < other;
            }
        }
        
        public boolean greaterThan(double other) {
            if (inclusive) {
                return number >= other;
            } else {
                return number > other;
            }
        }
        
        @Override
        public int compareTo(Bound other) {
            return Double.compare(number, other.number);
        }
        
    }
    
    /**
     * Thrown when an Interval is initialized with bounds that are equal.
     */
    public static class EqualBoundsException extends Exception {
        public EqualBoundsException(String message) {
            super(message);
        }
    }
    
}