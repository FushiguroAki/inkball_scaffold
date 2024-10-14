package inkball.utils;

import inkball.elements.*;
import processing.core.PApplet;
import processing.core.PVector;
import inkball.App;

import java.util.List;

public class CollisionManager {

    private static final float LINE_WIDTH = 10f;

    public static void handleCollisions(List<Ball> balls, List<Wall> walls, List<Hole> holes) {
        for (Ball ball : balls) {
            // check and handle collisions between balls and walls
            for (Wall wall : walls) {
                checkCollisionWithWall(ball, wall);
            }

            // check and handle collisions between balls
            checkCollisionBetweenBalls(balls);

            // collisions between balls and holes
            // for (Hole hole : holes) {
            //     checkCollisionWithHole(ball, hole);
            // }
        }
        
    }

    private static void checkCollisionWithWall(Ball ball, Wall wall) {
        // 获取球和墙的边界
        float ballLeft = ball.getX();
        float ballRight = ballLeft + App.CELLSIZE;
        float ballTop = ball.getY();
        float ballBottom = ballTop + App.CELLSIZE;
    
        float wallLeft = wall.getX() * App.CELLSIZE;
        float wallRight = wallLeft + App.CELLSIZE;
        float wallTop = (wall.getY() + 2) * App.CELLSIZE;
        float wallBottom = wallTop + App.CELLSIZE;
    
        // check if collision
        if (ballRight > wallLeft && ballLeft < wallRight && ballBottom > wallTop && ballTop < wallBottom) {
            // calculate overlap in each direction
            float overlapLeft = ballRight - wallLeft;
            float overlapRight = wallRight - ballLeft;
            float overlapTop = ballBottom - wallTop;
            float overlapBottom = wallBottom - ballTop;
    
            // find the smallest overlap
            float minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));
    
            // get direction of collision based on min overlap
            if (minOverlap == overlapLeft) {
                // collision on left side of wall
                ball.setX(wallLeft - App.CELLSIZE);
                ball.setDx(-ball.getDx()); // reverse x component
            } else if (minOverlap == overlapRight) {
                // on right side of wall
                ball.setX(wallRight);
                ball.setDx(-ball.getDx()); // reverse x
            } else if (minOverlap == overlapTop) {
                // on top side of wall
                ball.setY(wallTop - App.CELLSIZE);
                ball.setDy(-ball.getDy()); // reverse y
            } else if (minOverlap == overlapBottom) {
                // on bottom side of wall
                ball.setY(wallBottom);
                ball.setDy(-ball.getDy()); // reverse y
            }
        }
    }

    /**
     * distance between two balls' centers <= radiusSum, collision happens
     * @param balls
     */
    private static void checkCollisionBetweenBalls(List<Ball> balls) {
        int size = balls.size();
        for (int i = 0; i < size; i++) {
            Ball ballA = balls.get(i);
            for (int j = i + 1; j < size; j++) {
                Ball ballB = balls.get(j);

                        // check if collision
                        float dx = (ballA.getX() + App.CELLSIZE / 2) - (ballB.getX() + App.CELLSIZE / 2);
                        float dy = (ballA.getY() + App.CELLSIZE / 2) - (ballB.getY() + App.CELLSIZE / 2);
                        float distanceSquared = dx * dx + dy * dy;
                        float radiusSum = App.CELLSIZE / 2 + App.CELLSIZE / 2;

                        if (distanceSquared <= radiusSum * radiusSum) {
                            // change velocity
                            resolveBallCollision(ballA, ballB);
                        }

                
            }
        }
    }

    private static void resolveBallCollision(Ball ballA, Ball ballB) {
        // get velocity of two balls
        float vxA = ballA.getDx();
        float vyA = ballA.getDy();
        float vxB = ballB.getDx();
        float vyB = ballB.getDy();

        // calculate normal vector
        float dx = (ballA.getX() + App.CELLSIZE / 2) - (ballB.getX() + App.CELLSIZE / 2);
        float dy = (ballA.getY() + App.CELLSIZE / 2) - (ballB.getY() + App.CELLSIZE / 2);
        float distanceSquared = dx * dx + dy * dy;

        if (distanceSquared == 0) {
            return;
        }

        float distance = (float) Math.sqrt(distanceSquared);

        // normalize the normal vector
        float nx = dx / distance;
        float ny = dy / distance;

        // relative velocity
        float vxRel = vxA - vxB;
        float vyRel = vyA - vyB;

        // projection of the relative velocity in the direction of normal vector
        float vRelDotN = vxRel * nx + vyRel * ny;

        // if vRelDotN > 0, ball is moving away, no need to handle 
        if (vRelDotN > 0) {
            return;
        }

        // m1 = m2 = 1
        float impulse = -2 * vRelDotN / 2;

        // impulse vector
        float impulseX = impulse * nx;
        float impulseY = impulse * ny;

        // set new velocity
        ballA.setDx(vxA + impulseX);
        ballA.setDy(vyA + impulseY);
        ballB.setDx(vxB - impulseX);
        ballB.setDy(vyB - impulseY);
    }

    /**
     * ball and line collision
     * @param ball
     * @param lines
     */
    public static void handleBallLineCollisions(List<Ball> balls, List<Line> lines) {
        for (Ball ball : balls) {
            checkCollisionWithLines(ball, lines);
        }
    }

    private static void checkCollisionWithLines(Ball ball, List<Line> lines) {
        for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
            Line line = lines.get(lineIndex);
            List<PVector> points = line.getPoints();
            for (int i = 0; i < points.size() - 1; i++) {
                PVector p1 = points.get(i);
                PVector p2 = points.get(i + 1);

                // Calculate the distance from the ball's center to the line segment
                float distance = pointToSegmentDistance(ball.getX() + ball.getRadius(), ball.getY() + ball.getRadius(), p1.x, p1.y, p2.x, p2.y);

                // Check if the distance is less than or equal to the sum of the ball's radius and half the line's thickness
                if (distance <= ball.getRadius() + line.getThickness() / 2) {
                    // Calculate normal vector
                    float dx = p2.x - p1.x;
                    float dy = p2.y - p1.y;
                    PVector normal = new PVector(-dy, dx);
                    normal.normalize();

                    // Check if the line is nearly vertical
                    if (Math.abs(dx) < Math.abs(dy)) {
                        // Treat as a vertical wall collision
                        ball.reverseDirectionX();
                    } else {
                        // Treat as a horizontal wall collision
                        ball.reverseDirectionY();
                    }

                    // Reset collision cooldown
                    ball.resetCooldown();

                    // Remove the line after collision
                    lines.remove(lineIndex);
                    return; // Exit after removing the line to avoid concurrent modification
                }
            }
        }
    }

    // Helper method to calculate the shortest distance from a point to a line segment
    private static float pointToSegmentDistance(float px, float py, float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;

        if (dx == 0 && dy == 0) {
            dx = px - x1;
            dy = py - y1;
            return PApplet.sqrt(dx * dx + dy * dy);
        }

        float t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);

        if (t < 0) {
            dx = px - x1;
            dy = py - y1;
        } else if (t > 1) {
            dx = px - x2;
            dy = py - y2;
        } else {
            float nearX = x1 + t * dx;
            float nearY = y1 + t * dy;
            dx = px - nearX;
            dy = py - nearY;
        }

        return PApplet.sqrt(dx * dx + dy * dy);
    }

    private static void checkCollisionWithHole(Ball ball, Hole hole) {
        // TODO: Implement collision detection between ball and hole
        
    }


}
