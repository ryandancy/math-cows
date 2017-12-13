package ca.keal.raomk;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntervalTest {
    
    @Test
    void contructorOrdersCorrectly() throws Interval.EqualBoundsException {
        Interval.Bound bound1 = new Interval.Bound(1, false);
        Interval.Bound bound2 = new Interval.Bound(2, false);
        Interval interval1 = new Interval(bound1, bound2);
        Interval interval2 = new Interval(bound2, bound1);
        assertAll(
                () -> assertEquals(interval1, interval2),
                () -> assertEquals(interval1.getLowerBound(), bound1),
                () -> assertEquals(interval1.getUpperBound(), bound2)
        );
    }
    
    @Test
    void constructorOrdersCorrectlyWithInfinities() throws Interval.EqualBoundsException {
        Interval interval1 = new Interval(Interval.Bound.INFINITY, Interval.Bound.NEG_INFINITY);
        Interval interval2 = new Interval(Interval.Bound.NEG_INFINITY, Interval.Bound.INFINITY); 
        assertAll(
                () -> assertEquals(interval1, interval2),
                () -> assertEquals(interval1.getLowerBound(), Interval.Bound.NEG_INFINITY),
                () -> assertEquals(interval1.getUpperBound(), Interval.Bound.INFINITY)
        );
    }
    
    @Test
    void contructorThrowsWithIdenticalBounds() {
        Interval.Bound bound = new Interval.Bound(1, false);
        assertThrows(Interval.EqualBoundsException.class, () -> new Interval(bound, bound));
    }
    
    @Test
    void contructorThrowsWithEqualNumberBounds() {
        Interval.Bound bound1 = new Interval.Bound(1, false);
        Interval.Bound bound2 = new Interval.Bound(1, true);
        assertThrows(Interval.EqualBoundsException.class, () -> new Interval(bound1, bound2));
    }
    
    @Test
    void contains() throws Interval.EqualBoundsException {
        Interval interval = new Interval(new Interval.Bound(-10, true), new Interval.Bound(10, false));
        assertAll(
                () -> assertTrue(interval.contains(0)),
                () -> assertFalse(interval.contains(-15)),
                () -> assertFalse(interval.contains(15)),
                () -> assertTrue(interval.contains(-10)),
                () -> assertFalse(interval.contains(10))
        );
    }
    
    @Test
    void containsWithInfinities() throws Interval.EqualBoundsException {
        Interval interval = new Interval(Interval.Bound.INFINITY, Interval.Bound.NEG_INFINITY);
        assertAll(
                () -> assertTrue(interval.contains(1041027410124719.0)),
                () -> assertTrue(interval.contains(-1298187561284167.0))
        );
    }
    
    @Test
    void overlaps() throws Interval.EqualBoundsException {
        Interval interval1 = new Interval(new Interval.Bound(-10, true), new Interval.Bound(2, false));
        Interval interval2 = new Interval(new Interval.Bound(-2, true), new Interval.Bound(10, false));
        Interval interval3 = new Interval(new Interval.Bound(5, true), new Interval.Bound(13, false));
        assertAll(
                () -> assertTrue(interval1.overlaps(interval2)),
                () -> assertTrue(interval2.overlaps(interval1)),
                () -> assertFalse(interval1.overlaps(interval3)),
                () -> assertFalse(interval3.overlaps(interval1))
        );
    }
    
    @Test
    void overlapsJustMeetingTwoExclusiveGivesFalse() throws Interval.EqualBoundsException {
        Interval interval1 = new Interval(new Interval.Bound(-10, true), new Interval.Bound(0, false));
        Interval interval2 = new Interval(new Interval.Bound(0, false), new Interval.Bound(10, true));
        assertAll(
                () -> assertFalse(interval1.overlaps(interval2)),
                () -> assertFalse(interval2.overlaps(interval1))
        );
    }
    
    @Test
    void overlapsJustMeetingTwoInclusiveGivesTrue() throws Interval.EqualBoundsException {
        Interval interval1 = new Interval(new Interval.Bound(-10, true), new Interval.Bound(0, true));
        Interval interval2 = new Interval(new Interval.Bound(0, true), new Interval.Bound(10, true));
        assertAll(
                () -> assertTrue(interval1.overlaps(interval2)),
                () -> assertTrue(interval2.overlaps(interval1))
        );
    }
    
    @Test
    void overlapsJustMeetingInclusiveExclusiveGivesTrue() throws Interval.EqualBoundsException {
        Interval interval1 = new Interval(new Interval.Bound(-10, true), new Interval.Bound(0, false));
        Interval interval2 = new Interval(new Interval.Bound(0, true), new Interval.Bound(10, true));
        assertAll(
                () -> assertTrue(interval1.overlaps(interval2)),
                () -> assertTrue(interval2.overlaps(interval1))
        );
    }
    
    @Test
    void overlapsJustMeetingExclusiveInclusiveGivesTrue() throws Interval.EqualBoundsException {
        Interval interval1 = new Interval(new Interval.Bound(-10, true), new Interval.Bound(0, true));
        Interval interval2 = new Interval(new Interval.Bound(0, false), new Interval.Bound(10, true));
        assertAll(
                () -> assertTrue(interval1.overlaps(interval2)),
                () -> assertTrue(interval2.overlaps(interval1))
        );
    }
    
    @Test
    void overlapsWithInfinities() throws Interval.EqualBoundsException {
        Interval interval1 = new Interval(Interval.Bound.INFINITY, new Interval.Bound(-1, true));
        Interval interval2 = new Interval(Interval.Bound.INFINITY, new Interval.Bound(0, true));
        Interval interval3 = new Interval(Interval.Bound.NEG_INFINITY, new Interval.Bound(1, true));
        Interval interval4 = new Interval(Interval.Bound.NEG_INFINITY, new Interval.Bound(-2, true));
        assertAll(
                () -> assertTrue(interval1.overlaps(interval2)),
                () -> assertTrue(interval2.overlaps(interval1)),
                () -> assertTrue(interval2.overlaps(interval3)),
                () -> assertTrue(interval3.overlaps(interval2)),
                () -> assertFalse(interval2.overlaps(interval4)),
                () -> assertFalse(interval4.overlaps(interval2))
        );
    }
    
    @Test
    void combine() throws Interval.EqualBoundsException {
        Interval interval1 = new Interval(new Interval.Bound(-10, true), new Interval.Bound(2, false));
        Interval interval2 = new Interval(new Interval.Bound(-2, true), new Interval.Bound(10, false));
        assertEquals(interval1.combine(interval2),
                new Interval(new Interval.Bound(-10, true), new Interval.Bound(10, false)));
    }
    
    @Nested
    class BoundTest {
        
        @Test
        void constructor() {
            Interval.Bound bound = new Interval.Bound(-1.2, false);
            assertAll(
                    () -> assertEquals(bound.getNumber(), -1.2),
                    () -> assertEquals(bound.isInclusive(), false)
            );
        }
        
        @Test
        void exclusiveLessThan() {
            Interval.Bound bound = new Interval.Bound(5.3, false);
            assertAll(
                    () -> assertTrue(bound.lessThan(10)),
                    () -> assertFalse(bound.lessThan(-0.4)),
                    () -> assertFalse(bound.lessThan(5.3))
            );
        }
        
        @Test
        void inclusiveLessThan() {
            Interval.Bound bound = new Interval.Bound(5.3, true);
            assertAll(
                    () -> assertTrue(bound.lessThan(10)),
                    () -> assertFalse(bound.lessThan(-0.4)),
                    () -> assertTrue(bound.lessThan(5.3))
            );
        }
    
        @Test
        void exclusiveGreaterThan() {
            Interval.Bound bound = new Interval.Bound(-1.9, false);
            assertAll(
                    () -> assertTrue(bound.greaterThan(-4.6314125)),
                    () -> assertFalse(bound.greaterThan(-1.2)),
                    () -> assertFalse(bound.greaterThan(-1.9))
            );
        }
    
        @Test
        void inclusiveGreaterThan() {
            Interval.Bound bound = new Interval.Bound(0, true);
            assertAll(
                    () -> assertTrue(bound.greaterThan(-3)),
                    () -> assertFalse(bound.greaterThan(2)),
                    () -> assertTrue(bound.greaterThan(0))
            );
        }
        
        @Test
        void infinity() {
            assertAll(
                    () -> assertTrue(Interval.Bound.INFINITY.greaterThan(1024810924)),
                    () -> assertFalse(Interval.Bound.INFINITY.lessThan(-125111))
            );
        }
        
        @Test
        void negInfinity() {
            assertAll(
                    () -> assertFalse(Interval.Bound.NEG_INFINITY.greaterThan(1024810924)),
                    () -> assertTrue(Interval.Bound.NEG_INFINITY.lessThan(-11412425111.0))
            );
        }
        
    }
    
}