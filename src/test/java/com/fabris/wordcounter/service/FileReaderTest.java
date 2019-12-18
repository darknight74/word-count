package com.fabris.wordcounter.service;

import com.fabris.wordcounter.configuration.ApplicationSharedValues;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

class FileReaderTest {

    private LineWriter lineWriter = mock(LineWriter.class);
    private ApplicationSharedValues configuration = new ApplicationSharedValues();

    private FileReader service = new FileReader(lineWriter, configuration);

    @Test
    void readFile() throws IOException {
        when(lineWriter.writeLine(any()))
                .thenReturn(new ObjectId());
        service.readFile("./src/test/resources/testFileSmall.txt");
        verify(lineWriter, times(3)).writeLine(any());
    }
}