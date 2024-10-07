package inkball.elements;

import processing.core.PApplet;
import processing.core.PImage;

public class UnspawnedBall {
    private String color;
    private PImage image;

    public UnspawnedBall(String color, PImage image) {
        this.color = color;
        this.image = image;
    }

    public void draw(PApplet app, int xOffset, int yOffset) {
        app.image(image, xOffset, yOffset, 32, 32);
    }

    public String getColor() {
        return color;
    }
}