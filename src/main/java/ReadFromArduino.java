import processing.core.PApplet;
import processing.core.PImage;
import processing.serial.*;

public class ReadFromArduino extends PApplet {

    private Serial myPort;
    private PImage logo;
    private int bgcolor = 0;

    public static void main(String[] args) {
        ReadFromArduino application = new ReadFromArduino();
        application.initSurface();
        application.start();
    }

    @Override
    public void setup() {
        myPort = new Serial(this, Serial.list()[1],  9600);
        size(1, 1);

        surface.setResizable(true);
        colorMode(HSB, 255);

        logo = loadImage("http://arduino.cc/arduino_logo.png");
        surface.setSize(logo.width, logo.height);

        println("Available serial ports: ");
        println(Serial.list());
    }

    @Override
    public void draw() {
        if (myPort.available() > 0) {
            bgcolor = myPort.read();
            println(bgcolor);
        } else {
            println("Nothing available");
        }

        background(bgcolor, 255, 255);
        image(logo, 0, 0);
    }
}
