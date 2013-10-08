package com.distributedlife.pushflashbang.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.distributedlife.pushflashbang.Interval;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Intervals extends SQLiteOpenHelper {
    private static final String NAME = "intervals";
    private static final String ID = "id" ;
    private static final String INTERVAL = "interval" ;
    private static final String SEQUENCE = "sequence" ;
    private static final String[] ALL_COLUMNS = new String[] {ID, INTERVAL, SEQUENCE};
    private static final int VERSION = 1;

    public Intervals(Context context) {
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

        String[] columns = new String[] {"id INTEGER PRIMARY KEY AUTOINCREMENT, interval INTEGER NOT NULL", "sequence INTEGER NOT NULL"};
        stringBuilder.append(StringUtils.join(columns, ","));

        stringBuilder.append(")");
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    public void add(Interval interval) {
        getWritableDatabase().execSQL(insert(interval.getInterval(), interval.getSequence()));
    }

    private String insert(Integer interval, Integer sequence) {
        String[] columns = new String[] {"interval", "sequence"};

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("INSERT INTO %s (%s) VALUES (%d, %d);", NAME, StringUtils.join(columns, ", "), interval, sequence));

        return stringBuilder.toString();
    }

    public List<Interval> getAll() {
        List<Interval> intervals = new ArrayList<Interval>();

        Cursor cursor = getReadableDatabase().query(NAME, ALL_COLUMNS, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            intervals.add(mapToInterval(cursor));

            cursor.moveToNext();
        }

        cursor.close();
        return intervals;
    }

    private Interval mapToInterval(Cursor cursor) {
        return new Interval(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {}

    public boolean isEmpty() {
        return getAll().isEmpty();
    }

    public void update(Interval interval) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("UPDATE %s SET interval = %d, sequence = %d WHERE id = %d;", NAME, interval.getInterval(), interval.getSequence(), interval.getId()));

        getWritableDatabase().execSQL(stringBuilder.toString());
    }

    public Interval getFirst() {
        List<Interval> intervals = getAll();
        Collections.sort(intervals, new IntervalComparator());

        return intervals.get(0);
    }

    public Interval getInterval(Integer seconds) {
        for(Interval interval : getAll()) {
            if (interval.getInterval().equals(seconds)) {
                return interval;
            }
        }

        return null;
    }

    public Interval getPrevious(Integer seconds) {
        List<Interval> intervals = getAll();
        Collections.sort(intervals, new IntervalComparator());

        Interval previous = intervals.get(0);
        for (Interval interval : intervals) {
            if (interval.getInterval().equals(seconds)) {
                return previous;
            }

            previous = interval ;
        }

        return intervals.get(0);
    }

    public Interval getNext(Integer seconds) {
        List<Interval> intervals = getAll();
        Collections.sort(intervals, new IntervalComparator());

        boolean isNext = false;
        for (Interval interval : intervals) {
            if (isNext) {
                return interval;
            }
            if (interval.getInterval().equals(seconds)) {
                isNext = true;
            }
        }

        return intervals.get(intervals.size() -1);
    }

    private class IntervalComparator implements Comparator<Interval> {
        @Override
        public int compare(Interval interval, Interval interval2) {
            return interval.getInterval().compareTo(interval2.getInterval());
        }
    }
}