package inkball.elements;

import inkball.App;
import processing.core.PApplet;
import processing.core.PImage;

public class Wall {
    private int x, y;
    private PImage image;

    public Wall(int x, int y, PImage image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public void draw(PApplet app) {
        app.image(image, x * App.CELLSIZE, (y+2) * App.CELLSIZE, App.CELLSIZE, App.CELLSIZE);
    }
}
