package com.distributedlife.pushflashbang.db;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

import java.util.List;

public class MockCursor implements Cursor {
    private int position;
    private List<Object[]> originalItems;

    public MockCursor(List<Object[]> originalItems) {
        this.originalItems = originalItems;
        this.position = 0;
    }

    @Override
    public int getCount() {
        return originalItems.size();
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public boolean move(int i) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public boolean moveToPosition(int i) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public boolean moveToFirst() {
        position = 0;
        return true;
    }

    @Override
    public boolean moveToLast() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public boolean moveToNext() {
        position = position + 1;
        return true;
    }

    @Override
    public boolean moveToPrevious() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public boolean isFirst() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public boolean isLast() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public boolean isBeforeFirst() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public boolean isAfterLast() {
        return (position >= originalItems.size());
    }

    @Override
    public int getColumnIndex(String s) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public int getColumnIndexOrThrow(String s) throws IllegalArgumentException {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public String getColumnName(int i) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public String[] getColumnNames() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public int getColumnCount() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public byte[] getBlob(int i) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public String getString(int i) {
        return (String) originalItems.get(position)[i];
    }

    @Override
    public void copyStringToBuffer(int i, CharArrayBuffer charArrayBuffer) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public short getShort(int i) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public int getInt(int i) {
        return (Integer) originalItems.get(position)[i];
    }

    @Override
    public long getLong(int i) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public float getFloat(int i) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public double getDouble(int i) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public int getType(int i) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public boolean isNull(int i) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void deactivate() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public boolean requery() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void close() {}

    @Override
    public boolean isClosed() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void registerContentObserver(ContentObserver contentObserver) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void unregisterContentObserver(ContentObserver contentObserver) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void setNotificationUri(ContentResolver contentResolver, Uri uri) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public Uri getNotificationUri() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public boolean getWantsAllOnMoveCalls() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public Bundle getExtras() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public Bundle respond(Bundle bundle) {
        throw new RuntimeException("Not Implemented");
    }
}