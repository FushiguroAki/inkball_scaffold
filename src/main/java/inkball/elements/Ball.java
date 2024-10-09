package inkball.elements;

import inkball.App;
import processing.core.PApplet;
import processing.core.PImage;

public class Ball {
    private float x, y;
    private float dx, dy;   // velocity in x and y axis
    private PImage image;

    public Ball(int gridX, int gridY, PImage image) {
        this.x = gridX * App.CELLSIZE;
        this.y = (gridY + 2) * App.CELLSIZE;
        this.image = image;

        // Initialize velocities randomly to -2 or 2
        this.dx = getRandomVelocity();
        this.dy = getRandomVelocity();
    }

    private float getRandomVelocity() {
        return Math.random() < 0.5 ? -2.0f : 2.0f;
    }
    
    public void move() {
        x += dx;
        y += dy;
    }

    public void draw(PApplet app) {
        // app.image(image, x * App.CELLSIZE, (y + 2) * App.CELLSIZE, App.CELLSIZE, App.CELLSIZE);
        app.image(image, x, y, App.CELLSIZE, App.CELLSIZE);
        // app.fill(255, 0, 0);
        // app.ellipse(x + App.CELLSIZE / 2, y + App.CELLSIZE / 2, 5, 5);
    }

}
