package com.fabris.wordcounter.service;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

class FileReaderTest {

    private LineWriter lineWriter = mock(LineWriter.class);

    private FileReader service = new FileReader(lineWriter);

    @Test
    void readFile() throws IOException {
        when(lineWriter.writeLine(any()))
                .thenReturn(new ObjectId());
        service.readFile("./src/test/resources/testFileSmall.txt");
        verify(lineWriter, times(3)).writeLine(any());
    }
}