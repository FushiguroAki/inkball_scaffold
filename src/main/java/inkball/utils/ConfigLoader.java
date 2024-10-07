package inkball.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import inkball.elements.*;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;

public class ConfigLoader {
    private JSONObject config;
    private PApplet app;

    private static final String[] COLOR_MAPPINGS = {"grey", "orange", "blue", "green", "yellow"};

    public ConfigLoader(String configPath, PApplet app) {
        this.config = app.loadJSONObject(configPath);
        this.app = app;
    }

    /**
     * Load level configuration from config file
     * @param walls
     * @param spawners
     * @param holes
     * @param unspawnedBalls
     * @param balls
     * @param resourceManager
     * @param app
     */
    public void loadLevelConfig(List<Wall> walls, List<Spawner> spawners, List<Hole> holes, List<String> unspawnedBalls,List<Ball> balls, ResourceManager resourceManager, PApplet app ) {
        JSONObject levelConfig = config.getJSONArray("levels").getJSONObject(1);  // Load first level
        String layout = levelConfig.getString("layout");
        JSONArray ballsConfig = levelConfig.getJSONArray("balls");

        // Add ball colors from ballsConfig to unspawnedBalls queue
        for (int i = 0; i < ballsConfig.size(); i++) {
            unspawnedBalls.add(ballsConfig.getString(i));
        }

        // Load level layout
        loadLevel(layout, walls, spawners, holes, balls, resourceManager);
    }

    // Get ball spawn interval
    public float getSpawnInterval() {
        JSONObject levelConfig = config.getJSONArray("levels").getJSONObject(0);
        return levelConfig.getFloat("spawn_interval");
    }

    // Get level time
    public int getLevelTime() {
        JSONObject levelConfig = config.getJSONArray("levels").getJSONObject(0);
        return levelConfig.getInt("time");
    }

    /**
     * Load level layout from file
     * @param levelPath
     * @param walls
     * @param spawners
     * @param holes
     * @param balls
     * @param resourceManager
     */
    public void loadLevel(String levelPath, List<Wall> walls, List<Spawner> spawners, List<Hole> holes, List<Ball> balls, ResourceManager resourceManager)  {
        BufferedReader reader = app.createReader(levelPath);
        if (reader == null) {
            System.err.println("Fail to load level file: " + levelPath);
            return;
        }

        try {
            String line;
            int y = 0; // Row index

            // Go through each line of the level file
            while ((line = reader.readLine()) != null) {
                int x = 0; // Column index

                while (x < line.length()) {
                    char tile = line.charAt(x);

                    switch (tile) {
                        case 'X':   // Wall
                            walls.add(new Wall(x, y, resourceManager.getImage("wall0")));
                            x++;
                            break;

                        case '1':   // Wall color
                        case '2':
                        case '3':
                        case '4':
                            int wallIndex = Character.getNumericValue(tile);
                            walls.add(new Wall(x, y, resourceManager.getImage("wall" + wallIndex)));
                            x++;
                            break;

                        case 'S':   // Spawner
                            spawners.add(new Spawner(x, y, resourceManager.getImage("spawner")));
                            x++;
                            break;

                            case 'H':   // Hole followed by a number indicating color
                            if (x + 1 < line.length()) {
                                char holeColorChar = line.charAt(x + 1);
                                int holeColorIndex = Character.getNumericValue(holeColorChar);
                                if (holeColorIndex >= 0 && holeColorIndex < COLOR_MAPPINGS.length) {
                                    PImage holeImage = resourceManager.getImage("hole" + holeColorIndex);
                                    holes.add(new Hole(x, y, holeImage));
                                    x += 2; // Skip the next character as it's part of the hole definition
                                } else {
                                    System.err.println("Invalid hole color at position (" + x + ", " + y + ")");
                                    x++;
                                }
                            } else {
                                System.err.println("Incomplete hole definition at end of line " + y);
                                x++;
                            }
                            break;

                            case 'B':   // ball followed by a number indicating color
                            if (x + 1 < line.length()) {
                                char ballColorChar = line.charAt(x + 1);
                                int ballColorIndex = Character.getNumericValue(ballColorChar);
                                if (ballColorIndex >= 0 && ballColorIndex < COLOR_MAPPINGS.length) {
                                    String colorName = COLOR_MAPPINGS[ballColorIndex];
                                    PImage ballImage = resourceManager.getImage(colorName);
                                    if (ballImage != null) {
                                        balls.add(new Ball(x, y, ballImage));
                                    } else {
                                        System.err.println("Ball image not found for color: " + colorName);
                                    }
                                    x += 2; // Skip the next character as it's part of the ball definition
                                } else {
                                    System.err.println("Invalid ball color at position (" + x + ", " + y + ")");
                                    x++;
                                }
                            } else {
                                System.err.println("Incomplete ball definition at end of line " + y);
                                x++;
                            }
                            break;

                        default:
                            // Space as empty space
                            x++;
                            break;
                    }
                }
                y++;
            }
            reader.close();
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
