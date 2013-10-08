package com.distributedlife.pushflashbang.android.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import com.distributedlife.pushflashbang.R;

public class NewCard extends Revealed {
    @Override
    public void setup() {
        setContentView(R.layout.newcard);
        setupButtonBindings();
        setTitleText();
    }

    private void setTitleText() {
        TextView title = (TextView) findViewById(R.id.header);
        title.setText("New Stuff!");
    }

    private void setupButtonBindings() {
        Button reviewPassed = (Button) findViewById(R.id.ok);
        reviewPassed.setOnClickListener(new ReviewSucceeded());
    }
}