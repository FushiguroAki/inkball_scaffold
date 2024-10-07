package inkball.utils;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.HashMap;
import java.util.Map;

// load elements' image resources from file path
public class ResourceManager {
    private PApplet app;
    private Map<String, PImage> images;

    public ResourceManager(PApplet app) {
        this.app = app;
        images = new HashMap<>();
        loadResources();
    }

    private void loadResources() {
        images.put("tile", app.loadImage("src/main/resources/inkball/tile.png"));

        for (int i = 0; i <= 4; i++) {
            images.put("wall" + i, app.loadImage("src/main/resources/inkball/wall" + i + ".png"));
        }

        images.put("spawner", app.loadImage("src/main/resources/inkball/entrypoint.png"));

        for (int i = 0; i <= 4; i++) {
            images.put("hole" + i, app.loadImage("src/main/resources/inkball/hole" + i + ".png"));
        }

        images.put("grey", app.loadImage("src/main/resources/inkball/ball0.png"));
        images.put("orange", app.loadImage("src/main/resources/inkball/ball1.png"));
        images.put("blue", app.loadImage("src/main/resources/inkball/ball2.png"));
        images.put("green", app.loadImage("src/main/resources/inkball/ball3.png"));
        images.put("yellow", app.loadImage("src/main/resources/inkball/ball4.png"));
    }

    public PImage getImage(String key) {
        return images.get(key);
    }
}