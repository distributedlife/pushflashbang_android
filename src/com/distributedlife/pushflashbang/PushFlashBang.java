package com.distributedlife.pushflashbang;

import com.distributedlife.pushflashbang.db.Intervals;
import com.distributedlife.pushflashbang.db.Schedule;
import org.joda.time.DateTime;

public class PushFlashBang {
    private static final Integer[] INITIAL_INTERVALS = new Integer[] {0, 25, 120, 600, 3600, 18000, 86400, 432000, 2160000, 10368000, 63072000};
    public static final int SECONDS_TO_MILLIS = 1000;
    private ThingsToLearn thingsToLearn;
    private Intervals intervals;
    private Schedule schedule;

    public PushFlashBang(Intervals intervals, Schedule schedule, ThingsToLearn thingsToLearn) {
        this.intervals = intervals;
        this.schedule = schedule;
        this.thingsToLearn = thingsToLearn;

        if (intervals.isEmpty()) {
            createInitialSequence();
        }
        if (schedule.isEmpty()) {
            addFirstWord();
        }
    }

    private void addFirstWord() {
        ThingToLearn term = thingsToLearn.getFirst();
        WordReview firstWordReview = new WordReview(term.getWhat());

        schedule.add(firstWordReview);
    }

    private void createInitialSequence() {
        for (Integer seconds : INITIAL_INTERVALS) {
            intervals.add(new Interval(seconds));
        }
    }

    public WordReview getNextWordToReview() {
        WordReview next = schedule.getNext();

        if (next == null) {
            addNewSentenceToLearnToSchedule();
            next = schedule.getNext();
        }
        if (next == null) {
            addNewWordToLearnToSchedule();
            next = schedule.getNext();
        }

        return next;
    }

    private void addNewSentenceToLearnToSchedule() {
        ThingToLearn sentenceToLearn = thingsToLearn.getNextAvailableSentence();
        if (sentenceToLearn == null) {
            return;
        }

        WordReview nextWordReview = new WordReview(sentenceToLearn.getWhat());
        schedule.add(nextWordReview);
    }

    private void addNewWordToLearnToSchedule() {
        ThingToLearn thingToLearn = thingsToLearn.getNextWordToLearn();
        if (thingToLearn == null) {
            return;
        }

        WordReview nextWordReview = new WordReview(thingToLearn.getWhat());
        schedule.add(nextWordReview);
    }

    public void failedReview(WordReview review) {
        Interval currentInterval = intervals.getInterval(review.getInterval());
        currentInterval.decrementSequence();

        review.setInterval(intervals.getFirst().getInterval());
        review.setDue(DateTime.now().plus(review.getInterval() * SECONDS_TO_MILLIS));

        if (currentInterval.getSequence() == -10) {
            currentInterval.setSequence(0);

            Interval previousInterval = intervals.getPrevious(currentInterval.getInterval());
            currentInterval.setInterval((currentInterval.getInterval() + previousInterval.getInterval()) / 2);
        }

        schedule.update(review);
        intervals.update(currentInterval);
    }

    public void successfulReview(WordReview review) {
        Interval currentInterval = intervals.getInterval(review.getInterval());
        Interval nextInterval = intervals.getNext(currentInterval.getInterval());

        currentInterval.incrementSequence();

        review.setInterval(nextInterval.getInterval());
        review.setDue(DateTime.now().plus(review.getInterval() * SECONDS_TO_MILLIS));

        if (currentInterval.getSequence() == 10) {
            currentInterval.setSequence(0);

            currentInterval.setInterval((currentInterval.getInterval() + nextInterval.getInterval()) / 2);
        }

        schedule.update(review);
        intervals.update(currentInterval);
    }
}