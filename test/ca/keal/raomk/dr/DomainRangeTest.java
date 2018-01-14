package ca.keal.raomk.dr;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DomainRangeTest {
    
    @Test
    void testSimplificationPlainAllSimplify() throws Interval.EqualBoundsException {
        List<Interval> intervals = new ArrayList<>(Arrays.asList(
                new Interval(-10, false, 2, false),
                new Interval(0, false, 10, false),
                new Interval(8, false, 12, false)));
        DomainRange dr = new DomainRange(intervals);
        assertAll(
                () -> assertEquals(1, dr.getIntervals().size()),
                () -> assertEquals(new Interval(-10, false, 12, false), dr.getIntervals().toArray()[0])
        );
    }
    
    @Test
    void testSimplificationEdgeCasesAllSimplify() throws Interval.EqualBoundsException {
        List<Interval> intervals = new ArrayList<>(Arrays.asList(
                new Interval(-10, true, 2, false),
                new Interval(2, true, 8, true),
                new Interval(8, true, 12, false)));
        DomainRange dr = new DomainRange(intervals);
        assertAll(
                () -> assertEquals(1, dr.getIntervals().size()),
                () -> assertEquals(new Interval(-10, true, 12, false), dr.getIntervals().toArray()[0])
        );
    }
    
    @Test
    void testSimplificationPlainSomeSimplify() throws Interval.EqualBoundsException {
        List<Interval> intervals = new ArrayList<>(Arrays.asList(
                new Interval(-10, true, -2, false),
                new Interval(2, true, 8, true),
                new Interval(7, true, 12, false),
                new Interval(14, true, 20, true),
                new Interval(21, true, 24, false),
                new Interval(23, true, 25, false)));
        DomainRange dr = new DomainRange(intervals);
        assertAll(
                () -> assertEquals(4, dr.getIntervals().size()),
                () -> assertEquals(dr.getIntervals(), new HashSet<>(Arrays.asList(
                        new Interval(2, true, 12, false),
                        new Interval(-10, true, -2, false),
                        new Interval(14, true, 20, true),
                        new Interval(21, true, 25, false)
                )))
        );
    }
    
    @Test
    void testSimplificationTwoExclusiveEdgeCase() throws Interval.EqualBoundsException {
        List<Interval> intervals = new ArrayList<>(Arrays.asList(
                new Interval(-10, true, 0, false),
                new Interval(0, false, 10, true)));
        DomainRange dr = new DomainRange(intervals);
        assertEquals(new HashSet<>(intervals), dr.getIntervals());
    }
    
    @Test
    void testSimplificationAllSimplifyWithInfinities() throws Interval.EqualBoundsException {
        List<Interval> intervals = new ArrayList<>(Arrays.asList(
                new Interval(Interval.Bound.NEG_INFINITY, new Interval.Bound(5, true)),
                new Interval(Interval.Bound.NEG_INFINITY, new Interval.Bound(10, true)),
                new Interval(4, true, 11, true),
                new Interval(new Interval.Bound(10, true), Interval.Bound.INFINITY)));
        DomainRange dr = new DomainRange(intervals);
        assertAll(
                () -> assertEquals(1, dr.getIntervals().size()),
                () -> assertEquals(dr.getIntervals().toArray()[0],
                        new Interval(Interval.Bound.NEG_INFINITY, Interval.Bound.INFINITY))
        );
    }
    
    @Test
    void testSimplificationSomeSimplifyWithInfinities() throws Interval.EqualBoundsException {
        List<Interval> intervals = new ArrayList<>(Arrays.asList(
                new Interval(Interval.Bound.NEG_INFINITY, new Interval.Bound(5, true)),
                new Interval(Interval.Bound.NEG_INFINITY, new Interval.Bound(10, true)),
                new Interval(4, true, 11, true),
                new Interval(12, true, 16, true),
                new Interval(new Interval.Bound(15, true), Interval.Bound.INFINITY)));
        DomainRange dr = new DomainRange(intervals);
        assertAll(
                () -> assertEquals(2, dr.getIntervals().size()),
                () -> assertEquals(dr.getIntervals(), new HashSet<>(Arrays.asList(
                        new Interval(Interval.Bound.NEG_INFINITY, new Interval.Bound(11, true)),
                        new Interval(new Interval.Bound(12, true), Interval.Bound.INFINITY)
                )))
        );
    }
    
    @Test
    void parseComprehensiveSuccess() throws ParseException, Interval.EqualBoundsException {
        String drStr = "x < -4.5 or 20 <= x or -1 < x ≤ 2 or 10 >= x ≥ 1.2";
        List<Interval> expectedIntervals = new ArrayList<>(Arrays.asList(
                new Interval(Interval.Bound.NEG_INFINITY, new Interval.Bound(-4.5, false)),
                new Interval(new Interval.Bound(20, true), Interval.Bound.INFINITY),
                new Interval(-1, false, 2, true),
                new Interval(1.2, true, 10, true)));
        DomainRange expected = new DomainRange(expectedIntervals);
        DomainRange dr = DomainRange.parse(drStr, 'x');
        assertEquals(expected, dr);
    }
    
    @Test
    void parseIllegalTokensFails() {
        assertThrows(ParseException.class, () -> DomainRange.parse("0jF*S", 'x'));
    }
    
    @Test
    void parseWrongVariableFails() {
        assertThrows(ParseException.class, () -> DomainRange.parse("y > 2", 'x'));
    }
    
    @Test
    void parseOrAtBeginningFails() {
        assertThrows(ParseException.class, () -> DomainRange.parse("or x > 4", 'x'));
    }
    
    @Test
    void parseDoubleOrFails() {
        assertThrows(ParseException.class, () -> DomainRange.parse("2 < x < 5 or or x > 7", 'x'));
    }
    
    @Test
    void parseOrAtEndFails() {
        assertThrows(ParseException.class, () -> DomainRange.parse("x < 3 or", 'x'));
    }
    
    // The rest of the parse() tests are handled by Interval.parse()'s tests
    
}