package inkball.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import inkball.elements.*;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;

public class ConfigLoader {
    private JSONObject config;
    private PApplet app;

    public ConfigLoader(String configPath, PApplet app) {
        this.config = app.loadJSONObject(configPath);
        this.app = app;
    }

    // load level layout from file
    public void loadLevel(String levelPath, List<Wall> walls, List<Spawner> spawners, List<Hole> holes, PImage[] wallImages, PImage[] holeImages, PImage spawnerImage, PImage tileImage, int cellSize) {
        try (BufferedReader reader = new BufferedReader(new FileReader(levelPath))) {
            String line;
            int y = 0; // raw index

            // go through each line of level file
            while ((line = reader.readLine()) != null) {
                for (int x = 0; x < line.length(); x++) {
                    char tile = line.charAt(x);

                    switch (tile) {
                        case 'X': 
                            walls.add(new Wall(x, y, wallImages[0]));
                            break;
                        case '1': 
                            walls.add(new Wall(x, y, wallImages[1]));
                            break;
                        case '2': 
                            walls.add(new Wall(x, y, wallImages[2]));
                            break;
                        case '3': 
                            walls.add(new Wall(x, y, wallImages[3]));
                            break;
                        case '4':  
                            walls.add(new Wall(x, y, wallImages[4]));
                            break;
                        case 'S': 
                            spawners.add(new Spawner(x, y, spawnerImage));
                            break;
                        case 'H':  // Hole with right number refers to color
                            int holeColor = Character.getNumericValue(line.charAt(x + 1));
                            holes.add(new Hole(x, y, holeImages[holeColor]));
                            break;
                        default:
                            // space, draw tile
                            app.image(tileImage, x * cellSize, (y + 2) * cellSize, cellSize, cellSize);
                    }
                }
                y++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // init ball on spawner
    public void loadBallsAtSpawners(List<Ball> balls, List<Spawner> spawners, PImage[] ballImages) {
        for (int i = 0; i < spawners.size(); i++) {
            Spawner spawner = spawners.get(i);
            PImage ballImage = ballImages[i % ballImages.length];  // init based on color
            balls.add(new Ball(spawner.getX(), spawner.getY(), ballImage)); 
        }
    }


}
