package com.distributedlife.pushflashbang;

import com.distributedlife.pushflashbang.db.Schedule;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ThingsToLearnTest {
    private Map<String,Object> things;
    private Schedule schedule;
    private ThingsToLearn thingsToLearn;

    @Before
    public void setupYamlFile() {
        schedule = mock(Schedule.class);

        things = new HashMap<String, Object>();

        List<Object> sentences = new ArrayList<Object>();
        Map<String, Object> firstSentence = new HashMap<String, Object>();
        firstSentence.put("sentence", "what there");
        sentences.add(firstSentence);

        Map<String, Object> secondSentence = new HashMap<String, Object>();
        secondSentence.put("sentence", "hi there");
        secondSentence.put("meaning", "greet someone");
        secondSentence.put("guide", "haɪ ðɛə(ɹ)");
        sentences.add(secondSentence);

        List<Object> words = new ArrayList<Object>();
        Map<String, Object> firstWord = new HashMap<String, Object>();
        firstWord.put("word", "hi");
        firstWord.put("meaning", "hello");
        firstWord.put("guide", "haɪ");
        words.add(firstWord);

        Map<String, Object> secondWord = new HashMap<String, Object>();
        secondWord.put("word", "there");
        words.add(secondWord);

        things.put("words", words);
        things.put("sentences", sentences);
        things.put("language_native", "english");
        things.put("language_foreign", "chinese");
        things.put("congratulations_native", "Congratulations");
        things.put("congratulations_foreign", "祝贺您");

        thingsToLearn = new ThingsToLearn(things, schedule);
    }

    @Test
    public void itShouldReturnTheLanguage() {
        assertThat(thingsToLearn.getLanguageForeign(), is("chinese"));
        assertThat(thingsToLearn.getLanguageNative(), is("english"));
    }

    @Test
    public void itShouldReturnTheCongratulationsText() {
        assertThat(thingsToLearn.getCongratulationsForeign(), is("祝贺您"));
        assertThat(thingsToLearn.getCongratulationsNative(), is("Congratulations"));
    }

    @Test
    public void itShouldReturnTheFirstWord() {
        ThingsToLearn thingsToLearn = new ThingsToLearn(things, schedule);

        assertThat(thingsToLearn.getFirst().getWhat(), is("hi"));
    }

    @Test
    public void whenGettingTheNextWordItShouldIgnoreWordsAlreadyInTheSchedule() {
        when(schedule.includes("hi")).thenReturn(true);

        assertThat(thingsToLearn.getNextWordToLearn().getWhat(), not("hi"));
    }

    @Test
    public void itShouldReturnTheNextAvailableWord() {
        when(schedule.includes("hi")).thenReturn(true);
        when(schedule.includes("there")).thenReturn(false);

        assertThat(thingsToLearn.getNextWordToLearn().getWhat(), is("there"));
    }

    @Test
    public void whenGettingTheNextWordItShouldReturnNullIfThereAreNoWordsToAdd() {
        when(schedule.includes("hi")).thenReturn(true);
        when(schedule.includes("there")).thenReturn(true);

        assertThat(thingsToLearn.getNextWordToLearn(), is(nullValue()));
    }

    @Test
    public void whenGettingTheNextSentenceItShouldIgnoreSentencesWhereNotAllWordsAreKnown() {
        List<WordReview> knownWords = new ArrayList<WordReview>();
        knownWords.add(new WordReview("hi"));
        knownWords.add(new WordReview("there"));

        when(schedule.getAll()).thenReturn(knownWords);
        when(schedule.includes("what there")).thenReturn(false);
        when(schedule.includes("hi there")).thenReturn(false);

        assertThat(thingsToLearn.getNextAvailableSentence().getWhat(), not("what there"));
    }

    @Test
    public void whenGettingTheNextSentenceItShouldIgnoreSentencesAlreadyInTheSchedule() {
        List<WordReview> knownWords = new ArrayList<WordReview>();
        knownWords.add(new WordReview("hi"));
        knownWords.add(new WordReview("there"));
        knownWords.add(new WordReview("what"));

        when(schedule.getAll()).thenReturn(knownWords);
        when(schedule.includes("what there")).thenReturn(true);
        when(schedule.includes("hi there")).thenReturn(false);

        assertThat(thingsToLearn.getNextAvailableSentence().getWhat(), not("what there"));
    }

    @Test
    public void itShouldReturnTheNextAvailableSentence() {
        List<WordReview> knownWords = new ArrayList<WordReview>();
        knownWords.add(new WordReview("hi"));
        knownWords.add(new WordReview("there"));

        when(schedule.getAll()).thenReturn(knownWords);
        when(schedule.includes("what there")).thenReturn(true);
        when(schedule.includes("hi there")).thenReturn(false);

        assertThat(thingsToLearn.getNextAvailableSentence().getWhat(), is("hi there"));
    }

    @Test
    public void itShouldReturnTheNextAvailableSentenceEvenForLanguagesWithTheSpaceConcept() {
        things = new HashMap<String, Object>();

        List<Object> sentences = new ArrayList<Object>();
        Map<String, Object> firstSentence = new HashMap<String, Object>();
        firstSentence.put("sentence", "你好");
        sentences.add(firstSentence);

        List<Object> words = new ArrayList<Object>();
        Map<String, Object> firstWord = new HashMap<String, Object>();
        firstWord.put("word", "你");
        words.add(firstWord);

        Map<String, Object> secondWord = new HashMap<String, Object>();
        secondWord.put("word", "好");
        words.add(secondWord);

        things.put("words", words);
        things.put("sentences", sentences);

        thingsToLearn = new ThingsToLearn(things, schedule);

        List<WordReview> knownWords = new ArrayList<WordReview>();
        knownWords.add(new WordReview("你"));
        knownWords.add(new WordReview("好"));
        when(schedule.getAll()).thenReturn(knownWords);

        assertThat(thingsToLearn.getNextAvailableSentence().getWhat(), is("你好"));
    }

    @Test
    public void whenGettingTheNextSentenceItShouldReturnNullIfNoSentencesAvailable() {
        when(schedule.includes("hi there")).thenReturn(true);
        when(schedule.includes("what there")).thenReturn(true);

        assertThat(thingsToLearn.getNextAvailableSentence(), is(nullValue()));
    }

    @Test
    public void itShouldReturnTheMeaningForAWord() {
        assertThat(thingsToLearn.getMeaningFor("hi"), is("hello"));
    }

    @Test
    public void itShouldReturnTheMeaningForASentence() {
        assertThat(thingsToLearn.getMeaningFor("hi there"), is("greet someone"));
    }

    @Test
    public void itShouldReturnTheGuideForAWord() {
        assertThat(thingsToLearn.getPhoneticFor("hi"), is("haɪ"));
    }

    @Test
    public void itShouldReturnTheGuideForASentence() {
        assertThat(thingsToLearn.getPhoneticFor("hi there"), is("haɪ ðɛə(ɹ)"));
    }
}