package com.havvoric;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TSVFileDataTest {

    @Test
    public void extractRow_leadingWhite() {
        String row = "\tsecond\tthird\r\n";
        List<String> result = TSVFileData.extractRow(row);
        assertThat(result).hasSize(3).containsExactly("", "second", "third");
    }

    @Test
    public void extractRow_multipleTabs() {
        String row = "test\t\t\teol\r\n";
        List<String> result = TSVFileData.extractRow(row);
        assertThat(result).hasSize(4).containsExactly("test", "", "", "eol");
    }
}