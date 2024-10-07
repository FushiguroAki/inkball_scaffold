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
    private Board board;
    private ResourceManager resourceManager;
    private ConfigLoader configLoader;

    public static Random random = new Random();

    private static final int MAX_UNSPAWNED_DISPLAY = 5; // max num of unspawned balls show on top left
    private static final int MOVE_SPEED = 1;  // unspawned ball move anima speed 1px/f    
    
    private float spawnInterval;  // time interval between ball spawn
    private long lastSpawnTime;  // last time a ball spawn
   
    private List<String> unspawnedBallsQueue;
    private List<UnspawnedBall> displayedUnspawnedBalls;
    private boolean isAnimating = false;  // flag for top left unspawned ball move animation
    private int slotOffset = 0; // offset of the slot during animation
    private int animationProgress = 0; // tracks progress of the animation

    private int score;
    private int timeLeft;   // level time left
    private long lastTimeUpdate;  // manage time decrement

    private float spawnTimer;   // timer for ball spawn
    private long lastTimerUpdate;   // manage timer decrement 0.1 seconds

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
     * Load all resources such as images. Initialise the elements
     */
	@Override
    public void setup() {
        frameRate(FPS);
        resourceManager = new ResourceManager(this);
        configLoader = new ConfigLoader(configPath, this);
        board = new Board(resourceManager);
        unspawnedBallsQueue = new ArrayList<>();
        displayedUnspawnedBalls = new ArrayList<>();
        loadLevelAndBalls();
        spawnNextBall();    // spawn first ball immediately
        lastSpawnTime = millis();  // record start time
		lastTimeUpdate = millis() / 1000;
        spawnTimer = spawnInterval;
        lastTimerUpdate = millis();
    }


    /**
     * Load the level and ball based on the layout file
     */
    private void loadLevelAndBalls() {
        // elements from config file and level file
        board.loadLevel(configLoader, unspawnedBallsQueue, this);
        spawnInterval = configLoader.getSpawnInterval();  // ball spawn interval
        timeLeft = configLoader.getLevelTime();  // set initial level time left

        score = 0;

        // Initialize displayedUnspawnedBalls
        int initialDisplayCount = Math.min(unspawnedBallsQueue.size(), MAX_UNSPAWNED_DISPLAY);
        for (int i = 0; i < initialDisplayCount; i++) {
            String color = unspawnedBallsQueue.remove(0);
            PImage ballImage = resourceManager.getImage(color);
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

        if (isAnimating) {
            boolean animationComplete = updateUnspawnedBallsAnimation();
    
            if (animationComplete) {
                isAnimating = false;
                // after animation, remove the first ball and adjust displayedUnspawnedBalls
                spawnNextBall();
            }
        }

        updateSpawnTimer();

        drawUnspawnedBalls();

        // Draw tiles as background
        PImage tileImage = resourceManager.getImage("tile");
        for (int y = 0; y < HEIGHT / CELLSIZE; y++) {
            for (int x = 0; x < WIDTH / CELLSIZE; x++) {
                image(tileImage, x * CELLSIZE, (y + 2) * CELLSIZE, CELLSIZE, CELLSIZE);
            }
        }

        // Update and draw on board elements
        board.draw(this);

        //----------------------------------
        //display score
        //----------------------------------
        drawScoreAndTime();

        drawSpawnTimer();
        
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
    
        if (animationProgress >= CELLSIZE) {
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
        int yOffset = 16;
    
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
            PImage ballImage = resourceManager.getImage(nextBall.getColor());
    
            // Randomly choose a spawner
            List<Spawner> spawners = board.getSpawners();
            if (!spawners.isEmpty()) {
                int randomSpawnerIndex = (int) random(spawners.size());
                Spawner spawner = spawners.get(randomSpawnerIndex);

                // Spawn the ball at the spawner
                board.getBalls().add(new Ball(spawner.getX(), spawner.getY(), ballImage));
            }
        }
    
        // Add a new ball to the display list if there are more in the queue
        if (displayedUnspawnedBalls.size() < MAX_UNSPAWNED_DISPLAY && !unspawnedBallsQueue.isEmpty()) {
            String color = unspawnedBallsQueue.remove(0);
            PImage ballImage = resourceManager.getImage(color);
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
        float scoreY = 27;
    
        float timeX = width - textWidth(timeText) - 20;
        float timeY = 55;
    
        text(scoreText, scoreX, scoreY);
        text(timeText, timeX, timeY);
    }

    /**
     * update spawn timer every 0.1 seconds，
     * spawn a ball and animation when timer reaches 0
     */
    private void updateSpawnTimer() {
        // calculate the time difference between current time and the last update time
        long currentTime = millis();
        if (currentTime - lastTimerUpdate >= 100) {
            spawnTimer -= 0.1f;
            lastTimerUpdate = currentTime;
    
            if (spawnTimer < 0) {
                spawnTimer = 0;
            }
        }
    
        // when timer reaches 0, reset timer and start animation
        if (spawnTimer <= 0 && !isAnimating && !displayedUnspawnedBalls.isEmpty()) {
            isAnimating = true;
            lastSpawnTime = millis();
            animationProgress = 0;
            slotOffset = 0;
            spawnTimer = spawnInterval; // reset timer
        }
    }

    /**
     * draw spawn timer on top left
     */
    private void drawSpawnTimer() {
        fill(0); // text color black
        textSize(16);
    
        String timerText = String.format("%.1f s", spawnTimer);
    
        // position
        int xOffset = 170;
        int yOffset = 38;
    
        text(timerText, xOffset, yOffset);
    }

    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }

}