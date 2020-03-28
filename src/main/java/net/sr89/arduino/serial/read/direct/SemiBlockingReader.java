package net.sr89.arduino.serial.read.direct;

import com.fazecast.jSerialComm.SerialPort;
import net.sr89.arduino.serial.data.SerialData;
import net.sr89.arduino.serial.exception.SerialReadError;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_READ_SEMI_BLOCKING;

public class SemiBlockingReader implements Closeable {

    private static final Duration MAX_TIMEOUT = Duration.ofMillis(Integer.MAX_VALUE);

    private final SerialPort comPort;

    public SemiBlockingReader(SerialPort comPort, Duration timeout) throws IllegalArgumentException {
        if (timeout.isNegative()) {
            throw new IllegalArgumentException("Invalid timeout " + timeout + ": must be positive.");
        }

        if (timeout.toMillis() > MAX_TIMEOUT.toMillis()) {
            throw new IllegalArgumentException("Invalid timeout " + timeout + ": larger than maximum allowed: " + MAX_TIMEOUT);
        }

        this.comPort = comPort;
        comPort.setComPortTimeouts(TIMEOUT_READ_SEMI_BLOCKING, (int) timeout.toMillis(), 0);
    }

    /**
     * Waits until there is at least one byte to read.
     */
    public Optional<SerialData> tryRead(int bytesToRead) throws SerialReadError {
        if (!comPort.isOpen()) {
            if (!comPort.openPort()) {
                System.out.println("Could not open port " + portName());
                return Optional.empty();
            }
        }

        byte[] readBuffer = new byte[bytesToRead];
        int readBytes = comPort.readBytes(readBuffer, readBuffer.length);

        if (readBytes < 0) {
            throw new SerialReadError("Error while reading from serial port " + portName());
        }

        if (readBytes == 0) { // timeout
            return Optional.empty();
        }

        return Optional.of(new SerialData(readBuffer, readBytes));
    }

    private String portName() {
        return comPort.getDescriptivePortName();
    }

    @Override
    public void close() throws IOException {
        if (!comPort.closePort()) {
            throw new IOException("Could not close port: " + portName());
        }
    }
}
