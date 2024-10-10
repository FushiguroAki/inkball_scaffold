package inkball.elements;

import inkball.utils.*;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.List;
import java.util.ArrayList;

public class Board {
    private List<Wall> walls;
    private List<Spawner> spawners;
    private List<Hole> holes;
    private List<Ball> balls;
    private ResourceManager resourceManager;

    public Board(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        walls = new ArrayList<>();
        spawners = new ArrayList<>();
        holes = new ArrayList<>();
        balls = new ArrayList<>();
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
    }
    
    public List<Spawner> getSpawners() {
        return spawners;
    }

    public List<Ball> getBalls() {
        return balls;
    }


}