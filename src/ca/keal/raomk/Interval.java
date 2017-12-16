package ca.keal.raomk;

import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.List;

/**
 * Represents an interval; that is, a range of real numbers, each side being inclusive or exclusive. Used for
 * domain and range. Immutable.
 */
@Data
public class Interval {
    
    private static final String NUMBER_REGEX = "-?\\d+(\\.\\d+)?";
    
    private final Bound lowerBound;
    private final Bound upperBound;
    
    public Interval(double value1, boolean inclusive1, double value2, boolean inclusive2) throws EqualBoundsException {
        this(new Bound(value1, inclusive1), new Bound(value2, inclusive2));
    }
    
    /** You can pass lower & upper bounds in whichever order (don't let them be the same though) */
    public Interval(Bound bound1, Bound bound2) throws EqualBoundsException {
        int comparison = bound1.compareTo(bound2);
        
        if (comparison < 0) { // bound1 < bound2
            lowerBound = bound1;
            upperBound = bound2;
        } else if (comparison > 0) { // bound1 > bound2
            lowerBound = bound2;
            upperBound = bound1;
        } else { // bound1 == bound2
            throw new EqualBoundsException("Bounds cannot be equal!");
        }
    }
    
    /**
     * Parse an Interval out of the list of tokens. Assumes all tokens are valid; they may however not be in the
     * correct order. Parses in x > 3, 5 < x < 10, etc. form, not [1, 20) form.
     */
    public static Interval parse(List<String> tokens, char var) throws ParseException, EqualBoundsException {
        // There must be three (e.g. x > 5) or five (e.g. 9 < x < 10) tokens
        if (tokens.size() != 3 && tokens.size() != 5) {
            throw new ParseException("Interval has wrong number of parts");
        }
        
        // Tokens 0, 2, etc. must be num/var, tokens 1, 3, etc. must be comparison operators
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (i % 2 == 0) {
                // Even: must be num/var
                if (!token.matches(NUMBER_REGEX) && !token.equals(String.valueOf(var))) {
                    throw new ParseException("Token " + (i + 1) + " must be a number or the variable");
                }
            } else {
                // Odd: must be comparison operator
                if (!token.matches(">|>=|≥|<|<=|≤")) {
                    throw new ParseException("Token " + (i + 1) + " must be a comparison operator");
                }
            }
        }
        
        // Parse bounded or unbounded
        if (tokens.size() == 3) {
            return parseUnbounded(tokens, var);
        } else {
            return parseBounded(tokens, var);
        }
    }
    
    // Parse an Interval with one side unbounded (e.g. x < 1)
    private static Interval parseUnbounded(List<String> tokens, char var) throws ParseException, EqualBoundsException {
        boolean varFirst = tokens.get(0).equals(String.valueOf(var));
        
        // One must be variable, other must be number
        if (!tokens.get(varFirst ? 0 : 2).equals(String.valueOf(var))
                || !tokens.get(varFirst ? 2 : 0).matches(NUMBER_REGEX)) {
            throw new ParseException("Interval must alternate variable-operator-number or vice versa");
        }
        
        // Decide whether it's an upper or lower bound and whether it's inclusive based on the operator
        String operator = tokens.get(1);
        boolean boundUpper = isOperatorLessThan(operator) == varFirst;
        boolean inclusive = isOperatorInclusive(operator);
        
        // Parse the interval
        double boundNum = Double.parseDouble(tokens.get(varFirst ? 2 : 0));
        Bound bound = new Bound(boundNum, inclusive);
        
        Bound upper, lower;
        if (boundUpper) {
            upper = bound;
            lower = Bound.NEG_INFINITY;
        } else {
            lower = bound;
            upper = Bound.INFINITY;
        }
        
        return new Interval(upper, lower);
    }
    
    // Parse an interval with both sides bounded (e.g. -20 > x > -50)
    private static Interval parseBounded(List<String> tokens, char var) throws ParseException, EqualBoundsException {
        // Middle token must be variable
        if (!tokens.get(2).equals(String.valueOf(var))) {
            throw new ParseException("Variable must be in the middle of a double-bounded interval");
        }
        
        // First and last tokens must be numbers
        if (!tokens.get(0).matches(NUMBER_REGEX) || !tokens.get(4).matches(NUMBER_REGEX)) {
            throw new ParseException("Numbers must be the first and last of a double-bounded interval");
        }
        
        String operator1 = tokens.get(1);
        String operator2 = tokens.get(3);
        
        // Operators must both be less than or greater than
        boolean lessThan = isOperatorLessThan(operator1);
        if (lessThan != isOperatorLessThan(operator2)) {
            throw new ParseException("Operators must be the same direction in a double-bounded interval");
        }
        
        // Prevent things like 5 < x < 3
        Bound first = new Bound(Double.parseDouble(tokens.get(0)), isOperatorInclusive(operator1));
        Bound second = new Bound(Double.parseDouble(tokens.get(4)), isOperatorInclusive(operator2));
        Bound upper, lower;
        if (lessThan) {
            lower = first;
            upper = second;
        } else {
            lower = second;
            upper = first;
        }
        
        if (lower.compareTo(upper) > 0) {
            throw new ParseException("Lower bound cannot be greater than upper bound");
        }
        
        return new Interval(lower, upper);
    }
    
    private static boolean isOperatorInclusive(String operator) {
        return operator.equals("<=") || operator.equals("≤") || operator.equals(">=") || operator.equals("≥");
    }
    
    private static boolean isOperatorLessThan(String operator) {
        return operator.equals("<") || operator.equals("<=") || operator.equals("≤");
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