package ca.keal.raomk;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
                () -> assertEquals(new Interval(-10, false, 12, false), dr.getIntervals().get(0))
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
                () -> assertEquals(new Interval(-10, true, 12, false), dr.getIntervals().get(0))
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
                () -> assertEquals(new Interval(2, true, 12, false), dr.getIntervals().get(1)),
                () -> assertEquals(new Interval(-10, true, -2, false), dr.getIntervals().get(0)),
                () -> assertEquals(new Interval(14, true, 20, true), dr.getIntervals().get(2)),
                () -> assertEquals(new Interval(21, true, 25, false), dr.getIntervals().get(3))
        );
    }
    
    @Test
    void testSimplificationTwoExclusiveEdgeCase() throws Interval.EqualBoundsException {
        List<Interval> intervals = new ArrayList<>(Arrays.asList(
                new Interval(-10, true, 0, false),
                new Interval(0, false, 10, true)));
        DomainRange dr = new DomainRange(intervals);
        assertEquals(intervals, dr.getIntervals());
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
                () -> assertEquals(dr.getIntervals().get(0),
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
                () -> assertEquals(dr.getIntervals().get(0),
                        new Interval(Interval.Bound.NEG_INFINITY, new Interval.Bound(11, true))),
                () -> assertEquals(dr.getIntervals().get(1),
                        new Interval(new Interval.Bound(12, true), Interval.Bound.INFINITY))
        );
    }
    
}