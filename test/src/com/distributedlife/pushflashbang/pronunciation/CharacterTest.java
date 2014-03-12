package com.distributedlife.pushflashbang.pronunciation;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CharacterTest {
    @Test
    public void itShouldKnowIfItIsEmpty() {
        assertThat(new Character("").isEmpty(), is(true));
        assertThat(new Character("a").isEmpty(), is(false));
    }

    @Test
    public void itShouldKnowIfItIsAVariationOfA() {
        assertThat(new Character("").isVariationOfA(), is(false));
        assertThat(new Character("a").isVariationOfA(), is(true));
        assertThat(new Character("ā").isVariationOfA(), is(true));
        assertThat(new Character("á").isVariationOfA(), is(true));
        assertThat(new Character("ǎ").isVariationOfA(), is(true));
        assertThat(new Character("à").isVariationOfA(), is(true));
    }

    @Test
    public void itShouldKnowIfItIsAVariationOfE() {
        assertThat(new Character("").isVariationOfE(), is(false));
        assertThat(new Character("e").isVariationOfE(), is(true));
        assertThat(new Character("ē").isVariationOfE(), is(true));
        assertThat(new Character("é").isVariationOfE(), is(true));
        assertThat(new Character("ě").isVariationOfE(), is(true));
        assertThat(new Character("è").isVariationOfE(), is(true));
    }

    @Test
    public void itShouldKnowIfItIsAVariationOfI() {
        assertThat(new Character("").isVariationOfI(), is(false));
        assertThat(new Character("ī").isVariationOfI(), is(true));
        assertThat(new Character("í").isVariationOfI(), is(true));
        assertThat(new Character("ǐ").isVariationOfI(), is(true));
        assertThat(new Character("ì").isVariationOfI(), is(true));
        assertThat(new Character("i").isVariationOfI(), is(true));
    }

    @Test
    public void itShouldKnowIfItIsAVariationOfO() {
        assertThat(new Character("").isVariationOfO(), is(false));
        assertThat(new Character("o").isVariationOfO(), is(true));
        assertThat(new Character("ō").isVariationOfO(), is(true));
        assertThat(new Character("ó").isVariationOfO(), is(true));
        assertThat(new Character("ǒ").isVariationOfO(), is(true));
        assertThat(new Character("ò").isVariationOfO(), is(true));
    }

    @Test
    public void itShouldKnowIfItIsAVariationOfU() {
        assertThat(new Character("").isVariationOfU(), is(false));
        assertThat(new Character("u").isVariationOfU(), is(true));
        assertThat(new Character("ū").isVariationOfU(), is(true));
        assertThat(new Character("ú").isVariationOfU(), is(true));
        assertThat(new Character("ǔ").isVariationOfU(), is(true));
        assertThat(new Character("ù").isVariationOfU(), is(true));
    }

    @Test
    public void itShouldBeIgnoreVariationsWhenBeingEqual() {
        assertThat(new Character("u").equals(new Character("ú")), is(true));
        assertThat(new Character("ō").equals(new Character("ò")), is(true));
        assertThat(new Character("ì").equals(new Character("í")), is(true));
        assertThat(new Character("ě").equals(new Character("ē")), is(true));
        assertThat(new Character("a").equals(new Character("ǎ")), is(true));

        assertThat(new Character("a").equals(new Character("e")), is(false));
        assertThat(new Character("g").equals(new Character("g")), is(true));
        assertThat(new Character(" ").equals(new Character(" ")), is(true));
    }
}