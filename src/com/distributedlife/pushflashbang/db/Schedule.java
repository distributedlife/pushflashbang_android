package com.distributedlife.pushflashbang.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.distributedlife.pushflashbang.WordReview;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Schedule extends SQLiteOpenHelper {
    private static final String NAME = "schedule";
    private static final String ID = "id" ;
    private static final String WHAT = "what" ;
    private static final String DUE = "due" ;
    private static final String INTERVAL = "interval" ;
    private static final String FIRST = "first";
    private static final String[] ALL_COLUMNS = new String[] {ID, WHAT, DUE, INTERVAL, FIRST};
    private static final int VERSION = 1;
    public static final String PATTERN = "YYYY-MM-DD HH:mm:ss.SSSZ";

    public Schedule(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(create());
    }

    private static String create() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE");
        stringBuilder.append(" ");
        stringBuilder.append(NAME);
        stringBuilder.append(" ");
        stringBuilder.append("(");

        String[] columns = new String[] {"id INTEGER PRIMARY KEY AUTOINCREMENT, what TEXT NOT NULL", "due TEXT NOT NULL", "interval INTEGER NOT NULL, first INTEGER NOT NULL"};
        stringBuilder.append(StringUtils.join(columns, ", "));

        stringBuilder.append(")");
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {}

    public boolean isEmpty() {
        return getAll().isEmpty();
    }

    public void add(WordReview wordReview) {
        getWritableDatabase().execSQL(insert(wordReview.getWhat(), wordReview.getDue(), wordReview.getInterval()));
    }

    private String insert(String what, DateTime due, Integer interval) {
        DateTimeParser parser = DateTimeFormat.forPattern(PATTERN).getParser();
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().append(parser).toFormatter();

        String[] columns = new String[] {"what", "due", "interval", "first"};
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("INSERT INTO %s (%s) VALUES ('%s', '%s', %d, 1);", NAME, StringUtils.join(columns, ", "), what, due.toString(formatter), interval));

        return stringBuilder.toString();
    }

    public WordReview getNext() {
        List<WordReview> wordReviews = getAll();
        Collections.sort(wordReviews, new ReviewComparator());

        for(WordReview wordReview : wordReviews) {
            if (wordReview.getDue().isBeforeNow()) {
                return wordReview;
            }
        }

        return null;
    }

    public List<WordReview> getAll() {
        List<WordReview> schedule = new ArrayList<WordReview>();

        Cursor cursor = getReadableDatabase().query(NAME, ALL_COLUMNS, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            schedule.add(mapToReview(cursor));

            cursor.moveToNext();
        }

        cursor.close();
        return schedule;
    }

    private WordReview mapToReview(Cursor cursor) {
        DateTimeParser parser = DateTimeFormat.forPattern(PATTERN).getParser();
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().append(parser).toFormatter();

        return new WordReview(
                cursor.getInt(0),
                cursor.getString(1),
                DateTime.parse(cursor.getString(2), formatter),
                cursor.getInt(3),
                cursor.getInt(4)
        );
    }

    public boolean includes(String what) {
        for(WordReview wordReview : getAll()) {
            if (what.equals(wordReview.getWhat())) {
                return true;
            }
        }

        return false;
    }

    public void update(WordReview review) {
        DateTimeParser parser = DateTimeFormat.forPattern(PATTERN).getParser();
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().append(parser).toFormatter();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("UPDATE %s SET what = '%s', due = '%s', interval = %d, first = 0 WHERE id = %d;", NAME, review.getWhat(), review.getDue().toString(formatter), review.getInterval(), review.getId()));

        getWritableDatabase().execSQL(stringBuilder.toString());
    }

    public DateTime getTimeOfNextReview() {
        List<WordReview> wordReviews = getAll();
        Collections.sort(wordReviews, new ReviewComparator());

        return wordReviews.get(0).getDue();
    }

    public void updateIntervals(Integer oldInterval, Integer newInterval) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("UPDATE %s SET interval = %d WHERE interval = %d;", NAME, newInterval, oldInterval));

        getWritableDatabase().execSQL(stringBuilder.toString());
    }

    private class ReviewComparator implements Comparator<WordReview> {
        @Override
        public int compare(WordReview wordReview, WordReview wordReview2) {
            return wordReview.getDue().compareTo(wordReview2.getDue());
        }
    }
}