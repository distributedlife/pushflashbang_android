package com.distributedlife.pushflashbang.integration;

import com.distributedlife.pushflashbang.ThingsToLearn;
import com.distributedlife.pushflashbang.db.Schedule;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class ThingsToLearnTest {
    @Test
    public void itCanHandleTheFileFormat() throws IOException {
        Schedule schedule = mock(Schedule.class);
        Yaml yaml = new Yaml();

        URL url = ClassLoader.getSystemResource("chinese.yaml");
        File file = new File(url.getPath());
        InputStream input = new FileInputStream(file);

        ThingsToLearn thingsToLearn = new ThingsToLearn((Map<String, Object>) yaml.load(input), schedule);
        assertThat(thingsToLearn.getFirst().getWhat(), is("你"));
    }
}