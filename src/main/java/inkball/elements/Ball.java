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

    public float getRadius() {
        return App.CELLSIZE / 2;
    }

    private float getRandomVelocity() {
        float velocity = Math.random() < 0.5 ? -2.0f : 2.0f;
        System.out.println("Initial velocity: " + velocity);
        return velocity;
    }
    
    public void move() {
        x += dx;
        y += dy;
        System.out.println("Ball position updated to: (" + x + ", " + y + ")");
        System.out.println("Ball velocity: (" + dx + ", " + dy + ")");
        updateCooldown();
    }
    

    // reverse direction of the ball when coliision
    public void reverseDirectionX() {
        dx *= -1;
    }

    public void reverseDirectionY() {
        dy *= -1;
    }

    private int collisionCooldown = 0;

    public void updateCooldown() {
        if (collisionCooldown > 0) {
            collisionCooldown--;
        }
    }

    public boolean canCollide() {
        return collisionCooldown == 0;
    }

    public void resetCooldown() {
        collisionCooldown = 2;
    }

    public void draw(PApplet app) {
        // app.image(image, x * App.CELLSIZE, (y + 2) * App.CELLSIZE, App.CELLSIZE, App.CELLSIZE);
        app.image(image, x, y, App.CELLSIZE, App.CELLSIZE);
        // app.fill(255, 0, 0);
        // app.ellipse(x + App.CELLSIZE / 2, y + App.CELLSIZE / 2, 5, 5);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getDx() {
        return dx;
    }

    public float getDy() {
        return dy;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setDx(float dx) {
        this.dx = dx;
    }

    public void setDy(float dy) {
        this.dy = dy;
    }

    public PImage getImage() {
        return image;
    }

    public void applyForce(float fx, float fy) {
        this.dx += fx;
        this.dy += fy;
        System.out.println("Ball velocity updated to: (" + dx + ", " + dy + ")");
    }
}
