## Arduino Serial COM

Small wrapper on top of [jSerialCom](https://github.com/Fazecast/jSerialComm) that simplifies reading serial messages written by [arduino](https://www.arduino.cc/) to a COM interface. 

### Usage

There are three modes: blocking, non blocking, and buffered.

All three modes require an instance of `com.fazecast.jSerialComm.SerialPort`:

    SerialPort port = SerialPort.getCommPort("COM3");

##### Blocking mode

The following code example creates a blocking reader with a timeout of 100 milliseconds,
and tries to read 1000 bytes of data.
The method returns an optional containing the data if 1000 bytes were read within the timeout,
otherwise an empty optional is returned.

```
try (BlockingReader reader = new BlockingReader(port, Duration.ofMillis(100))) {
    Optional<SerialData> data = reader.tryRead(1000);
}
```

##### Non Blocking mode

This code creates a non-blocking reader which returns available data immediately, if available:

```
try (NonBlockingReader reader = new NonBlockingReader(port);) {
    Optional<SerialData> serialData = reader.tryRead();
}
```

##### Buffered mode

This code creates a line reader and tries to read the next line (terminated by a line feed + carriage return), returning within the timeout.
Note that each line can only be SerialAsciiLineReader#BUFFER_SIZE bytes long, including the 2 bytes for LF+CR.

```
try (SerialAsciiLineReader reader = new SerialAsciiLineReader(new SemiBlockingReader(port, Duration.ofMillis(1000)));) {
    Optional<String> line = reader.readNextLine();
}
```