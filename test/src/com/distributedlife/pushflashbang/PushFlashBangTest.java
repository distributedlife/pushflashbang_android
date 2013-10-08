package com.distributedlife.pushflashbang;

import com.distributedlife.pushflashbang.db.Intervals;
import com.distributedlife.pushflashbang.db.Schedule;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class PushFlashBangTest {
    public static final int ONE_SECOND = 1000;
    private Intervals intervals;
    private DateTime now;
    private DateTime past;
    private DateTime future;
    private Schedule schedule;
    private ThingsToLearn thingsToLearn;

    @Before
    public void setupAndFreezeTime() {
        intervals = mock(Intervals.class);
        schedule = mock(Schedule.class);
        thingsToLearn = mock(ThingsToLearn.class);

        now = DateTime.now();
        past = now.minus(ONE_SECOND);
        future = now.plus(ONE_SECOND);
        JodaDateTime.freeze(now);
    }

    @After
    public void unfreezeTime() {
        JodaDateTime.unfreeze();
    }

    @Test
    public void WhenTheDatabaseHasNoRowsAddTheInitialIntervals() {
        when(intervals.isEmpty()).thenReturn(true);
        new PushFlashBang(intervals, schedule, thingsToLearn);

        ArgumentCaptor<Interval> argumentCaptor = ArgumentCaptor.forClass(Interval.class);
        verify(intervals, atLeastOnce()).add(argumentCaptor.capture());

        List<Interval> params = argumentCaptor.getAllValues();
        for (Interval interval : params) {
            assertThat(interval.getSequence(), is(0));
        }

        assertThat(params.get(0).getInterval(), is(0));
        assertThat(params.get(1).getInterval(), is(25));
        assertThat(params.get(2).getInterval(), is(120));
        assertThat(params.get(3).getInterval(), is(600));
        assertThat(params.get(4).getInterval(), is(3600));
        assertThat(params.get(5).getInterval(), is(18000));
        assertThat(params.get(6).getInterval(), is(86400));
        assertThat(params.get(7).getInterval(), is(432000));
        assertThat(params.get(8).getInterval(), is(2160000));
        assertThat(params.get(9).getInterval(), is(10368000));
        assertThat(params.get(10).getInterval(), is(63072000));

        assertThat(params.size(), is(11));
    }

    @Test
    public void WhenTheDatabaseHasRowsNoMoreAreAdded() {
        when(intervals.isEmpty()).thenReturn(false);
        new PushFlashBang(intervals, schedule, thingsToLearn);

        verify(intervals, never()).add(any(Interval.class));
    }

    @Test
    public void WhenTheScheduleIsEmptyTheFirstWordIsAdded() {
        when(schedule.isEmpty()).thenReturn(true);
        when(thingsToLearn.getFirst()).thenReturn(new ThingToLearn("你"));

        new PushFlashBang(intervals, schedule, thingsToLearn);

        ArgumentCaptor<WordReview> argumentCaptor = ArgumentCaptor.forClass(WordReview.class);
        verify(schedule).add(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getWhat(), is("你"));
        assertThat(argumentCaptor.getValue().getDue(), is(now));
        assertThat(argumentCaptor.getValue().getInterval(), is(0));
    }

    @Test
    public void WhenTheScheduleIsNotEmptyNoWordsAreAdded() {
        when(schedule.isEmpty()).thenReturn(false);

        new PushFlashBang(intervals, schedule, thingsToLearn);

        verify(schedule, never()).add(any(WordReview.class));
    }

    @Test
    public void WhenThereIsAWordToReviewItIsReturned() {
        when(schedule.isEmpty()).thenReturn(false);
        when(schedule.getNext()).thenReturn(new WordReview("你"));

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);

        assertThat(pushflashbang.getNextWordToReview().getWhat(), is("你"));
        assertThat(pushflashbang.getNextWordToReview().getDue(), is(now));
        assertThat(pushflashbang.getNextWordToReview().getInterval(), is(0));
    }

    @Test
    public void WhenThereIsNoWordToReviewTheNextAvailableSentenceIsScheduled() {
        when(schedule.isEmpty()).thenReturn(false);
        when(schedule.getNext()).thenReturn(null);
        when(thingsToLearn.getNextAvailableSentence()).thenReturn(new ThingToLearn("你好"));

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);

        pushflashbang.getNextWordToReview();

        ArgumentCaptor<WordReview> argumentCaptor = ArgumentCaptor.forClass(WordReview.class);
        verify(schedule).add(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getWhat(), is("你好"));
        assertThat(argumentCaptor.getValue().getDue(), is(now));
        assertThat(argumentCaptor.getValue().getInterval(), is(0));
    }

    @Test
    public void WhenThereIsNoWordToReviewTheNextAvailableSentenceIsReturned() {
        when(schedule.isEmpty()).thenReturn(false);
        when(schedule.getNext()).thenReturn(null, new WordReview("你好"));
        when(thingsToLearn.getNextAvailableSentence()).thenReturn(new ThingToLearn("你好"));

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);

        assertThat(pushflashbang.getNextWordToReview().getWhat(), is("你好"));
        assertThat(pushflashbang.getNextWordToReview().getDue(), is(now));
        assertThat(pushflashbang.getNextWordToReview().getInterval(), is(0));
    }

    @Test
    public void WhenThereIsNoWordToReviewAndNoAvailableSentenceTheNextAvailableWordIsScheduled() {
        when(schedule.isEmpty()).thenReturn(false);
        when(schedule.getNext()).thenReturn(null);
        when(thingsToLearn.getNextAvailableSentence()).thenReturn(null);
        when(thingsToLearn.getNextWordToLearn()).thenReturn(new ThingToLearn("好"));

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);
        pushflashbang.getNextWordToReview();

        ArgumentCaptor<WordReview> argumentCaptor = ArgumentCaptor.forClass(WordReview.class);
        verify(schedule).add(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getWhat(), is("好"));
        assertThat(argumentCaptor.getValue().getDue(), is(now));
        assertThat(argumentCaptor.getValue().getInterval(), is(0));
    }

    @Test
    public void WhenThereIsNoWordToReviewAndNoAvailableSentenceTheNextAvailableWordIsReturned() {
        when(schedule.isEmpty()).thenReturn(false);
        when(schedule.getNext()).thenReturn(null, new WordReview("好"));
        when(thingsToLearn.getNextAvailableSentence()).thenReturn(null);
        when(thingsToLearn.getNextWordToLearn()).thenReturn(new ThingToLearn("好"));

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);

        assertThat(pushflashbang.getNextWordToReview().getWhat(), is("好"));
        assertThat(pushflashbang.getNextWordToReview().getDue(), is(now));
        assertThat(pushflashbang.getNextWordToReview().getInterval(), is(0));
    }

    @Test
    public void WhenThereIsNoWordToReviewAndNothingToLearnReturnNull() {
        when(schedule.isEmpty()).thenReturn(false);
        when(schedule.getNext()).thenReturn(null);
        when(thingsToLearn.getNextAvailableSentence()).thenReturn(null);
        when(thingsToLearn.getNextWordToLearn()).thenReturn(null);

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);

        assertThat(pushflashbang.getNextWordToReview(), is(nullValue()));
    }

    @Test
    public void WhenThereIsAFailedReviewItShouldResetTheInterval() {
        WordReview review = new WordReview("好");
        when(intervals.getInterval(0)).thenReturn(new Interval(0));
        when(intervals.getFirst()).thenReturn(new Interval(5));

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);
        pushflashbang.failedReview(review);

        assertThat(review.getInterval(), is(5));
    }

    @Test
    public void WhenThereIsAFailedReviewItShouldSaveTheUpdatedInterval() {
        WordReview review = new WordReview("好");
        Interval interval = new Interval(5);
        Interval currentInterval = new Interval(0);
        when(intervals.getInterval(0)).thenReturn(currentInterval);
        when(intervals.getFirst()).thenReturn(interval);

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);
        pushflashbang.failedReview(review);

        ArgumentCaptor<Interval> argumentCaptor = ArgumentCaptor.forClass(Interval.class);
        verify(intervals).update(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getId(), is(currentInterval.getId()));
    }

    @Test
    public void WhenThereIsAFailedReviewItShouldRescheduleTheReview() {
        WordReview review = new WordReview("好");
        when(intervals.getInterval(0)).thenReturn(new Interval(0));
        when(intervals.getFirst()).thenReturn(new Interval(5));

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);
        pushflashbang.failedReview(review);

        assertThat(review.getDue(), is(now.plus(5000)));
    }

    @Test
    public void WhenThereIsAFailedReviewItShouldSaveTheUpdatedReview() {
        WordReview review = new WordReview("好");
        when(intervals.getInterval(0)).thenReturn(new Interval(0));
        when(intervals.getFirst()).thenReturn(new Interval(5));

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);
        pushflashbang.failedReview(review);

        verify(schedule).update(review);
    }

    @Test
    public void WhenThereIsAFailedReviewAndThePreviousSequenceIsPositiveItShouldSetTheFailedSequenceToMinusOne() {
        WordReview review = new WordReview("好");
        Interval currentInterval = new Interval(5);
        currentInterval.setSequence(1);
        when(intervals.getInterval(0)).thenReturn(currentInterval);
        when(intervals.getFirst()).thenReturn(new Interval(5));

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);
        pushflashbang.failedReview(review);

        assertThat(currentInterval.getSequence(), is(-1));
    }

    @Test
    public void WhenThereIsAFailedReviewAndThePreviousSequenceIsNotPositiveItShouldDecrementTheSequence() {
        WordReview review = new WordReview("好");
        Interval currentInterval = new Interval(5);
        currentInterval.setSequence(0);
        when(intervals.getInterval(0)).thenReturn(currentInterval);
        when(intervals.getFirst()).thenReturn(new Interval(5));

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);
        pushflashbang.failedReview(review);

        assertThat(currentInterval.getSequence(), is(-1));
    }

    @Test
    public void WhenThereIsAFailedReviewAndTheUpdatedSequenceBecomesNegativeTenItShouldSetTheSequenceToZero() {
        WordReview review = new WordReview("好");
        Interval currentInterval = new Interval(5);
        currentInterval.setSequence(-9);
        when(intervals.getInterval(0)).thenReturn(currentInterval);
        when(intervals.getPrevious(5)).thenReturn(new Interval(0));
        when(intervals.getFirst()).thenReturn(new Interval(5));

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);
        pushflashbang.failedReview(review);

        assertThat(currentInterval.getSequence(), is(0));
    }

    @Test
    public void WhenThereIsAFailedReviewAndTheUpdatedSequenceBecomesNegativeTenItShouldReduceTheInterval() {
        WordReview review = new WordReview(1, "好", past, 5, 1);
        Interval currentInterval = new Interval(5);
        currentInterval.setSequence(-9);
        when(intervals.getInterval(5)).thenReturn(currentInterval);
        when(intervals.getPrevious(5)).thenReturn(new Interval(0));
        when(intervals.getFirst()).thenReturn(new Interval(0));

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);
        pushflashbang.failedReview(review);

        assertThat(currentInterval.getInterval(), is(2));
    }

    @Test
    public void WhenThereIsAFailedReviewAndThereIsAChangeInIntervalTimeItShouldUpdateAllExistingReviewsOnTheOldInterval() {
        WordReview review = new WordReview(1, "好", past, 5, 1);
        Interval currentInterval = new Interval(5);
        currentInterval.setSequence(-9);
        when(intervals.getInterval(5)).thenReturn(currentInterval);
        when(intervals.getPrevious(5)).thenReturn(new Interval(0));
        when(intervals.getFirst()).thenReturn(new Interval(0));

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);
        pushflashbang.failedReview(review);

        verify(schedule).updateIntervals(5, 2);
    }

    @Test
    public void WhenThereIsAFailedReviewAndTheUpdatedSequenceBecomesNegativeTenItShouldAlwaysLeaveZeroAsZero() {
        WordReview review = new WordReview(1, "好", past, 0, 1);
        Interval currentInterval = new Interval(0);
        currentInterval.setSequence(-9);
        when(intervals.getInterval(0)).thenReturn(currentInterval);
        when(intervals.getPrevious(0)).thenReturn(currentInterval);
        when(intervals.getFirst()).thenReturn(new Interval(0));

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);
        pushflashbang.failedReview(review);

        assertThat(currentInterval.getInterval(), is(0));
    }

    @Test
    public void WhenThereIsASuccessfulReviewItShouldResetTheInterval() {
        WordReview review = new WordReview("好");
        when(intervals.getInterval(0)).thenReturn(new Interval(0));
        when(intervals.getNext(0)).thenReturn(new Interval(25));

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);
        pushflashbang.successfulReview(review);

        assertThat(review.getInterval(), is(25));
    }

    @Test
    public void WhenThereIsASuccessfulReviewItShouldSaveTheUpdatedInterval() {
        WordReview review = new WordReview("好");
        Interval interval = new Interval(5);
        Interval currentInterval = new Interval(0);
        when(intervals.getInterval(0)).thenReturn(currentInterval);
        when(intervals.getNext(0)).thenReturn(interval);

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);
        pushflashbang.successfulReview(review);

        ArgumentCaptor<Interval> argumentCaptor = ArgumentCaptor.forClass(Interval.class);
        verify(intervals).update(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getId(), is(currentInterval.getId()));
    }

    @Test
    public void WhenThereIsASuccessfulReviewItShouldRescheduleTheReview() {
        WordReview review = new WordReview("好");
        when(intervals.getInterval(0)).thenReturn(new Interval(0));
        when(intervals.getNext(0)).thenReturn(new Interval(25));

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);
        pushflashbang.successfulReview(review);

        assertThat(review.getDue(), is(now.plus(25000)));
    }

    @Test
    public void WhenThereIsASuccessfulReviewItShouldSaveTheUpdatedReview() {
        WordReview review = new WordReview("好");
        when(intervals.getInterval(0)).thenReturn(new Interval(0));
        when(intervals.getNext(0)).thenReturn(new Interval(5));

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);
        pushflashbang.successfulReview(review);

        verify(schedule).update(review);
    }

    @Test
    public void WhenThereIsASuccessfulReviewAndThePreviousSequenceIsNegativeItShouldSetTheSequenceToOne() {
        WordReview review = new WordReview("好");
        Interval currentInterval = new Interval(0);
        currentInterval.setSequence(-1);
        when(intervals.getInterval(0)).thenReturn(currentInterval);
        when(intervals.getNext(0)).thenReturn(new Interval(5));

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);
        pushflashbang.successfulReview(review);

        assertThat(currentInterval.getSequence(), is(1));
    }

    @Test
    public void WhenThereIsASuccessfulReviewAndThePreviousSequenceIsNotNegativeItShouldIncrementTheSequence() {
        WordReview review = new WordReview("好");
        Interval currentInterval = new Interval(0);
        currentInterval.setSequence(0);
        when(intervals.getInterval(0)).thenReturn(currentInterval);
        when(intervals.getNext(0)).thenReturn(new Interval(5));

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);
        pushflashbang.successfulReview(review);

        assertThat(currentInterval.getSequence(), is(1));
    }

    @Test
    public void WhenThereIsASuccessfulReviewAndTheUpdatedSequenceBecomesPositiveTenItShouldSetTheSequenceToZero() {
        WordReview review = new WordReview("好");
        Interval currentInterval = new Interval(5);
        currentInterval.setSequence(9);
        when(intervals.getInterval(0)).thenReturn(currentInterval);
        when(intervals.getNext(5)).thenReturn(new Interval(25));

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);
        pushflashbang.successfulReview(review);

        assertThat(currentInterval.getSequence(), is(0));
    }

    @Test
    public void WhenThereIsASuccessfulReviewAndTheUpdatedSequenceBecomesPositiveTenItShouldIncreaseTheInterval() {
        WordReview review = new WordReview(1, "好", past, 5, 1);
        Interval currentInterval = new Interval(5);
        currentInterval.setSequence(9);
        when(intervals.getInterval(5)).thenReturn(currentInterval);
        when(intervals.getNext(5)).thenReturn(new Interval(25));

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);
        pushflashbang.successfulReview(review);

        assertThat(currentInterval.getInterval(), is(15));
    }

    @Test
    public void WhenThereIsASuccessfulReviewAndTheUpdatedSequenceBecomesPositiveTenItShouldAlwaysLeaveZeroAsZero() {
        WordReview review = new WordReview(1, "好", past, 0, 1);
        Interval currentInterval = new Interval(0);
        currentInterval.setSequence(9);
        when(intervals.getInterval(0)).thenReturn(currentInterval);
        when(intervals.getNext(0)).thenReturn(new Interval(5));

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);
        pushflashbang.successfulReview(review);

        assertThat(currentInterval.getInterval(), is(0));
    }

    @Test
    public void WhenThereIsASuccessfulReviewAndThereIsAChangeInIntervalTimeItShouldUpdateAllExistingReviewsOnTheOldInterval() {
        WordReview review = new WordReview(1, "好", past, 5, 1);
        Interval currentInterval = new Interval(5);
        currentInterval.setSequence(9);
        when(intervals.getInterval(5)).thenReturn(currentInterval);
        when(intervals.getNext(5)).thenReturn(new Interval(25));

        PushFlashBang pushflashbang = new PushFlashBang(intervals, schedule, thingsToLearn);
        pushflashbang.successfulReview(review);

        verify(schedule).updateIntervals(5, 15);
    }
}