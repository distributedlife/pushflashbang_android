package com.distributedlife.pushflashbang.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.distributedlife.pushflashbang.Interval;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.constructor;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SQLiteDatabase.class, SQLiteOpenHelper.class, ContentValues.class })
public class IntervalsTest {
    private SQLiteDatabase sqLiteDatabase;
    private Intervals intervals;

    @Before
    public void setupAndroidStubs() throws Exception {
        suppress(constructor(Intervals.class));

        Context context = mock(Context.class);
        intervals = spy(new Intervals(context));
        sqLiteDatabase = mock(SQLiteDatabase.class);
    }

    @Test
    public void itShouldCreateADatabase() {
        intervals.onCreate(sqLiteDatabase);

        verify(sqLiteDatabase).execSQL("CREATE TABLE intervals (id INTEGER PRIMARY KEY AUTOINCREMENT, interval INTEGER NOT NULL,sequence INTEGER NOT NULL);");
    }

    @Test
    public void itShouldAddIntervals() {
        doReturn(sqLiteDatabase).when(intervals).getWritableDatabase();

        intervals.add(new Interval(5));

        verify(sqLiteDatabase).execSQL("INSERT INTO intervals (interval, sequence) VALUES (5, 0);");
    }

    @Test
    public void itShouldGetAllItemsInTable() {
        List<Object[]> originalItems = new ArrayList<Object[]>();
        originalItems.add(new Object[] {1, 5, 0});
        originalItems.add(new Object[] {2, 25, 5});
        originalItems.add(new Object[] {3, 120, -5});

        MockCursor fakeCursor = new MockCursor(originalItems);

        doReturn(sqLiteDatabase).when(intervals).getReadableDatabase();
        when(sqLiteDatabase.query("intervals", new String[] {"id", "interval", "sequence"}, null, null, null, null, null)).thenReturn(fakeCursor);

        List<Interval> items = intervals.getAll();

        assertThat(items.size(), is(fakeCursor.getCount()));
        assertThat(items.get(0).getId(), is(1));
        assertThat(items.get(0).getInterval(), is(5));
        assertThat(items.get(0).getSequence(), is(0));

        assertThat(items.get(1).getId(), is(2));
        assertThat(items.get(1).getInterval(), is(25));
        assertThat(items.get(1).getSequence(), is(5));

        assertThat(items.get(2).getId(), is(3));
        assertThat(items.get(2).getInterval(), is(120));
        assertThat(items.get(2).getSequence(), is(-5));
    }

    @Test
    public void itShouldKnowIfItIsEmpty() {
        doReturn(sqLiteDatabase).when(intervals).getReadableDatabase();
        when(sqLiteDatabase.query("intervals", new String[] {"id", "interval", "sequence"}, null, null, null, null, null)).thenReturn(new MockCursor(new ArrayList<Object[]>()));

        assertThat(intervals.isEmpty(), is(true));
    }

    @Test
    public void itShouldUpdateIntervals() {
        doReturn(sqLiteDatabase).when(intervals).getWritableDatabase();

        intervals.update(new Interval(10, 5, -3));

        verify(sqLiteDatabase).execSQL("UPDATE intervals SET interval = 5, sequence = -3 WHERE id = 10;");
    }

    @Test
    public void itShouldReturnTheFirstInterval() {
        List<Object[]> originalItems = new ArrayList<Object[]>();
        originalItems.add(new Object[] {1, 5, -1});
        originalItems.add(new Object[] {3, 0, 1});

        MockCursor fakeCursor = new MockCursor(originalItems);

        doReturn(sqLiteDatabase).when(intervals).getReadableDatabase();
        when(sqLiteDatabase.query("intervals", new String[] {"id", "interval", "sequence"}, null, null, null, null, null)).thenReturn(fakeCursor);

        assertThat(intervals.getFirst().getId(), is(3));
        assertThat(intervals.getFirst().getInterval(), is(0));
        assertThat(intervals.getFirst().getSequence(), is(1));
    }
    @Test
    public void itShouldReturnASpecificInterval() {
        List<Object[]> originalItems = new ArrayList<Object[]>();
        originalItems.add(new Object[] {1, 5, -1});
        originalItems.add(new Object[] {3, 0, 1});

        MockCursor fakeCursor = new MockCursor(originalItems);

        doReturn(sqLiteDatabase).when(intervals).getReadableDatabase();
        when(sqLiteDatabase.query("intervals", new String[] {"id", "interval", "sequence"}, null, null, null, null, null)).thenReturn(fakeCursor);

        assertThat(intervals.getInterval(5).getId(), is(1));
        assertThat(intervals.getInterval(5).getInterval(), is(5));
        assertThat(intervals.getInterval(5).getSequence(), is(-1));
    }

    @Test
    public void itShouldReturnThePreviousInterval() {
        List<Object[]> originalItems = new ArrayList<Object[]>();
        originalItems.add(new Object[] {1, 5, -1});
        originalItems.add(new Object[] {3, 0, 1});

        MockCursor fakeCursor = new MockCursor(originalItems);

        doReturn(sqLiteDatabase).when(intervals).getReadableDatabase();
        when(sqLiteDatabase.query("intervals", new String[] {"id", "interval", "sequence"}, null, null, null, null, null)).thenReturn(fakeCursor);

        assertThat(intervals.getPrevious(5).getInterval(), is(0));
    }

    @Test
    public void itShouldReturnItselfIfItIsTheFirstInterval() {
        List<Object[]> originalItems = new ArrayList<Object[]>();
        originalItems.add(new Object[] {1, 5, -1});
        originalItems.add(new Object[] {3, 0, 1});

        MockCursor fakeCursor = new MockCursor(originalItems);

        doReturn(sqLiteDatabase).when(intervals).getReadableDatabase();
        when(sqLiteDatabase.query("intervals", new String[] {"id", "interval", "sequence"}, null, null, null, null, null)).thenReturn(fakeCursor);

        assertThat(intervals.getPrevious(0).getInterval(), is(0));
    }

    @Test
    public void itShouldReturnTheNextInterval() {
        List<Object[]> originalItems = new ArrayList<Object[]>();
        originalItems.add(new Object[] {1, 5, -1});
        originalItems.add(new Object[] {3, 0, 1});

        MockCursor fakeCursor = new MockCursor(originalItems);

        doReturn(sqLiteDatabase).when(intervals).getReadableDatabase();
        when(sqLiteDatabase.query("intervals", new String[] {"id", "interval", "sequence"}, null, null, null, null, null)).thenReturn(fakeCursor);

        assertThat(intervals.getNext(0).getInterval(), is(5));
    }

    @Test
    public void itShouldReturnItselfIfItIsTheLastInterval() {
        List<Object[]> originalItems = new ArrayList<Object[]>();
        originalItems.add(new Object[] {1, 5, -1});
        originalItems.add(new Object[] {3, 0, 1});

        MockCursor fakeCursor = new MockCursor(originalItems);

        doReturn(sqLiteDatabase).when(intervals).getReadableDatabase();
        when(sqLiteDatabase.query("intervals", new String[] {"id", "interval", "sequence"}, null, null, null, null, null)).thenReturn(fakeCursor);

        assertThat(intervals.getNext(5).getInterval(), is(5));
    }
}