package ca.keal.raomk;

/**
 * Thrown by DomainRange.parse(), Interval.parse(), etc. when parsing fails.
 */
public class ParseException extends Exception {
    
    public ParseException(String msg) {
        super(msg);
    }
    
}