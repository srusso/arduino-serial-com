package net.sr89.arduino.serial;

import com.fazecast.jSerialComm.SerialPort;
import net.sr89.arduino.serial.data.SerialData;
import net.sr89.arduino.serial.exception.SerialReadError;
import net.sr89.arduino.serial.read.direct.BlockingReader;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ReadBlockingFromArduino {
    private static final Instant beginning = Instant.now();

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) throws SerialReadError, IOException {
        final SerialPort port = SerialPort.getCommPort("COM3");

        try (BlockingReader reader = new BlockingReader(port, Duration.ofMillis(100))) {
            while (true) {
                reader.tryRead(1000)
                        .ifPresentOrElse(
                                ReadBlockingFromArduino::printData,
                                ReadBlockingFromArduino::waitABit
                        );
            }
        }
    }

    private static void printData(SerialData serialData) {
        String byteString = byteArrayString(serialData.getData(), serialData.getLastIndexExclusive());
        System.out.println(toDecimalSeconds(elapsed()) + ": " + byteString + " = " + toUTF8(serialData));
    }

    private static String toUTF8(SerialData serialData) {
        return new String(serialData.getData(), 0, serialData.getLastIndexExclusive(), UTF_8);
    }

    public static String byteArrayString(byte[] a, int toIndexExclusive) {
        int iMax = toIndexExclusive - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
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
