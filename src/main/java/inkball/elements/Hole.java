package inkball.elements;

import inkball.App;
import processing.core.PApplet;
import processing.core.PImage;

public class Hole {
    private int x, y;
    private PImage image;

    public Hole(int x, int y, PImage image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public void draw(PApplet app) {
        app.image(image, x * App.CELLSIZE, (y + 2) * App.CELLSIZE, App.CELLSIZE * 2, App.CELLSIZE * 2);  // Holes take 2x2 space
    }

    public void attractBall(Ball ball) {
        float holeX = x * App.CELLSIZE + App.CELLSIZE;
        float holeY = (y + 2) * App.CELLSIZE + App.CELLSIZE;
        
        float ballCenterX = ball.getX() + ball.getRadius();
        float ballCenterY = ball.getY() + ball.getRadius();
        
        float dx = holeX - ballCenterX;
        float dy = holeY - ballCenterY;
        float distance = PApplet.sqrt(dx * dx + dy * dy);

        // Check if the ball is within 32 pixels of the center of the hole
        if (distance <= 32 && distance > 0) {
            // float forceMagnitude = 0.005f / distance;
            float forceMagnitude = 2f / distance; // Increase force as distance decreases
            float forceX = dx * forceMagnitude;
            float forceY = dy * forceMagnitude;

            System.out.println("Attracting ball with force: (" + forceX + ", " + forceY + ")");
            ball.applyForce(forceX, forceY);
        }
    }
}
