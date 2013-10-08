package com.distributedlife.pushflashbang;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class IntervalTest {
    @Test
    public void whenDecrementingTheSequenceItShouldBeSetToNegativeOneIfPreviouslyPositive() {
        Interval interval = new Interval(5);
        interval.setSequence(5);

        interval.decrementSequence();

        assertThat(interval.getSequence(), is(-1));
    }

    @Test
    public void whenDecrementingTheSequenceItShouldBeReducedByOneIfPreviouslyZero() {
        Interval interval = new Interval(5);
        interval.setSequence(0);

        interval.decrementSequence();

        assertThat(interval.getSequence(), is(-1));
    }

    @Test
    public void whenDecrementingTheSequenceItShouldBeReducedByOneIfPreviouslyNegative() {
        Interval interval = new Interval(5);
        interval.setSequence(-5);

        interval.decrementSequence();

        assertThat(interval.getSequence(), is(-6));
    }

    @Test
    public void whenIncrementingTheSequenceItShouldBeSetToOneIfPreviouslyNegative() {
        Interval interval = new Interval(5);
        interval.setSequence(-5);

        interval.incrementSequence();

        assertThat(interval.getSequence(), is(1));
    }

    @Test
    public void whenIncrementingTheSequenceItShouldBeIncreasedByOneIfPreviouslyZero() {
        Interval interval = new Interval(5);
        interval.setSequence(0);

        interval.incrementSequence();

        assertThat(interval.getSequence(), is(1));
    }

    @Test
    public void whenIncrementingTheSequenceItShouldBeIncreasedByOneIfPreviouslyPositive() {
        Interval interval = new Interval(5);
        interval.setSequence(5);

        interval.incrementSequence();

        assertThat(interval.getSequence(), is(6));
    }

    @Test
    public void itShouldReturnReviewIfIdIsOdd() {
        Interval interval = new Interval(1, 5, 0);
        assertThat(interval.isReview(), is(true));
    }

    @Test
    public void itShouldReturnTranslateIfIdIsEven() {
        Interval interval = new Interval(2, 5, 0);
        assertThat(interval.isReview(), is(false));
    }
}