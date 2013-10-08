package com.distributedlife.pushflashbang.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.distributedlife.pushflashbang.PushFlashBang;
import com.distributedlife.pushflashbang.R;
import com.distributedlife.pushflashbang.WordReview;
import com.distributedlife.pushflashbang.ThingsToLearn;
import com.distributedlife.pushflashbang.db.Intervals;
import com.distributedlife.pushflashbang.db.Schedule;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Map;

public class Review extends Activity {
    public static final String FILE_NAME = "things_to_learn.yaml";
    private PushFlashBang pushFlashBang;
    private Intervals intervals;
    private Schedule schedule;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review);

        intervals = new Intervals(this);
        schedule = new Schedule(this);

        ThingsToLearn thingsToLearn = null;
        try {
            AssetManager assetManager = this.getAssets();
            Yaml yaml = new Yaml();
            thingsToLearn = new ThingsToLearn((Map<String, Object>) yaml.load(assetManager.open(FILE_NAME)), schedule);
        } catch (IOException e) {
            e.printStackTrace();
        }

        pushFlashBang = new PushFlashBang(intervals, schedule, thingsToLearn);

        Button button = (Button) findViewById(R.id.reveal);
        button.setOnClickListener(new RevealAnswer());
    }

    @Override
    public void onResume() {
        super.onResume();

        WordReview wordReview = pushFlashBang.getNextWordToReview();
        if (wordReview == null) {
            goToDone();
        } else {
            updateToLatestReview(wordReview);
        }
    }

    private void updateToLatestReview(WordReview wordReview) {
        TextView textView = (TextView) findViewById(R.id.what);
        textView.setText(wordReview.getWhat());
    }

    private void goToDone() {
        intervals.close();
        schedule.close();

        Intent intent = new Intent(this, Done.class);
        startActivity(intent);
        this.finish();
    }

    public class RevealAnswer implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            intervals.close();
            schedule.close();

            Intent intent = new Intent(view.getContext(), Revealed.class);
            startActivity(intent);
        }
    }
}
