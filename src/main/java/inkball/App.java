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
import org.checkerframework.checker.units.qual.s;

import inkball.elements.*;
import inkball.elements.Ball;
import inkball.elements.Hole;
import inkball.elements.Spawner;
import inkball.elements.Wall;
import inkball.utils.*;

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

    private static final int MAX_UNSPAWNED_DISPLAY = 5; // max num of unspawned balls show on top left
    private static final int MOVE_SPEED = 1;  // unspawned ball move anima speed 1px/f

	private List<Wall> walls;
    private List<Spawner> spawners;
    private List<Hole> holes;
    private List<Ball> balls;
    
    private PImage[] wallImages;
    private PImage spawnerImage;
    private PImage[] holeImages;
    private PImage tileImage;
    
    private float spawnInterval;  // time interval between ball spawn
    private long lastSpawnTime;  // last time a ball spawn
   
    private Queue<String> unspawnedBallsQueue;
    private List<UnspawnedBall> displayedUnspawnedBalls;
    private Map<String, PImage> ballImagesMap;
    private boolean isAnimating = false;  // flag for top left unspawned ball move animation
    private int slotOffset = 0; // offset of the slot during animation
    private int animationProgress = 0; // tracks progress of the animation

    private int score;
    private int timeLeft;   // level time left
    private long lastTimeUpdate;  // manage time decrement

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
        loadLevelAndBalls();
        spawnNextBall();    // spawn first ball immediately
        lastSpawnTime = millis();  // record start time
		
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

        ballImagesMap = new HashMap<>();
        ballImagesMap.put("grey", loadImage("src/main/resources/inkball/ball0.png"));
        ballImagesMap.put("orange", loadImage("src/main/resources/inkball/ball1.png"));
        ballImagesMap.put("blue", loadImage("src/main/resources/inkball/ball2.png"));
        ballImagesMap.put("green", loadImage("src/main/resources/inkball/ball3.png"));
        ballImagesMap.put("yellow", loadImage("src/main/resources/inkball/ball4.png"));
    }

    /**
     * Load the level and ball based on the layout file
     */
    private void loadLevelAndBalls() {
        ConfigLoader configLoader = new ConfigLoader("config.json", this);
        walls = new ArrayList<>();
        spawners = new ArrayList<>();
        holes = new ArrayList<>();
        balls = new ArrayList<>();
        unspawnedBallsQueue = new LinkedList<>();
        displayedUnspawnedBalls = new ArrayList<>();

        // elements from config file and level file
        configLoader.loadLevelConfig(walls, spawners, holes, unspawnedBallsQueue, wallImages, holeImages, spawnerImage, tileImage, CELLSIZE, balls, ballImagesMap);
        spawnInterval = configLoader.getSpawnInterval();  // ball spawn interval
        timeLeft = configLoader.getLevelTime();  // set initial level time left

        score = 0;

        lastTimeUpdate = millis() / 1000;  // convert to seconds

        // Initialize displayedUnspawnedBalls
        int initialDisplayCount = Math.min(unspawnedBallsQueue.size(), MAX_UNSPAWNED_DISPLAY);
        for (int i = 0; i < initialDisplayCount; i++) {
            String color = unspawnedBallsQueue.poll();
            PImage ballImage = ballImagesMap.get(color);
            displayedUnspawnedBalls.add(new UnspawnedBall(color, ballImage));
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
        // Update timeLeft every second
        long currentTime = millis() / 1000; // Get current time in seconds
        if (currentTime - lastTimeUpdate >= 1) {
            timeLeft--;
            lastTimeUpdate = currentTime;
        }

        // timeLeft doesn't go below zero
        if (timeLeft < 0) {
            timeLeft = 0;
            // TODO handle end of level or game over
        }

        // Check if time to start animation
        if (!isAnimating && !displayedUnspawnedBalls.isEmpty() && millis() - lastSpawnTime >= spawnInterval * 1000) {
            isAnimating = true;
            lastSpawnTime = millis();
            animationProgress = 0;
            slotOffset = 0;
        }

        if (isAnimating) {
            boolean animationComplete = updateUnspawnedBallsAnimation();
    
            if (animationComplete) {
                isAnimating = false;
                // after animation, remove the first ball and adjust displayedUnspawnedBalls
                spawnNextBall();
            }
        }

        drawUnspawnedBalls();

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

        for (Ball ball : balls) {
            ball.draw(this);
        }

        //----------------------------------
        //display score
        //----------------------------------
        drawScoreAndTime();
        
		//----------------------------------
        //----------------------------------
		//display game end message

    }

    /**
     * update unspawned balls move animation, return true if animation complete
     */
    private boolean updateUnspawnedBallsAnimation() {
        animationProgress += MOVE_SPEED;
        slotOffset -= MOVE_SPEED;
    
        if (animationProgress >= 32) {
            // Animation completed
            slotOffset = 0; // reset slotOffset for next cycle
            return true;
        } else {
            return false;
        }
    }

    /**
     * draw unspawned balls on top left
     */
    private void drawUnspawnedBalls() {
        int yOffset = 20;
    
        for (int i = 0; i < displayedUnspawnedBalls.size(); i++) {
            UnspawnedBall ub = displayedUnspawnedBalls.get(i);
            int xOffset = 5 + i * 32 + slotOffset;
            ub.draw(this, xOffset, yOffset);
        }
    }

    /**
     * spawn next ball from unspawned balls
     */
    private void spawnNextBall() {
        if (!displayedUnspawnedBalls.isEmpty()) {
            // Remove the first ball and spawn it
            UnspawnedBall nextBall = displayedUnspawnedBalls.remove(0);
            PImage ballImage = ballImagesMap.get(nextBall.getColor());
    
            // Randomly choose a spawner
            int randomSpawnerIndex = (int) random(spawners.size());
            Spawner spawner = spawners.get(randomSpawnerIndex);
    
            // Spawn the ball at the spawner
            balls.add(new Ball(spawner.getX(), spawner.getY(), ballImage));
        }
    
        // Add a new ball to the display list if there are more in the queue
        if (displayedUnspawnedBalls.size() < MAX_UNSPAWNED_DISPLAY && !unspawnedBallsQueue.isEmpty()) {
            String color = unspawnedBallsQueue.poll();
            PImage ballImage = ballImagesMap.get(color);
            displayedUnspawnedBalls.add(new UnspawnedBall(color, ballImage));
        }
    }

    /**
     * draw score and timer on top right
     */
    private void drawScoreAndTime() {
        fill(0); // text color black
        textSize(20);
    
        String scoreText = "Score: " + score;
        String timeText = "Time: " + timeLeft;
    
        // Calculate positions based on the screen width
        float scoreX = width - textWidth(scoreText) - 20;
        float scoreY = 25;
    
        float timeX = width - textWidth(timeText) - 20;
        float timeY = 55;
    
        text(scoreText, scoreX, scoreY);
        text(timeText, timeX, timeY);
    }



    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }

}