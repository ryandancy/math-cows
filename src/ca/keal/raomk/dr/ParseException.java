package ca.keal.raomk.dr;

/**
 * Thrown by DomainRange.parse(), Interval.parse(), etc. when parsing fails.
 */
public class ParseException extends Exception {
    
    ParseException(String msg) {
        super(msg);
    }
    
}