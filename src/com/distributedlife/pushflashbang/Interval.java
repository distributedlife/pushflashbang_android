package com.distributedlife.pushflashbang;

public class Interval {
    private Integer interval;
    private Integer sequence;
    private int id;

    public Interval(Integer interval) {
        this.interval = interval;
        this.sequence = 0 ;
    }

    public Interval(int id, int interval, int sequence) {
        this.id = id;
        this.interval = interval;
        this.sequence = sequence;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void decrementSequence() {
        if (sequence > 0) {
            sequence = -1;
        } else {
            sequence -= 1;
        }
    }

    public void incrementSequence() {
        if (sequence < 0) {
            sequence = 1;
        } else {
            sequence += 1;
        }
    }

    public boolean isReview() {
        return (id % 2 == 1);
    }
}