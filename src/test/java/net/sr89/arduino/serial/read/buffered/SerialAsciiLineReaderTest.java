package net.sr89.arduino.serial.read.buffered;

import net.sr89.arduino.serial.data.SerialData;
import net.sr89.arduino.serial.exception.SerialReadError;
import net.sr89.arduino.serial.read.direct.SemiBlockingReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SerialAsciiLineReaderTest {

    SemiBlockingReader semiBlockingReader;

    SerialAsciiLineReader reader;

    // TODO remove this crap
    public static void main(String[] args) throws SerialReadError {
        SerialAsciiLineReaderTest test = new SerialAsciiLineReaderTest();

        test.setUp();
        test.readLessThanOneLine();

        test.setUp();
        test.readNothing();

        test.setUp();
        test.readFullLine();

        test.setUp();
        test.readMultipleLines();
    }

    @BeforeEach
    void setUp() {
        semiBlockingReader = mock(SemiBlockingReader.class);
        reader = new SerialAsciiLineReader(semiBlockingReader);
    }

    @Test
    void readNothing() throws SerialReadError {
        Optional<SerialData> data = Optional.empty();
        when(semiBlockingReader.tryRead(100)).thenReturn(data);

        assertTrue(reader.readNextLine().isEmpty());
    }

    @Test
    void readLessThanOneLine() throws SerialReadError {
        String string = "newline character missing";
        Optional<SerialData> data = Optional.of(
                new SerialData(string.getBytes(US_ASCII))
        );
        when(semiBlockingReader.tryRead(100)).thenReturn(data);

        assertTrue(reader.readNextLine().isEmpty());
    }

    @Test
    void readFullLine() throws SerialReadError {
        String string = "this is a full line\r\n";
        byte[] bytes = string.getBytes(US_ASCII);
        Optional<SerialData> data = Optional.of(
                new SerialData(bytes)
        );
        when(semiBlockingReader.tryRead(100))
                .thenReturn(data)
                .thenReturn(Optional.empty());

        Optional<String> line = reader.readNextLine();
        assertTrue(line.isPresent());
        assertEquals(line.get(), string);

        assertTrue(reader.readNextLine().isEmpty());
    }

    @Test
    void readMultipleLines() throws SerialReadError {
        String string = "first\r\nsecond\r\nthird\r\nunfinished";
        when(semiBlockingReader.tryRead(100))
                .thenReturn(Optional.of(new SerialData(string.getBytes(US_ASCII))))
                .thenReturn(Optional.of(new SerialData(" - now finished\r\ndangling".getBytes(US_ASCII))))
                .thenReturn(Optional.empty());

        String first = reader.readNextLine().get();
        String second = reader.readNextLine().get();
        String third = reader.readNextLine().get();
        String fourth = reader.readNextLine().get();
        assertEquals(first, "first\r\n");
        assertEquals(second, "second\r\n");
        assertEquals(third, "third\r\n");
        assertEquals(fourth, "unfinished - now finished\r\n");
    }
}