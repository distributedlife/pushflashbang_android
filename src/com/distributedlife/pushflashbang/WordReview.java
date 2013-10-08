package com.distributedlife.pushflashbang;

import org.joda.time.DateTime;

public class WordReview {
    private String what;
    private DateTime due;
    private Integer interval;
    private Integer id;
    private CharSequence meaning;

    public WordReview(Integer id, String what, DateTime due, int interval) {
        this.id = id;
        this.what = what;
        this.due = due;
        this.interval = interval;
    }

    public WordReview(String what) {
        this.what = what;
        this.due = DateTime.now();
        this.interval = 0;
    }

    public String getWhat() {
        return what;
    }

    public DateTime getDue() {
        return due;
    }

    public Integer getInterval() {
        return interval;
    }

    public Integer getId() {
        return id;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public void setDue(DateTime due) {
        this.due = due;
    }
}