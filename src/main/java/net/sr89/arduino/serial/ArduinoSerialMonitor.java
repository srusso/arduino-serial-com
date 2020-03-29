package net.sr89.arduino.serial;

import com.fazecast.jSerialComm.SerialPort;
import net.sr89.arduino.serial.exception.SerialReadError;
import net.sr89.arduino.serial.read.buffered.SerialAsciiLineReader;
import net.sr89.arduino.serial.read.direct.SemiBlockingReader;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class ArduinoSerialMonitor {
    private static final Instant beginning = Instant.now();

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) throws SerialReadError, IOException {
        final SerialPort port = SerialPort.getCommPort("COM3");

        try (SerialAsciiLineReader reader = new SerialAsciiLineReader(new SemiBlockingReader(port, Duration.ofMillis(1000)));) {
            while (true) {
                reader.readNextLine().ifPresentOrElse(
                        line -> System.out.print(toDecimalSeconds(elapsed()) + ": " + line),
                        ArduinoSerialMonitor::waitABit
                );
            }
        }
    }

    private static String toDecimalSeconds(Duration duration) {
        long seconds = duration.getSeconds();
        Duration leftover = duration.minusSeconds(seconds);
        long millis = leftover.toMillis();

        return seconds + "." + threeDigitMillis(millis);
    }

    private static String threeDigitMillis(long millis) {
        if (millis < 10) {
            return "00" + millis;
        } else if (millis < 100) {
            return "0" + millis;
        } else {
            return String.valueOf(millis);
        }
    }

    private static Duration elapsed() {
        return Duration.between(beginning, Instant.now());
    }

    private static void waitABit() {
        try {
            Thread.sleep(20L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
