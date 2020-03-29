package net.sr89.arduino.serial.data;

public class SerialData {
    private final byte[] data;
    private final int size;

    public SerialData(byte[] data) {
        this.data = data;
        this.size = data.length;
    }

    public SerialData(byte[] data, int size) {
        this.data = data;
        this.size = size;
    }

    public byte[] getData() {
        return data;
    }

    public int getSize() {
        return size;
    }
}
