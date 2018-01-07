package ca.keal.raomk;

public final class Utils {
    
    public static boolean approxEqual(double a, double b) {
        return Math.abs(a - b) < 0.005;
    }
    
}