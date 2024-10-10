package inkball.elements;

import inkball.App;
import processing.core.PApplet;
import processing.core.PImage;

public class Wall {
    private int gridX;
    private int gridY;
    private PImage image;

    public Wall(int gridX, int gridY, PImage image) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.image = image;
    }

    public void draw(PApplet app) {
        float pixelX = gridX * App.CELLSIZE;
        float pixelY = (gridY + 2) * App.CELLSIZE;
        app.image(image, pixelX, pixelY, App.CELLSIZE, App.CELLSIZE);
    }

    public int getX() {
        return gridX;
    }

    public int getY() {
        return gridY;
    }

    public PImage getImage() {
        return image;
    }

    public void setImage(PImage image) {
        this.image = image;
    }
}
