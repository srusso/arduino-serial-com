package net.sr89.arduino.serial.read.buffered;

import net.sr89.arduino.serial.exception.SerialReadError;
import net.sr89.arduino.serial.read.direct.SemiBlockingReader;

import java.io.Closeable;
import java.io.IOException;

public class SerialAsciiLineReader implements Closeable {

    public static final int CARRIAGE_RETURN = 13;
    public static final int LINE_FEED = 10;

    private static final byte[] NEWLINE = new byte[]{CARRIAGE_RETURN, LINE_FEED};

    private final SemiBlockingReader reader;

    public SerialAsciiLineReader(SemiBlockingReader reader) {
        this.reader = reader;
    }

    public String readNextLine() throws SerialReadError {
        reader.tryRead(100);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
