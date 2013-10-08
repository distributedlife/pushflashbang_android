package com.distributedlife.pushflashbang.integration;

import com.distributedlife.pushflashbang.pronunciation.PronunciationGuidance;
import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ChinesePronunciationGuidanceTest {
    private PronunciationGuidance pronunciationGuidance;

    @Before
    public void setup() throws FileNotFoundException {
        Yaml yaml = new Yaml();

        URL url = ClassLoader.getSystemResource("chinese_pronunciation_guidance.yaml");
        File file = new File(url.getPath());
        InputStream input = new FileInputStream(file);

        pronunciationGuidance = new PronunciationGuidance((LinkedHashMap<String, Object>) yaml.load(input));
    }

    @Test
    public void itShouldReturnEachCharacterWithExpansionIfNoExpansionExists() {
        assertThat(pronunciationGuidance.getExplanation("1234").toString(), is("1, 2, 3, 4"));
    }

    @Test
    public void itShouldIgnoreSpaces() {
        assertThat(pronunciationGuidance.getExplanation("1 2").toString(), is("1, 2"));
    }

    @Test
    public void itShouldMatchTheLongestMatcherFirst() {
        assertThat(pronunciationGuidance.getExplanation("b").toString(), is("<em>b</em>oy"));
        assertThat(pronunciationGuidance.getExplanation("ng").toString(), is("so<em>ng</em>"));
        assertThat(pronunciationGuidance.getExplanation("ngb").toString(), is("so<em>ng</em>, <em>b</em>oy"));
        assertThat(pronunciationGuidance.getExplanation("nngb").toString(), is("<em>n</em>imble, so<em>ng</em>, <em>b</em>oy"));
    }

    @Test
    public void itShouldHandleTheACharacterWithAccents() {
        assertThat(pronunciationGuidance.getExplanation("ā").toString(), is("m<em>a</em>"));
        assertThat(pronunciationGuidance.getExplanation("é").toString(), is("<em>e</em>arn"));
        assertThat(pronunciationGuidance.getExplanation("í").toString(), is("s<em>i</em>t"));
        assertThat(pronunciationGuidance.getExplanation("ò").toString(), is("dr<em>o</em>p"));
        assertThat(pronunciationGuidance.getExplanation("ū").toString(), is("l<em>oo</em>k"));
    }

    @Test
    public void itShouldHandleCombinedVowelsWithAccents() {
        assertThat(pronunciationGuidance.getExplanation("ai").toString(), is("<em>eye</em>"));
        assertThat(pronunciationGuidance.getExplanation("āi").toString(), is("<em>eye</em>"));
        assertThat(pronunciationGuidance.getExplanation("aí").toString(), is("<em>eye</em>"));
        assertThat(pronunciationGuidance.getExplanation("āí").toString(), is("<em>eye</em>"));
        assertThat(pronunciationGuidance.getExplanation("üe").toString(), is("<em>we</em>t"));
        assertThat(pronunciationGuidance.getExplanation("íao").toString(), is("m<em>eow</em>"));
        assertThat(pronunciationGuidance.getExplanation("iāo").toString(), is("m<em>eow</em>"));
        assertThat(pronunciationGuidance.getExplanation("iaò").toString(), is("m<em>eow</em>"));
        assertThat(pronunciationGuidance.getExplanation("íāò").toString(), is("m<em>eow</em>"));
    }

    @Test
    public void itShouldHandleAForwardAndBackChecks() {
        assertThat(pronunciationGuidance.getExplanation("iā").toString(), is("<em>ea</em>r"));
        assertThat(pronunciationGuidance.getExplanation("iās").toString(), is("<em>ea</em>r, <em>s</em>on"));
        assertThat(pronunciationGuidance.getExplanation("ān").toString(), is("m<em>a</em>, <em>n</em>imble"));
        assertThat(pronunciationGuidance.getExplanation("jān").toString(), is("<em>j</em>eep, m<em>a</em>, <em>n</em>imble"));
        assertThat(pronunciationGuidance.getExplanation("iān").toString(), is("<em>ea</em>r, <em>n</em>imble"));

        assertThat(pronunciationGuidance.getExplanation("én").toString(), is("<em>e</em>arn, <em>n</em>imble"));
        assertThat(pronunciationGuidance.getExplanation("né").toString(), is("<em>n</em>imble, <em>e</em>arn"));
        assertThat(pronunciationGuidance.getExplanation("ié").toString(), is("<em>air</em>"));
        assertThat(pronunciationGuidance.getExplanation("ué").toString(), is("l<em>oo</em>k, g<em>e</em>t"));

        assertThat(pronunciationGuidance.getExplanation("zhi").toString(), is("slu<em>dg</em>e, vocalised <em>r</em>"));
        assertThat(pronunciationGuidance.getExplanation("zwi").toString(), is("wor<em>ds</em>, <em>w</em>e, s<em>i</em>t"));
        assertThat(pronunciationGuidance.getExplanation("chi").toString(), is("<em>ch</em>ildren, vocalised <em>r</em>"));
        assertThat(pronunciationGuidance.getExplanation("cwi").toString(), is("ea<em>ts</em>, <em>w</em>e, s<em>i</em>t"));
        assertThat(pronunciationGuidance.getExplanation("shi").toString(), is("<em>sh</em>ake, vocalised <em>r</em>"));
        assertThat(pronunciationGuidance.getExplanation("swi").toString(), is("<em>s</em>on, <em>w</em>e, s<em>i</em>t"));
        assertThat(pronunciationGuidance.getExplanation("ri").toString(), is("<em>r</em>aw, vocalised <em>r</em>"));
        assertThat(pronunciationGuidance.getExplanation("di").toString(), is("<em>d</em>ig, s<em>i</em>t"));
        assertThat(pronunciationGuidance.getExplanation("hi").toString(), is("<em>h</em>ot, s<em>i</em>t"));

        assertThat(pronunciationGuidance.getExplanation("zi").toString(), is("wor<em>ds</em>, <em>i</em> like a buzzing bee"));
        assertThat(pronunciationGuidance.getExplanation("ci").toString(), is("ea<em>ts</em>, <em>i</em> like a buzzing bee"));
        assertThat(pronunciationGuidance.getExplanation("si").toString(), is("<em>s</em>on, <em>i</em> like a buzzing bee"));

        assertThat(pronunciationGuidance.getExplanation("on").toString(), is("dr<em>o</em>p, <em>n</em>imble"));
        assertThat(pronunciationGuidance.getExplanation("ohg").toString(), is("dr<em>o</em>p, <em>h</em>ot, <em>g</em>ood"));
        assertThat(pronunciationGuidance.getExplanation("ong").toString(), is("s<em>o</em>w, so<em>ng</em>"));
    }
}