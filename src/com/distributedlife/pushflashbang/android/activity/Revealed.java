package com.distributedlife.pushflashbang.android.activity;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.distributedlife.pushflashbang.*;
import com.distributedlife.pushflashbang.db.Intervals;
import com.distributedlife.pushflashbang.db.Schedule;
import com.distributedlife.pushflashbang.pronunciation.PronunciationExplanation;
import com.distributedlife.pushflashbang.pronunciation.PronunciationGuidance;
import org.apache.commons.lang3.Range;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Revealed extends PushFlashBangActivity {
    private PronunciationGuidance pronunciationGuidance;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.revealed);

//        intervals = new Intervals(this);
//        schedule = new Schedule(this);
//
//        try {
//            thingsToLearn = new ThingsToLearn((Map<String, Object>) new Yaml().load(getAsset(FILE_NAME)), schedule);
//        } catch (IOException e) {
//            //TODO: error dialogue.
//            e.printStackTrace();
//            finish();
//        }
//
//        pushFlashBang = new PushFlashBang(intervals, schedule, thingsToLearn);

        try {
            pronunciationGuidance = new PronunciationGuidance((LinkedHashMap<String, Object>) new Yaml().load(getAsset("chinese_pronunciation_guidance.yaml")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button reviewFailed = (Button) findViewById(R.id.fail);
        reviewFailed.setOnClickListener(new ReviewFailed());

        Button reviewPassed = (Button) findViewById(R.id.pass);
        reviewPassed.setOnClickListener(new ReviewSucceeded());

        WordReview wordReview = pushFlashBang.getNextWordToReview();
        if (wordReview == null) {
            finish();
        } else {
            setFields(wordReview);
        }
    }

    private InputStream getAsset(String filename) throws IOException {
        return this.getAssets().open(filename);
    }

    private void setFields(WordReview wordReview) {
        setReviewedWord(wordReview);
        setTranslation(wordReview);
        setPronunciation(wordReview);
    }

    private void setPronunciation(WordReview wordReview) {
        String phoneticPassage = thingsToLearn.getPhoneticFor(wordReview.getWhat());

        TableLayout pronunciation = (TableLayout) findViewById(R.id.pronunciation);
        for (String part : phoneticPassage.split(" ")) {
            TableRow row = new TableRow(this);

            PronunciationExplanation pronunciationExplanation = pronunciationGuidance.getExplanation(part);

            TextView pronunciationText = newPronunciationText(pronunciationExplanation);
            highlightKeySoundsInPronunciationTexxt(pronunciationExplanation, pronunciationText);

            row.addView(newPhoneticText(part));
            row.addView(pronunciationText);
            pronunciation.addView(row);
        }
    }

    private void highlightKeySoundsInPronunciationTexxt(PronunciationExplanation pronunciationExplanation, TextView pronunciationText) {
        Spannable str = (Spannable) pronunciationText.getText();
        for(Range<Integer> range: pronunciationExplanation.getEmphasisedParts()) {
            str.setSpan(new ForegroundColorSpan(0x8CFF0000), range.getMinimum(), range.getMaximum(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private TextView newPhoneticText(String part) {
        TextView guideText = new TextView(this);
        guideText.setText(part);
        guideText.setTextAppearance(this, R.style.pronunciationText);
        guideText.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.25f));
        return guideText;
    }

    private TextView newPronunciationText(PronunciationExplanation explanation) {
        TextView pronunciationText = new TextView(this);
        pronunciationText.setText(explanation.withoutTags(), TextView.BufferType.SPANNABLE);
        pronunciationText.setTextAppearance(this, R.style.pronunciationText);
        pronunciationText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        pronunciationText.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.75f));

        return pronunciationText;
    }

    private void setTranslation(WordReview wordReview) {
        TextView meaning = (TextView) findViewById(R.id.meaning);
        meaning.setText(thingsToLearn.getMeaningFor(wordReview.getWhat()));
    }

    private void setReviewedWord(WordReview wordReview) {
        TextView what = (TextView) findViewById(R.id.what);
        what.setText(wordReview.getWhat());
    }

    private class ReviewFailed implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            pushFlashBang.failedReview(pushFlashBang.getNextWordToReview());

            intervals.close();
            schedule.close();

            finish();
        }
    }

    private class ReviewSucceeded implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            pushFlashBang.successfulReview(pushFlashBang.getNextWordToReview());

            intervals.close();
            schedule.close();

            finish();
        }
    }
}