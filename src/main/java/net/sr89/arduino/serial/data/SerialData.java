package net.sr89.arduino.serial.data;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SerialData {
    private final byte[] data;
    private final int lastIndexExclusive;

    public SerialData(byte[] data) {
        this.data = data;
        this.lastIndexExclusive = data.length;
    }

    public SerialData(byte[] data, int lastIndexExclusive) {
        this.data = data;
        this.lastIndexExclusive = lastIndexExclusive;
    }

    public byte[] getData() {
        return data;
    }

    public int getLastIndexExclusive() {
        return lastIndexExclusive;
    }
}
