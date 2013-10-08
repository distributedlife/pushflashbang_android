package com.distributedlife.pushflashbang;

import com.distributedlife.pushflashbang.db.Schedule;

import java.util.*;

public class ThingsToLearn {
    private final ArrayList<Object> words;
    private final ArrayList<Object> sentences;
    private final String language;
    private Schedule schedule;
    public static final String[] IGNORED = new String[]{
            " ", ",", ".", ";", "`", "~", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "-", "_", "+",
            "=", "{", "[", "}", "]", "|", "\\", ":", ";", "'", "\"", "<", ">", "?", "/"
    };

    public ThingsToLearn(Map<String, Object> things, Schedule schedule) {
        this.schedule = schedule;

        language = (String) things.get("language");
        words = (ArrayList<Object>) things.get("words");
        sentences = (ArrayList<Object>) things.get("sentences");
    }

    public ThingToLearn getFirst() {
        Map<String, Object> thingToLearn = (Map<String, Object>) words.get(0);

        return new ThingToLearn((String) thingToLearn.get("word"));
    }

    public ThingToLearn getNextWordToLearn() {
        for(Object thing: words) {
            Map<String, Object> thingToLearn = (Map<String, Object>) thing;

            String word = (String) thingToLearn.get("word");
            if (schedule.includes(word)) {
                continue;
            }

            return new ThingToLearn(word);
        }

        return null;
    }

    public ThingToLearn getNextAvailableSentence() {
        for(Object thing: sentences) {
            Map<String, Object> thingToLearn = (Map<String, Object>) thing;

            String sentence = (String) thingToLearn.get("sentence");
            if (schedule.includes(sentence)) {
                continue;
            }

            sentence = removePunctuation(sentence);

            List<WordReview> knownWords = getSortedListOfKnownWords();
            for (WordReview word : knownWords) {
                sentence = sentence.replace(word.getWhat(), "");

                if (sentence.isEmpty()) {
                    return new ThingToLearn((String) thingToLearn.get("sentence"));
                }
            }
        }

        return null;
    }

    private List<WordReview> getSortedListOfKnownWords() {
        List<WordReview> knownWords = schedule.getAll();
        Collections.sort(knownWords, new SortByLength());
        Collections.reverse(knownWords);

        return knownWords;
    }

    private String removePunctuation(String sentence) {
        for(String ignore : IGNORED) {
            sentence = sentence.replace(ignore, "");
        }
        return sentence;
    }

    public String getMeaningFor(String what) {
        for(Object thing: words) {
            Map<String, Object> thingToLearn = (Map<String, Object>) thing;

            if (what.equals(thingToLearn.get("word"))) {
                return (String) thingToLearn.get("meaning");
            }
        }
        for(Object thing: sentences) {
            Map<String, Object> thingToLearn = (Map<String, Object>) thing;

            if (what.equals(thingToLearn.get("sentence"))) {
                return (String) thingToLearn.get("meaning");
            }
        }

        return "No meaning found";
    }

    public String getPhoneticFor(String what) {
        for(Object thing: words) {
            Map<String, Object> thingToLearn = (Map<String, Object>) thing;

            if (what.equals(thingToLearn.get("word"))) {
                return (String) thingToLearn.get("guide");
            }
        }
        for(Object thing: sentences) {
            Map<String, Object> thingToLearn = (Map<String, Object>) thing;

            if (what.equals(thingToLearn.get("sentence"))) {
                return (String) thingToLearn.get("guide");
            }
        }

        return "No guide found";
    }

    public String getLanguage() {
        return language;
    }

    private class SortByLength implements Comparator<WordReview> {
        @Override
        public int compare(WordReview review, WordReview review2) {
            return ((Integer)review.getWhat().length()).compareTo(review2.getWhat().length());
        }
    }
}