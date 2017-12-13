package ca.keal.raomk;

import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;

/**
 * Represents an interval; that is, a range of real numbers, each side being inclusive or exclusive. Used for
 * domain and range. Immutable.
 */
@Data
public class Interval {
    
    private final Bound lowerBound;
    private final Bound upperBound;
    
    public Interval(double value1, boolean inclusive1, double value2, boolean inclusive2) throws EqualBoundsException {
        this(new Bound(value1, inclusive1), new Bound(value2, inclusive2));
    }
    
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
    
    public boolean overlaps(@NonNull Interval interval) {
        // Plain overlap: lower or upper in interval or interval.lower or interval.upper in this interval
        if (contains(interval.lowerBound.getNumber()) || contains(interval.upperBound.getNumber())
                || interval.contains(lowerBound.getNumber()) || interval.contains(upperBound.getNumber())) {
            return true;
        }
        
        // Edge case: just equal
        // If both are exclusive, then not overlaps; if any inclusive, then overlaps
        if (upperBound.getNumber() == interval.lowerBound.getNumber()) {
            return upperBound.isInclusive() || interval.lowerBound.isInclusive();
        }
        //noinspection SimplifiableIfStatement
        if (lowerBound.getNumber() == interval.upperBound.getNumber()) {
            return lowerBound.isInclusive() || interval.upperBound.isInclusive();
        }
        
        return false;
    }
    
    /**
     * Return this interval combined with {@code interval} - take min lower bound and max upper bound.
     * Most useful if they overlap.
     */
    @SneakyThrows
    public Interval combine(@NonNull Interval interval) {
        // Take min lower bound - if they're the same, inclusive wins
        Bound newLower;
        switch (lowerBound.compareTo(interval.lowerBound)) {
            case -1: // lowerBound < interval.lowerBound
                newLower = lowerBound;
                break;
            case 1: // lowerBoard > interval.lowerBound
                newLower = interval.lowerBound;
                break;
            default: // lowerBound == interval.lowerBound
                newLower = lowerBound.isInclusive() ? lowerBound : interval.lowerBound;
        }
        
        // Take max upper bound - if they're the same, inclusive wins
        Bound newUpper;
        switch (upperBound.compareTo(interval.upperBound)) {
            case -1: // upperBound < interval.upperBound
                newUpper = interval.upperBound;
                break;
            case 1: // upperBound > interval.upperBound
                newUpper = upperBound;
                break;
            default: // upperBound == interval.upperBound
                newUpper = upperBound.isInclusive() ? upperBound : interval.upperBound;
        }
        
        // Note: sneaky throwing because Interval.EqualBoundsException will never happen
        // (it would require both this interval and param interval to have equal bounds, impossible!)
        return new Interval(newLower, newUpper);
    }
    
    @Override
    public String toString() {
        return (lowerBound.isInclusive() ? "[" : "(")
                + lowerBound.getNumber() + ", " + upperBound.getNumber()
                + (upperBound.isInclusive() ? "]" : ")");
    }
    
    /**
     * Each bound of an interval - inclusive or exclusive, orderable.
     */
    @Data
    public static class Bound implements Comparable<Bound> {
        
        public static final Bound INFINITY = new Bound(Double.POSITIVE_INFINITY, false);
        public static final Bound NEG_INFINITY = new Bound(Double.NEGATIVE_INFINITY, false);
        
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