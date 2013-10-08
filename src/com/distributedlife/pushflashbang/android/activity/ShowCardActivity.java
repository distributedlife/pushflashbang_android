package com.distributedlife.pushflashbang.android.activity;

import android.os.Bundle;

public abstract class ShowCardActivity extends PushFlashBangActivity  {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    abstract void setup();
}