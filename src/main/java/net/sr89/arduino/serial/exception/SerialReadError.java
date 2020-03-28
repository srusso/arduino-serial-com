package net.sr89.arduino.serial.exception;

public class SerialReadError extends Exception {
    public SerialReadError(String errorMessage) {
        super(errorMessage);
    }
}
