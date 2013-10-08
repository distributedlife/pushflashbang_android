package com.distributedlife.pushflashbang.content;

import com.distributedlife.pushflashbang.ThingsToLearn;
import com.distributedlife.pushflashbang.db.Schedule;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URL;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class AllSentencesCanBeLearnt {
    @Test
    public void itShouldDetectUnlearnableSentences() throws IOException {
        Schedule schedule = mock(Schedule.class);
        Yaml yaml = new Yaml();

        URL url = ClassLoader.getSystemResource("chinese.yaml");
        File file = new File(url.getPath());
        InputStream input = new FileInputStream(file);

        ThingsToLearn thingsToLearn = new ThingsToLearn((Map<String, Object>) yaml.load(input), schedule);
        assertThat(thingsToLearn.allSentencesCanBeLearnt(), is(true));
    }

    @Test
    public void itShouldListUnlearnableSentences() throws IOException {
        Schedule schedule = mock(Schedule.class);
        Yaml yaml = new Yaml();

        URL url = ClassLoader.getSystemResource("contains_unlearnable.yaml");
        File file = new File(url.getPath());
        InputStream input = new FileInputStream(file);

        ThingsToLearn thingsToLearn = new ThingsToLearn((Map<String, Object>) yaml.load(input), schedule);
        thingsToLearn.allSentencesCanBeLearnt();

        assertThat(thingsToLearn.getUnlearnableSentences().get(0), is("你好吗"));
    }
}