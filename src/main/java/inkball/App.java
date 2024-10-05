package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.util.*;

import org.checkerframework.checker.units.qual.h;

import inkball.elements.*;
import inkball.elements.Hole;
import inkball.elements.Spawner;
import inkball.elements.Wall;

public class App extends PApplet {

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 2 * CELLSIZE;
    public static int WIDTH = 576; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 30;

    public String configPath;

    public static Random random = new Random();
	
	private List<Wall> walls;
    private List<Spawner> spawners;
    private List<Hole> holes;

    private PImage[] wallImages;
    private PImage spawnerImage;
    private PImage[] holeImages;
    private PImage tileImage;

    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
	@Override
    public void setup() {
        frameRate(FPS);
        loadResources();
        loadLevel("level1.txt");
		//See PApplet javadoc:
		//loadJSONObject(configPath)
		// the image is loaded from relative path: "src/main/resources/inkball/..."
		/*try {
            result = loadImage(URLDecoder.decode(this.getClass().getResource(filename+".png").getPath(), StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }*/
    }

    /**
     * load elements' image resources from file path
     */
    private void loadResources() {
        tileImage = loadImage("src/main/resources/inkball/tile.png");

        wallImages = new PImage[5];
        wallImages[0] = loadImage("src/main/resources/inkball/wall0.png");
        wallImages[1] = loadImage("src/main/resources/inkball/wall1.png");
        wallImages[2] = loadImage("src/main/resources/inkball/wall2.png");
        wallImages[3] = loadImage("src/main/resources/inkball/wall3.png");
        wallImages[4] = loadImage("src/main/resources/inkball/wall4.png");

        spawnerImage = loadImage("src/main/resources/inkball/entrypoint.png");

        holeImages = new PImage[5];
        holeImages[0] = loadImage("src/main/resources/inkball/hole0.png");
        holeImages[1] = loadImage("src/main/resources/inkball/hole1.png");
        holeImages[2] = loadImage("src/main/resources/inkball/hole2.png");
        holeImages[3] = loadImage("src/main/resources/inkball/hole3.png");
        holeImages[4] = loadImage("src/main/resources/inkball/hole4.png");
        
    }

    /**
     * Load the level based on the layout file
     */
    private void loadLevel(String levelPath) {
        walls = new ArrayList<>();
        spawners = new ArrayList<>();
        holes = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(levelPath))) {
            String line;
            int y = 0;
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
                        case 'S':  // Spawner
                            spawners.add(new Spawner(x, y, spawnerImage));
                            break;
                        case 'H':  // Hole
                            int holeColor = Character.getNumericValue(line.charAt(x + 1));  // get the number next to H, color
                            holes.add(new Hole(x, y, holeImages[holeColor]));
                            break;
                        default:
                            // empty spaces, tile
                            image(tileImage, x * CELLSIZE, y * CELLSIZE, CELLSIZE, CELLSIZE);
                    }
                }
                y++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // create a new player-drawn line object
    }
	
	@Override
    public void mouseDragged(MouseEvent e) {
        // add line segments to player-drawn line object if left mouse button is held
		
		// remove player-drawn line object if right mouse button is held 
		// and mouse position collides with the line
    }

    @Override
    public void mouseReleased(MouseEvent e) {
		
    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        

        //----------------------------------
        //display Board for current level:
        //----------------------------------
        background(200);    // clear and set background color gray

        // TODO draw TOPBAR

        // draw from the 3rd row
        // draw tiles as background
        for (int y = 0; y < HEIGHT / CELLSIZE; y++) {
            for (int x = 0; x < WIDTH / CELLSIZE; x++) {
                image(tileImage, x * CELLSIZE, (y + 2) * CELLSIZE, CELLSIZE, CELLSIZE);
            }
        }

        // draw other elements
        for (Wall wall : walls) {
            wall.draw(this);
        }

        for (Spawner spawner : spawners) {
            spawner.draw(this);
        }

        for (Hole hole : holes) {
            hole.draw(this);
        }

        //----------------------------------
        //display score
        //----------------------------------
        //TODO
        
		//----------------------------------
        //----------------------------------
		//display game end message

    }


    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }

}