package inkball.elements;

import inkball.App;
import inkball.utils.*;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.List;
import java.util.ArrayList;

public class Board {
    private List<Wall> walls;
    private List<Spawner> spawners;
    private List<Hole> holes;
    private List<Ball> balls;
    private ResourceManager resourceManager;

    private PApplet app;
    private List<Line> lines = new ArrayList<>();
    private Line currentLine = null;

    public Board(PApplet app, ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        walls = new ArrayList<>();
        spawners = new ArrayList<>();
        holes = new ArrayList<>();
        balls = new ArrayList<>();
        this.app = app;
    }


    public void loadLevel(ConfigLoader configLoader, List<String> unspawnedBalls, PApplet app) {
        configLoader.loadLevelConfig(walls, spawners, holes, unspawnedBalls, balls, resourceManager, app);
    }
    
    public void update() {
        for (Ball ball : balls) {
            ball.move();
            ball.updateCooldown();
        }
        // TODO handle collisions
        CollisionManager.handleCollisions(balls, walls, holes);
    }

    public void draw(PApplet app) {
        for (Wall wall : walls) {
            wall.draw(app);
        }

        for (Spawner spawner : spawners) {
            spawner.draw(app);
        }

        for (Hole hole : holes) {
            hole.draw(app);
        }

        for (Ball ball : balls) {
            ball.draw(app);
        }

        drawLines();
    }
    
    public List<Spawner> getSpawners() {
        return spawners;
    }

    public List<Ball> getBalls() {
        return balls;
    }

    private void drawLines() {
        app.strokeWeight(10);
        app.stroke(0);
        app.noFill();

        for (Line line : lines) {
            List<PVector> points = line.getPoints();
            if (points.size() > 1) {
                app.beginShape();
                for (PVector point : points) {
                    app.vertex(point.x, point.y);
                }
                app.endShape();
            }
        }
    }

    public void mousePressed() {
        float mouseX = app.mouseX;
        float mouseY = app.mouseY;

        // create a new player-drawn line object
        if (app.mouseButton == PApplet.LEFT && isInGameArea(mouseX, mouseY)) {
            // create a new line object
            currentLine = new Line(10, app.color(0));
            currentLine.addPoint(app.mouseX, app.mouseY);
            lines.add(currentLine);
        }
    }
	
    public void mouseDragged() {
        float mouseX = app.mouseX;
        float mouseY = app.mouseY;
        
        // add line segments to player-drawn line object if left mouse button is held
		if (app.mouseButton == PApplet.LEFT && currentLine != null && isInGameArea(mouseX, mouseY)) {
            currentLine.addPoint(app.mouseX, app.mouseY);   // add point to the current line
        }
    }

    public void mouseReleased() {
        // finish player-drawn line object if left mouse button is released
        if (app.mouseButton == PApplet.LEFT) {
            currentLine = null; // finish the current line
        }
    }

    /**
     * check mouse y is in game area
     */
    private boolean isInGameArea(float x, float y) {
    float gameAreaTop = 2 * App.CELLSIZE;
    float gameAreaLeft = 0;
    float gameAreaRight = app.width;
    float gameAreaBottom = app.height;

    return x >= gameAreaLeft && x <= gameAreaRight && y >= gameAreaTop && y <= gameAreaBottom;
}

}