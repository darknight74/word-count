package com.fabris.wordcounter.service;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

class FileReaderTest {

    private LineWriter lineWriter = mock(LineWriter.class);
    private WordCounter wordCounter = mock(WordCounter.class);

    private FileReader service = new FileReader(lineWriter, wordCounter);

    @Test
    void readFile() throws IOException {
        service.readFile("./src/test/resources/testFileSmall.xml");
        verify(lineWriter, times(8)).writeLine(any());
    }
}