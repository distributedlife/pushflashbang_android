package com.distributedlife.pushflashbang;

import com.distributedlife.pushflashbang.db.Schedule;

import java.util.*;

public class ThingsToLearn {
    private final ArrayList<Object> words;
    private final ArrayList<Object> sentences;
    private final String language_foreign;
    private final String congratulations_foreign;
    private final String language_native;
    private final String congratulations_native;
    private Schedule schedule;
    public static final String[] IGNORED = new String[]{
            " ", ",", ".", ";", "`", "~", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "-", "_", "+",
            "=", "{", "[", "}", "]", "|", "\\", ":", ";", "'", "\"", "<", ">", "?", "/"
    };
    private List<String> unlearnableSentences;

    public ThingsToLearn(Map<String, Object> things, Schedule schedule) {
        this.schedule = schedule;

        language_foreign = (String) things.get("language_foreign");
        language_native = (String) things.get("language_native");
        words = (ArrayList<Object>) things.get("words");
        sentences = (ArrayList<Object>) things.get("sentences");
        congratulations_foreign = (String) things.get("congratulations_foreign");
        congratulations_native = (String) things.get("congratulations_native");
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

    public String getLanguageForeign() {
        return language_foreign;
    }

    public String getCongratulationsForeign() {
        return congratulations_foreign;
    }

    public String getCongratulationsNative() {
        return congratulations_native;
    }

    public String getLanguageNative() {
        return language_native;
    }

    public boolean allSentencesCanBeLearnt() {
        unlearnableSentences = new ArrayList<String>();

        for(Object thing: sentences) {
            Map<String, Object> thingToLearn = (Map<String, Object>) thing;

            String sentence = (String) thingToLearn.get("sentence");
            sentence = removePunctuation(sentence);

            for (Object wordObject : words) {
                Map<String, Object> wordStruct = (Map<String, Object>) wordObject;

                String word = (String) wordStruct.get("word");
                sentence = sentence.replace(word, "");
                if (sentence.isEmpty()) {
                    break;
                }
            }

            if (!sentence.isEmpty()) {
                unlearnableSentences.add((String) thingToLearn.get("sentence"));
            }
        }

        return unlearnableSentences.isEmpty();
    }

    public List<String> getUnlearnableSentences() {
        return unlearnableSentences;
    }

    private class SortByLength implements Comparator<WordReview> {
        @Override
        public int compare(WordReview review, WordReview review2) {
            return ((Integer)review.getWhat().length()).compareTo(review2.getWhat().length());
        }
    }
}