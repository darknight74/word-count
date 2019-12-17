package com.fabris.wordcounter.service;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

class FileReaderTest {

    private LineWriterFuture lineWriter = mock(LineWriterFuture.class);
    private WordCounterFuture wordCounter = mock(WordCounterFuture.class);

    private FileReader service = new FileReader(lineWriter, wordCounter);

    @Test
    void readFile() throws IOException {
        when(lineWriter.writeLine(any()))
                .thenReturn(CompletableFuture.completedFuture(new ObjectId()));
        service.readFile("./src/test/resources/testFileSmall.xml");
        verify(lineWriter, times(8)).writeLine(any());
    }
}