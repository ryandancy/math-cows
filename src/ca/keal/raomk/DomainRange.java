package ca.keal.raomk;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a domain or a range. This differs from {@link Interval} in that a domain/range can have multiple
 * intervals joined by "or" clauses. Immutable.
 */
@EqualsAndHashCode
public class DomainRange {
    
    private static final String LEGAL_TOKENS_REGEX = "<|≤|<=|>|≥|>=|or";
    
    /** The list of intervals or'd together */
    @Getter private final List<Interval> intervals;
    
    public DomainRange(List<Interval> intervals) {
        simplifyIntervalList(intervals);
        this.intervals = Collections.unmodifiableList(intervals);
    }
    
    /**
     * Parses a DomainRange. This is like {@link #parse(String, char)}, but for when the input is constant.
     * This method simply does not throw any checked exceptions, instead wrapping them in a RuntimeException
     * since the input should be determined not to have any errors.
     */
    public static DomainRange parseStatic(String input, char var) {
        try {
            return parse(input, var);
        } catch (ParseException | Interval.EqualBoundsException e) {
            System.err.println("Error in static parse of DomainRange");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Parse a {@link DomainRange} from a string representation such as:
     * <ul>
     *     <li>3 &lt; x &le; 5</li>
     *     <li>q &gt; 1.24</li>
     *     <li>x &lt; -1 or x &gt; 1</li>
     * </ul>
     * @param input the string to parse a DomainRange from
     * @param var what character represents a variable
     * @return a {@link DomainRange} parsed from the input.
     * @throws ParseException if the input is malformed or uses a variable other than var.
     * @throws Interval.EqualBoundsException if any interval has equal bounds (ex. 3 < x < 3).
     */
    public static DomainRange parse(String input, char var) throws ParseException, Interval.EqualBoundsException {
        List<String> tokens = tokenize(input);
        
        // Try to find illegal tokens - any not <, <=, >, >=, &le;, &ge;, number, var, or
        List<String> illegals = tokens.stream()
                .filter(token -> !(token.matches(LEGAL_TOKENS_REGEX))) // any always-legal token
                .filter(token -> !(token.equals(String.valueOf(var)))) // the variable
                .filter(token -> !(token.matches("-?\\d+(\\.\\d+)?"))) // number
                .collect(Collectors.toList());
        if (illegals.size() > 0) {
            throw new ParseException("Illegal tokens: " + illegals.stream().reduce((t1, t2) -> t1 + ", " + t2));
        }
        
        // All tokens can now be assumed to be legal
        // Separate into intervals (joined together by "or")
        List<List<String>> intervalsTokens = new ArrayList<>();
        int firstOr = tokens.indexOf("or");
        while (firstOr != -1) { // while there are "or"s in the list
            if (firstOr == 0 || firstOr == tokens.size()) {
                throw new ParseException("Extraneous \"or\"");
            } else {
                // put tokens before "or" into a new list, put tokens after "or" back into tokens
                intervalsTokens.add(tokens.subList(0, firstOr));
                tokens = tokens.subList(firstOr + 1, tokens.size());
            }
            firstOr = tokens.indexOf("or");
        }
        intervalsTokens.add(tokens);
        
        // Apparently ParseException and EqualBoundsException can't be thrown from within a lambda,
        // so we can't use streaming here *sigh*
        List<Interval> intervals = new ArrayList<>();
        for (List<String> intervalTokens : intervalsTokens) {
            intervals.add(Interval.parse(intervalTokens, var));
        }
        return new DomainRange(intervals);
    }
    
    /**
     * Split input into:
     * - strings of contiguous alphabetical characters (i.e. [a-zA-Z]+)
     * - strings of contiguous numerical characters (i.e. [0-9\\.\\-]+)
     * - strings of single characters of any not in these
     * then remove all whitespace-only strings
     */
    private static List<String> tokenize(@NonNull String input) {
        // Split:
        // between [a-zA-Z], [0-9.\\-] or vice versa
        // any before or after [^a-zA-Z0-9.\\-]
        String[] split = input.split(
                "(?<=[a-zA-Z])(?=[0-9.\\-])" // between alphabet and number
                + "|(?<=[0-9.\\-])(?=[a-zA-Z])" // between number and alphabet
                + "|(?<=[a-zA-Z0-9.\\-\\s])(?=[^a-zA-Z0-9.\\-])" // between non-symbol/whitespace and symbol
                + "|(?<=[^a-zA-Z0-9.\\-])(?=[a-zA-Z0-9.\\-\\s])"); // between symbol and non-symbol/whitespace
        
        
        // Filter out whitespace
        // We don't need to trim because the regex separates all whitespace
        return Arrays.stream(split).filter(str -> !str.matches("^\\s*$")).collect(Collectors.toList());
    }
    
    // Simplify the intervals - if any overlap, merge them
    private static void simplifyIntervalList(List<Interval> intervals) {
        for (int i = 0; i < intervals.size() - 1; i++) {
            Interval interval1 = intervals.get(i);
            for (int j = i + 1; j < intervals.size(); j++) {
                Interval interval2 = intervals.get(j);
                if (interval1.overlaps(interval2)) {
                    // remove interval1 and interval2 and insert combined version in interval1's place
                    Interval combined = interval1.combine(interval2);
                    intervals.remove(j);
                    intervals.remove(i);
                    intervals.add(i, combined);
                    interval1 = combined;
                    j--; // twiddle the index in order to check all intervals
                }
            }
        }
    }
    
    public boolean contains(double value) {
        // Do any of the intervals contain value?
        return intervals.stream().anyMatch(interval -> interval.contains(value));
    }
    
    @Override
    public String toString() {
        // All the interval strings with " or " in between
        return intervals.stream()
                .map(Interval::toString)
                .reduce("", (interval1, interval2) -> interval1 + " or " + interval2);
    }
    
}