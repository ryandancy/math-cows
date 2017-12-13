package ca.keal.raomk;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * Represents a domain or a range. This differs from {@link Interval} in that a domain/range can have multiple
 * intervals joined by "or" clauses. Immutable.
 */
@EqualsAndHashCode
public class DomainRange {
    
    /** The list of intervals or'd together */
    @Getter private final List<Interval> intervals;
    
    public DomainRange(List<Interval> intervals) {
        simplifyIntervalList(intervals);
        this.intervals = Collections.unmodifiableList(intervals);
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