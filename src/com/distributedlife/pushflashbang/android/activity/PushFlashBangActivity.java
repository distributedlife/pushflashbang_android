package com.distributedlife.pushflashbang.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.distributedlife.pushflashbang.PushFlashBang;
import com.distributedlife.pushflashbang.ThingsToLearn;
import com.distributedlife.pushflashbang.db.Intervals;
import com.distributedlife.pushflashbang.db.Schedule;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class PushFlashBangActivity extends Activity {
    public static final String FILENAME = "things_to_learn.yaml";
    protected PushFlashBang pushFlashBang;
    protected Schedule schedule;
    protected Intervals intervals;
    protected ThingsToLearn thingsToLearn;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intervals = new Intervals(this);
        schedule = new Schedule(this);
        try {
            thingsToLearn = new ThingsToLearn((Map<String, Object>) new Yaml().load(getAsset(FILENAME)), schedule);
        } catch (IOException e) {
            //TODO: something here
            e.printStackTrace();
            finish();
        }

        pushFlashBang = new PushFlashBang(intervals, schedule, thingsToLearn);
    }

    protected void returnToReview() {
        Intent intent = new Intent(this, Review.class);
        startActivity(intent);
        this.finish();
    }

    private InputStream getAsset(String filename) throws IOException {
        return this.getAssets().open(filename);
    }
}