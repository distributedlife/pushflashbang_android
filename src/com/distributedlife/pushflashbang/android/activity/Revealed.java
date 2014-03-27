package com.distributedlife.pushflashbang.android.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.distributedlife.pushflashbang.R;
import com.distributedlife.pushflashbang.WordReview;

public class Revealed extends ShowCardActivity {
//    private PronunciationGuidance pronunciationGuidance;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setup();

//        try {
//            pronunciationGuidance = new PronunciationGuidance((LinkedHashMap<String, Object>) new Yaml().load(getAsset("pronunciation_guidance.yaml")));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        WordReview wordReview = pushFlashBang.getNextWordToReview();
        if (wordReview == null) {
            finish();
        } else {
            setFields(wordReview);
        }
    }

    @Override
    void setup() {
        setContentView(R.layout.revealed);
        setupButtonBindings();
    }

    private void setupButtonBindings() {
        Button reviewFailed = (Button) findViewById(R.id.fail);
        reviewFailed.setOnClickListener(new ReviewFailed());

        Button reviewPassed = (Button) findViewById(R.id.pass);
        reviewPassed.setOnClickListener(new ReviewSucceeded());
    }

//    private InputStream getAsset(String filename) throws IOException {
//        return this.getAssets().open(filename);
//    }

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

//            PronunciationExplanation pronunciationExplanation = pronunciationGuidance.getExplanation(part);

//            TextView pronunciationText = newPronunciationText(pronunciationExplanation);
//            highlightKeySoundsInPronunciationText(pronunciationExplanation, pronunciationText);

            row.addView(newPhoneticText(part));
//            row.addView(pronunciationText);
            pronunciation.addView(row);
        }
    }

//    private void highlightKeySoundsInPronunciationText(PronunciationExplanation pronunciationExplanation, TextView pronunciationText) {
//        Spannable str = (Spannable) pronunciationText.getText();
//        for(Range<Integer> range: pronunciationExplanation.getEmphasisedParts()) {
//            str.setSpan(new ForegroundColorSpan(0x8CFF0000), range.getMinimum(), range.getMaximum(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//    }

    private TextView newPhoneticText(String part) {
        TextView guideText = new TextView(this);
        guideText.setText(part);
        guideText.setTextAppearance(this, R.style.pronunciationText);
        guideText.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.25f));
        return guideText;
    }

//    private TextView newPronunciationText(PronunciationExplanation explanation) {
//        TextView pronunciationText = new TextView(this);
//        pronunciationText.setText(explanation.withoutTags(), TextView.BufferType.SPANNABLE);
//        pronunciationText.setTextAppearance(this, R.style.pronunciationText);
//        pronunciationText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//        pronunciationText.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.75f));
//
//        return pronunciationText;
//    }

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
            finish();
        }
    }

    class ReviewSucceeded implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            pushFlashBang.successfulReview(pushFlashBang.getNextWordToReview());
            finish();
        }
    }
}