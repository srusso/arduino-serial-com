package net.sr89.arduino.serial.read.direct;

import com.fazecast.jSerialComm.SerialPort;
import net.sr89.arduino.serial.data.SerialData;
import net.sr89.arduino.serial.exception.SerialReadError;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;

public class NonBlockingReader implements Closeable {

    private final SerialPort comPort;

    public NonBlockingReader(SerialPort comPort) {
        this.comPort = comPort;
    }

    public Optional<SerialData> tryRead() throws SerialReadError {
        if (!comPort.isOpen()) {
            if (!comPort.openPort()) {
                System.out.println("Could not open port " + portName());
                return Optional.empty();
            }
        }

        int bytesAvailable = comPort.bytesAvailable();

        if (bytesAvailable < 0) {
            System.out.println("The port is closed");
            return Optional.empty();
        }

        if (bytesAvailable == 0) {
            return Optional.empty();
        }

        byte[] readBuffer = new byte[bytesAvailable];
        int readBytes = comPort.readBytes(readBuffer, readBuffer.length);

        if (readBytes < 0) {
            throw new SerialReadError("Error while reading from serial port " + portName());
        }

        return Optional.of(new SerialData(readBuffer));
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
