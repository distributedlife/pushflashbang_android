package com.distributedlife.pushflashbang.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.distributedlife.pushflashbang.JodaDateTime;
import com.distributedlife.pushflashbang.WordReview;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.constructor;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest({SQLiteDatabase.class, SQLiteOpenHelper.class, ContentValues.class })
public class ScheduleTest {
    private static final long ONE_SECOND = 1000;
    private SQLiteDatabase sqLiteDatabase;
    private Schedule schedule;
    private DateTime past;
    private DateTime now;
    private DateTime future;
    private DateTimeFormatter formatter;

    @Before
    public void setupAndroidStubs() throws Exception {
        suppress(constructor(Schedule.class));

        Context context = mock(Context.class);
        schedule = spy(new Schedule(context));
        sqLiteDatabase = mock(SQLiteDatabase.class);

        now = DateTime.now();
        past = now.minus(ONE_SECOND);
        future = now.plus(ONE_SECOND);
        JodaDateTime.freeze(now);

        DateTimeParser parser = DateTimeFormat.forPattern(Schedule.PATTERN).getParser();
        formatter = new DateTimeFormatterBuilder().append(parser).toFormatter();
    }

    @Test
    public void itShouldCreateADatabase() {
        schedule.onCreate(sqLiteDatabase);

        verify(sqLiteDatabase).execSQL("CREATE TABLE schedule (id INTEGER PRIMARY KEY AUTOINCREMENT, what TEXT NOT NULL, due TEXT NOT NULL, interval INTEGER NOT NULL, first INTEGER NOT NULL);");
    }

    @Test
    public void itShouldKnowIfItIsEmpty() {
        doReturn(sqLiteDatabase).when(schedule).getReadableDatabase();
        when(sqLiteDatabase.query("schedule", new String[] {"id", "what", "due", "interval", "first"}, null, null, null, null, null)).thenReturn(new MockCursor(new ArrayList<Object[]>()));

        assertThat(schedule.isEmpty(), is(true));
    }

    @Test
    public void itShouldGetAllItemsInTable() {
        List<Object[]> originalItems = new ArrayList<Object[]>();
        originalItems.add(new Object[] {1, "你", past.toString(formatter), 5, 1});
        originalItems.add(new Object[] {2, "好", now.toString(formatter), 25, 0});
        originalItems.add(new Object[] {3, "你好", future.toString(formatter), 120, 0});

        MockCursor fakeCursor = new MockCursor(originalItems);

        doReturn(sqLiteDatabase).when(schedule).getReadableDatabase();
        when(sqLiteDatabase.query("schedule", new String[] {"id", "what", "due", "interval", "first"}, null, null, null, null, null)).thenReturn(fakeCursor);

        List<WordReview> items = schedule.getAll();

        assertThat(items.size(), is(fakeCursor.getCount()));
        assertThat(items.get(0).getId(), is(1));
        assertThat(items.get(0).getWhat(), is("你"));
        assertThat(items.get(0).getDue(), is(past));
        assertThat(items.get(0).getInterval(), is(5));
        assertThat(items.get(0).isFirst(), is(true));

        assertThat(items.get(1).getId(), is(2));
        assertThat(items.get(1).getWhat(), is("好"));
        assertThat(items.get(1).getDue(), is(now));
        assertThat(items.get(1).getInterval(), is(25));
        assertThat(items.get(1).isFirst(), is(false));

        assertThat(items.get(2).getId(), is(3));
        assertThat(items.get(2).getWhat(), is("你好"));
        assertThat(items.get(2).getDue(), is(future));
        assertThat(items.get(2).getInterval(), is(120));
        assertThat(items.get(2).isFirst(), is(false));
    }

    @Test
    public void itShouldGetTheNextDueReview() {
        List<Object[]> originalItems = new ArrayList<Object[]>();
        originalItems.add(new Object[] {1, "你", future.toString(formatter), 5, 1});
        originalItems.add(new Object[] {3, "你好", past.toString(formatter), 120, 0});

        MockCursor fakeCursor = new MockCursor(originalItems);

        doReturn(sqLiteDatabase).when(schedule).getReadableDatabase();
        when(sqLiteDatabase.query("schedule", new String[] {"id", "what", "due", "interval", "first"}, null, null, null, null, null)).thenReturn(fakeCursor);

        assertThat(schedule.getNext().getId(), is(3));
        assertThat(schedule.getNext().getWhat(), is("你好"));
        assertThat(schedule.getNext().getDue(), is(past));
        assertThat(schedule.getNext().getInterval(), is(120));
        assertThat(schedule.getNext().isFirst(), is(false));
    }

    @Test
    public void whenGettingTheNextDueItShouldIgnoreFutureReviews() {
        List<Object[]> originalItems = new ArrayList<Object[]>();
        originalItems.add(new Object[] {1, "你", future.toString(formatter), 5, 1});
        originalItems.add(new Object[] {3, "你好", future.toString(formatter), 120, 0});

        MockCursor fakeCursor = new MockCursor(originalItems);

        doReturn(sqLiteDatabase).when(schedule).getReadableDatabase();
        when(sqLiteDatabase.query("schedule", new String[] {"id", "what", "due", "interval", "first"}, null, null, null, null, null)).thenReturn(fakeCursor);

        assertThat(schedule.getNext(), is(nullValue()));
    }

    @Test
    public void itShouldAddReviewsToTheSchedule() {
        doReturn(sqLiteDatabase).when(schedule).getWritableDatabase();

        schedule.add(new WordReview("你好"));

        verify(sqLiteDatabase).execSQL("INSERT INTO schedule (what, due, interval, first) VALUES ('你好', '" + now.toString(formatter) + "', 0, 1);");
    }

    @Test
    public void itShouldKnowIfSomethingIsInTheSchedule() {
        List<WordReview> inSchedule = new ArrayList<WordReview>();
        inSchedule.add(new WordReview("你"));
        inSchedule.add(new WordReview("你好"));

        doReturn(inSchedule).when(schedule).getAll();

        assertThat(schedule.includes("你"), is(true));
        assertThat(schedule.includes("好"), is(false));
    }

    @Test
    public void itShouldUpdateReviewsInTheSchedule() {
        doReturn(sqLiteDatabase).when(schedule).getWritableDatabase();

        schedule.update(new WordReview(50, "你好", future, 10, 1));

        verify(sqLiteDatabase).execSQL("UPDATE schedule SET what = '你好', due = '" + future.toString(formatter) + "', interval = 10, first = 0 WHERE id = 50;");
    }

    @Test
    public void itShouldUpdateAllOldIntervalsToNewIntervals() {
        doReturn(sqLiteDatabase).when(schedule).getWritableDatabase();

        schedule.updateIntervals(100, 200);

        verify(sqLiteDatabase).execSQL("UPDATE schedule SET interval = 200 WHERE interval = 100;");
    }
}