package net.sr89.arduino.serial.read.buffered;

import net.sr89.arduino.serial.exception.SerialReadError;
import net.sr89.arduino.serial.read.direct.SemiBlockingReader;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class SerialAsciiLineReader implements Closeable {

    public static final int CARRIAGE_RETURN = 13;
    public static final int LINE_FEED = 10;
    public static final int BUFFER_SIZE = 1024;

    private final SemiBlockingReader reader;
    private final ByteBuffer buffer;
    private final Queue<String> readyLines;

    public SerialAsciiLineReader(SemiBlockingReader reader) {
        this.reader = reader;
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
        this.readyLines = new LinkedList<>();
    }

    public Optional<String> readNextLine() throws SerialReadError {
        if (!readyLines.isEmpty()) {
            return Optional.of(readyLines.poll());
        }

        readFromUnderlyingBuffer();

        if (!readyLines.isEmpty()) {
            return Optional.of(readyLines.poll());
        } else {
            return Optional.empty();
        }
    }

    private void readFromUnderlyingBuffer() throws SerialReadError {
        reader.tryRead(100)
                .ifPresent(data -> {
                    for (int i = 0; i < data.getSize(); i++) {
                        final byte b = data.getData()[i];

                        if (isLineFeedFollowingCarriageReturn(b)) {
                            buffer.put(b);
                            byte[] bufferContents = unloadBufferToArray();
                            readyLines.offer(new String(bufferContents, US_ASCII));
                        } else {
                            buffer.put(b);
                        }
                    }
                });
    }

    private byte[] unloadBufferToArray() {
        byte[] bufferContents = new byte[buffer.position()];
        buffer.clear();
        buffer.get(bufferContents);
        buffer.clear();
        return bufferContents;
    }

    private boolean isLineFeedFollowingCarriageReturn(byte b) {
        return buffer.position() > 0 && b == LINE_FEED && buffer.get(buffer.position() - 1) == CARRIAGE_RETURN;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
