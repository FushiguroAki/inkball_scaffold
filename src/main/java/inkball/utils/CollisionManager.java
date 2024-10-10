package inkball.utils;

import inkball.elements.*;
import inkball.App;

import java.util.List;

public class CollisionManager {

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


    private static void checkCollisionWithHole(Ball ball, Hole hole) {
        // TODO: 实现球与洞的碰撞检测
        
    }


}