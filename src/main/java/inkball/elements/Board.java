package inkball.elements;

import inkball.App;
import inkball.utils.*;
import processing.core.PApplet;
import processing.core.PConstants;
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

    @SuppressWarnings("deprecation")
    public void mousePressed() {
        float mouseX = app.mouseX;
        float mouseY = app.mouseY;
        boolean isCtrlOrCommandPressed = false;

        if (app.keyEvent != null) {
            isCtrlOrCommandPressed = app.keyEvent.isControlDown() || app.keyEvent.isMetaDown();
        }

        // create a new player-drawn line object
        if (app.mouseButton == PApplet.LEFT && isInGameArea(mouseX, mouseY)) {
            // create a new line object
            currentLine = new Line(10, app.color(0));
            currentLine.addPoint(mouseX, mouseY);
            lines.add(currentLine);
        } // ctrl + left or right to delete line
        else if ((app.mouseButton == PConstants.LEFT && isCtrlOrCommandPressed) ||
        app.mouseButton == PConstants.RIGHT) {
            // System.out.println("Key Code: " + app.keyCode);
            Line line = getLineAtPosition(mouseX, mouseY);
            if (line != null) {
                lines.remove(line);
            }
        }

        System.out.println("keyEvent: " + app.keyEvent);
        if (app.keyEvent != null) {
            System.out.println("isControlDown: " + app.keyEvent.isControlDown());
            System.out.println("isMetaDown: " + app.keyEvent.isMetaDown());
}
    }
	
    public void mouseDragged() {
        float mouseX = app.mouseX;
        float mouseY = app.mouseY;

        // add line segments to player-drawn line object if left mouse button is held
		if (app.mouseButton == PApplet.LEFT && currentLine != null && isInGameArea(mouseX, mouseY)) {
            currentLine.addPoint(mouseX, mouseY);   // add point to the current line
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

    /**
     * check the position click has a line
     */
    private Line getLineAtPosition(float x, float y) {
        // set detection threshold
        float threshold = 5.0f;
    
        // go through the line list from back to front, prioritize the newest line
        for (int i = lines.size() - 1; i >= 0; i--) {
            Line line = lines.get(i);
            List<PVector> points = line.getPoints();
    
            // go through all line segments
            for (int j = 0; j < points.size() - 1; j++) {
                PVector p1 = points.get(j);
                PVector p2 = points.get(j + 1);
    
                // shortest distance from the point to the line segment
                float distance = pointToSegmentDistance(x, y, p1.x, p1.y, p2.x, p2.y);
    
                if (distance <= threshold) {
                    return line;
                }
            }
        }
        return null;
    }

    /**
     * calculate the shortest distance from a point to a line segment
     */
    private float pointToSegmentDistance(float px, float py, float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
    
        if (dx == 0 && dy == 0) {
            // two end points of the line segment overlap
            dx = px - x1;
            dy = py - y1;
            return PApplet.sqrt(dx * dx + dy * dy);
        }
    
        // t calculate projection parameter t
        float t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);
    
        if (t < 0) {
            // the nearest point is before the line segment
            dx = px - x1;
            dy = py - y1;
        } else if (t > 1) {
            // after the line segment
            dx = px - x2;
            dy = py - y2;
        } else {
            // on the line segment
            float nearX = x1 + t * dx;
            float nearY = y1 + t * dy;
            dx = px - nearX;
            dy = py - nearY;
        }
    
        return PApplet.sqrt(dx * dx + dy * dy);
    }
}