package ca.keal.raomk.dr;

import ca.keal.raomk.dr.Interval;
import ca.keal.raomk.dr.ParseException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

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
    
    @Test
    void parseOneBoundVarFirstLt() throws ParseException, Interval.EqualBoundsException {
        Interval expected = new Interval(Interval.Bound.NEG_INFINITY, new Interval.Bound(0, false));
        Interval actual = Interval.parse(Arrays.asList("x", "<", "0"), 'x');
        assertEquals(expected, actual);
    }
    
    @Test
    void parseOneBoundVarFirstLe1() throws ParseException, Interval.EqualBoundsException {
        Interval expected = new Interval(Interval.Bound.NEG_INFINITY, new Interval.Bound(0, true));
        Interval actual = Interval.parse(Arrays.asList("x", "<=", "0"), 'x');
        assertEquals(expected, actual);
    }
    
    @Test
    void parseOneBoundVarFirstLe2() throws ParseException, Interval.EqualBoundsException {
        Interval expected = new Interval(Interval.Bound.NEG_INFINITY, new Interval.Bound(0, true));
        Interval actual = Interval.parse(Arrays.asList("x", "≤", "0"), 'x');
        assertEquals(expected, actual);
    }
    @Test
    void parseOneBoundVarFirstGt() throws ParseException, Interval.EqualBoundsException {
        Interval expected = new Interval(new Interval.Bound(0, false), Interval.Bound.INFINITY);
        Interval actual = Interval.parse(Arrays.asList("x", ">", "0"), 'x');
        assertEquals(expected, actual);
    }
    
    @Test
    void parseOneBoundVarFirstGe1() throws ParseException, Interval.EqualBoundsException {
        Interval expected = new Interval(new Interval.Bound(0, true), Interval.Bound.INFINITY);
        Interval actual = Interval.parse(Arrays.asList("x", ">=", "0"), 'x');
        assertEquals(expected, actual);
    }
    
    @Test
    void parseOneBoundVarFirstGe2() throws ParseException, Interval.EqualBoundsException {
        Interval expected = new Interval(new Interval.Bound(0, true), Interval.Bound.INFINITY);
        Interval actual = Interval.parse(Arrays.asList("x", "≥", "0"), 'x');
        assertEquals(expected, actual);
    }
    
    @Test
    void parseOneBoundVarLastLt() throws ParseException, Interval.EqualBoundsException {
        Interval expected = new Interval(new Interval.Bound(0, false), Interval.Bound.INFINITY);
        Interval actual = Interval.parse(Arrays.asList("0", "<", "x"), 'x');
        assertEquals(expected, actual);
    }
    
    @Test
    void parseOneBoundVarLastGt() throws ParseException, Interval.EqualBoundsException {
        Interval expected = new Interval(Interval.Bound.NEG_INFINITY, new Interval.Bound(0, false));
        Interval actual = Interval.parse(Arrays.asList("0", ">", "x"), 'x');
        assertEquals(expected, actual);
    }
    
    @Test
    void parseTwoBoundsBothLt() throws ParseException, Interval.EqualBoundsException {
        Interval expected = new Interval(-1, false, 1, false);
        Interval actual = Interval.parse(Arrays.asList("-1", "<", "y", "<", "1"), 'y');
        assertEquals(expected, actual);
    }
    
    @Test
    void parseTwoBoundsLtLe() throws ParseException, Interval.EqualBoundsException {
        Interval expected = new Interval(-1, true, 1, false);
        Interval actual = Interval.parse(Arrays.asList("-1", "<=", "y", "<", "1"), 'y');
        assertEquals(expected, actual);
    }
    
    @Test
    void parseTwoBoundsBothLe() throws ParseException, Interval.EqualBoundsException {
        Interval expected = new Interval(-1, true, 1, true);
        Interval actual = Interval.parse(Arrays.asList("-1", "≤", "y", "<=", "1"), 'y');
        assertEquals(expected, actual);
    }
    
    @Test
    void parseTwoBoundsBothGt() throws ParseException, Interval.EqualBoundsException {
        Interval expected = new Interval(-1, false, 1, false);
        Interval actual = Interval.parse(Arrays.asList("1", ">", "y", ">", "-1"), 'y');
        assertEquals(expected, actual);
    }
    
    @Test
    void parseTwoBoundsGtGe() throws ParseException, Interval.EqualBoundsException {
        Interval expected = new Interval(-1, false, 1, true);
        Interval actual = Interval.parse(Arrays.asList("1", ">=", "y", ">", "-1"), 'y');
        assertEquals(expected, actual);
    }
    
    @Test
    void parseTwoBoundsBothGe() throws ParseException, Interval.EqualBoundsException {
        Interval expected = new Interval(-1, true, 1, true);
        Interval actual = Interval.parse(Arrays.asList("1", "≥", "y", ">=", "-1"), 'y');
        assertEquals(expected, actual);
    }
    
    @Test
    void parseWrongNumberTokensFails() {
        assertThrows(ParseException.class, () -> Interval.parse(Arrays.asList("T", ">", "2", "<"), 'T'));
    }
    
    @Test
    void parseIncorrectPatternFails() {
        assertThrows(ParseException.class, () -> Interval.parse(Arrays.asList("<", "q", "<"), 'q'));
    }
    
    @Test
    void parseOneBoundTwoVariablesFails() {
        assertThrows(ParseException.class, () -> Interval.parse(Arrays.asList("z", "<", "z"), 'z'));
    }
    
    @Test
    void parseOneBoundNoVariableFails() {
        assertThrows(ParseException.class, () -> Interval.parse(Arrays.asList("1.4", ">=", "-61.153"), 'n'));
    }
    
    @Test
    void parseTwoBoundVariableFirstFails() {
        assertThrows(ParseException.class, () -> Interval.parse(Arrays.asList("x", ">", "x", ">=", "2"), 'x'));
    }
    
    @Test
    void parseTwoBoundNumberMiddleFails() {
        assertThrows(ParseException.class, () -> Interval.parse(Arrays.asList("1", "<", "3", "<", "5"), 'x'));
    }
    
    @Test
    void parseTwoBoundVariableLastFails() {
        assertThrows(ParseException.class, () -> Interval.parse(Arrays.asList("1", ">=", "x", ">", "x"), 'x'));
    }
    
    @Test
    void parseTwoBoundOperatorMismatchPointingInwardsFails() {
        assertThrows(ParseException.class, () -> Interval.parse(Arrays.asList("1", ">=", "x", "<", "5"), 'x'));
    }
    
    @Test
    void parseTwoBoundOperatorMismatchPointingOutwardsFails() {
        assertThrows(ParseException.class, () -> Interval.parse(Arrays.asList("1", "<", "x", ">", "-5.3"), 'x'));
    }
    
    @Test
    void parseTwoBoundEqualBoundsFails() {
        assertThrows(Interval.EqualBoundsException.class, () -> Interval.parse(
                Arrays.asList("1", "<=", "x", "<=", "1"), 'x'));
    }
    
    @Test
    void parseTwoBoundLowerBoundGreaterThanUpperBoundLtFails() {
        assertThrows(ParseException.class, () -> Interval.parse(Arrays.asList("1", "<", "x", "<", "-1"), 'x'));
    }
    
    @Test
    void parseTwoBoundLowerBoundGreaterThanUpperBoundGtFails() {
        assertThrows(ParseException.class, () -> Interval.parse(Arrays.asList("-1", ">", "x", ">", "1"), 'x'));
    }
    
}