package inkball.elements;

import inkball.App;
import processing.core.PApplet;
import processing.core.PImage;

public class Hole {
    int x, y;
    PImage image;

    public Hole(int x, int y, PImage image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public void draw(PApplet app) {
        app.image(image, x * App.CELLSIZE, (y+2) * App.CELLSIZE, App.CELLSIZE * 2, App.CELLSIZE * 2);  // Holes take 2x2 space
    }
}
