package com.distributedlife.pushflashbang.pronunciation;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PronunciationExplanation {
    public static final String OPEN_TAG = "<em>";
    public static final String CLOSE_TAG = "</em>";
    public static final String NOTHING = "";
    private List<String> guidance;

    public PronunciationExplanation(List<String> guidance) {
        this.guidance = guidance;
    }

    public String toString() {
        return StringUtils.join(guidance, ", ");
    }

    public String withoutTags() {
        return toString().replace(OPEN_TAG, NOTHING).replace(CLOSE_TAG, "");
    }

    public List<Range<Integer>> getEmphasisedParts() {
        List<Range<Integer>> ranges = new ArrayList<Range<Integer>>();

        String text = toString();

        while(text.indexOf(OPEN_TAG) != -1) {
            Integer start = text.indexOf(OPEN_TAG);
            Integer end = text.indexOf(CLOSE_TAG);

            text = text.replaceFirst(OPEN_TAG, "");
            text = text.replaceFirst(CLOSE_TAG, "");

            end -= OPEN_TAG.length();

            ranges.add(Range.between(start, end));
        }

        return ranges;
    }
}