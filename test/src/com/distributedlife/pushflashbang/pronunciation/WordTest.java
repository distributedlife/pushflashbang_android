package com.distributedlife.pushflashbang.pronunciation;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class WordTest {
    private Word word;

    @Before
    public void setup() {
        word = new Word("Hi");
    }

    @Test
    public void itShouldSetTheOffsetToZero() {
        assertThat(word.getOffset(), is(0));
    }

    @Test
    public void itShouldMoveToAnyGivenOffset() {
        word.moveTo(2);
        assertThat(word.getOffset(), is(2));
    }

    @Test
    public void itShouldMoveToTheNextOffset() {
        word.moveToNext();
        assertThat(word.getOffset(), is(1));
        word.moveToNext();
        assertThat(word.getOffset(), is(2));
    }

    @Test
    public void itShouldShiftTheOffsetRightByAGivenAmount() {
        word.moveTo(4);
        word.shiftRight(4);
        assertThat(word.getOffset(), is(8));
    }

    @Test
    public void itShouldReturnTheLengthOfTheWord() {
        assertThat(word.getLength(), is(2));
    }

    @Test
    public void itShouldReturnTheWordSplitUpIntoCharacters() {
        assertThat(word.getMultibyteArray(), is(new String[]{"H", "i"}));
    }

    @Test
    public void itShouldReturnACharacterObjectAtTheSpecifiedPosition() {
        assertThat(word.charAt(1).equals(new Character("i")), is (true));
    }

    @Test
    public void itShouldReturnNullIfThereIsNoCharacterAtTheSpecifiedPosition() {
        assertThat(word.charAt(-1), is(nullValue()));
        assertThat(word.charAt(2), is(nullValue()));
    }

    @Test
    public void itShouldKnowIfIsAtTheLastCharacter() {
        assertThat(word.atLastCharacter(), is(false));
        assertThat(word.NotBeyondEnd(), is(true));

        word.moveToNext();

        assertThat(word.atLastCharacter(), is(true));
        assertThat(word.NotBeyondEnd(), is(true));

        word.moveToNext();

        assertThat(word.NotBeyondEnd(), is(false));
    }
}