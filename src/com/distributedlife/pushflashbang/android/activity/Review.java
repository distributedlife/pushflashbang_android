package com.distributedlife.pushflashbang.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.distributedlife.pushflashbang.R;
import com.distributedlife.pushflashbang.WordReview;

public class Review extends PushFlashBangActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review);

        setupButtonBindings();

        route();
    }

    @Override
    public void onResume() {
        super.onResume();

        route();
    }

    private void route() {
        WordReview wordReview = pushFlashBang.getNextWordToReview();
        if (wordReview == null) {
            show(this, Done.class);
            this.finish();
        } else if (wordReview.isFirst()) {
            show(this, NewCard.class);
        } else {
            updateToLatestReview(wordReview);
        }
    }

    private void setupButtonBindings() {
        Button button = (Button) findViewById(R.id.reveal);
        button.setOnClickListener(new RevealAnswer());
    }

    private void updateToLatestReview(WordReview wordReview) {
        TextView textView = (TextView) findViewById(R.id.what);
        TextView hint = (TextView) findViewById(R.id.hint);

        String displayText;
        String hintText;
        if (intervals.getInterval(wordReview.getInterval()).isReview()) {
            displayText = wordReview.getWhat();
            hintText = getString(R.string.review_hint);
        } else {
            displayText = thingsToLearn.getMeaningFor(wordReview.getWhat());
            hintText = getString(R.string.translate_hint);
        }
        textView.setText(displayText);
        hint.setText(hintText.replace("LANGUAGE", thingsToLearn.getLanguageForeign()));
    }

    public class RevealAnswer implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            show(view.getContext(), Revealed.class);
        }
    }
}
