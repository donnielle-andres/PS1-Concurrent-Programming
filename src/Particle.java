import java.awt.*;
import java.util.*;

public class Particle {
    private static final int particleSize = 3, PARTFRAME_WIDTH = 1280, PARTFRAME_HEIGHT = 720;
    public Point position;
    private double theta, velocity;
    private Color color;
    double moveX = 0.0;
    double moveY = 0.0;

    public Particle(int x, int y, double velocity, double theta){

        this.position = new Point (x, y);
        this.theta = theta;
        this.velocity = velocity;
        this.color = Color.WHITE;

        //System.out.printf("Particle Class: %d , %d , %f , %f ", x_part, y_part, velocity, theta);

    }

    public void draw(Graphics graphics){
        int y_part = PARTFRAME_HEIGHT - position.y - particleSize;
        graphics.setColor(color);
        graphics.fillOval(position.x, y_part, particleSize, particleSize);
        //System.out.printf("\n Particle Position: %d , %d ", position.x, y_part);
    }

// Update Particle Position
    public void updatePosition(double moveTime) {
        // Convert angle to radians for trigonometric calculations
        double radians = Math.toRadians(this.theta);

        // Calculate movement based on velocity and angle
        double movementX = this.velocity * Math.cos(radians) * moveTime;
        double movementY = this.velocity * Math.sin(radians) * moveTime;

        // Accumulate sub-pixel movements
        accumulateMovement(movementX, movementY);

        // Update position when accumulated movement exceeds 1 pixel
        applyPositionChange();
    }


    private void accumulateMovement(double movementX, double movementY) {
        moveX += movementX;
        moveY += movementY;
    }

    private void applyPositionChange() {
        int roundedX = (int) Math.round(moveX);
        int roundedY = (int) Math.round(moveY);

        this.position.x += roundedX;
        this.position.y += roundedY;

        // Reset accumulated movement after applying position change
        moveX -= roundedX;
        moveY -= roundedY;
    }


// Wall Collision - Handle Wall Collision

    public void partCollision(ArrayList<Wall> walls) {
        // Handle wall collisions
        for (Wall wall : walls) {
            if (wallCollision(wall)) {
                checkParticleBounce(wall);
            }
        }

        // Handle frame collisions
        handleHorizontalCollision();
        handleVerticalCollision();
        
        // Normalize angle
        normalizeAngle();
        //System.out.printf("\n Particle partCollision: %d , %d ", position.x, position.y);
    }
    

    private void handleHorizontalCollision() {
        int buffer = 5; // A small buffer to prevent sticking to the wall
    
        if (position.x <= 0) {
            theta = 180 - theta;
            position.x = buffer; // Move particle slightly inside to prevent sticking
        } else if (position.x + particleSize >= PARTFRAME_WIDTH) {
            theta = 180 - theta;
            position.x = PARTFRAME_WIDTH - particleSize - buffer;
        }
    }
    
    private void handleVerticalCollision() {
        int buffer = 1; // A small buffer to prevent sticking to the wall
    
        if (position.y + particleSize >= PARTFRAME_HEIGHT) {
            theta = -theta;
            position.y = PARTFRAME_HEIGHT - particleSize - buffer;
        } else if (position.y <= 0) {
            theta = -theta;
            position.y = buffer;
        }
    }

    private void normalizeAngle() {
        if (theta < 0) {
            theta += 360;
        } else if (theta > 360) {
            theta -= 360;
        }
    }

// Check Wall Collision
    private boolean wallCollision(Wall wall) {
        // Current position
        double currX = position.x;
        double currY = position.y;

        // Predicted next position based on current velocity and angle
        double moveTime = 1 / 60.0; 
        double[] nextPosition = getNextPosition(currX, currY, moveTime);

        // Wall start and end points
        double X_sw = wall.startWall.x;
        double Y_sw = wall.startWall.y;
        double X_ew = wall.endWall.x;
        double Y_ew = wall.endWall.y;

        // Calculate intersection
        return hasIntersection(currX, currY, nextPosition[0], nextPosition[1], X_sw, Y_sw, X_ew, Y_ew);
    }

    private double[] getNextPosition(double x, double y, double moveTime) {
        double angleRadians = Math.toRadians(theta);
        double velocityX = velocity * Math.cos(angleRadians);
        double velocityY = velocity * Math.sin(angleRadians);
        double nextX = x + velocityX * moveTime;
        double nextY = y + velocityY * moveTime;
        return new double[]{nextX, nextY};
    }

    private boolean hasIntersection(double currX, double currY, double nextPosiX, double nextPosiY, double X_sw, double Y_sw, double X_ew, double Y_ew) {
        // Calculate denominators for the line intersection formula
        double inter = (currX - nextPosiX) * (Y_sw - Y_ew) - (currY - nextPosiY) * (X_sw - X_ew);
        if (inter == 0) {
            return false; // Lines are parallel, no intersection
        }

        // Calculate the intersection point (u and t are the line scalar values)
        double scalar1 = ((currX - X_sw) * (Y_sw - Y_ew) - (currY - Y_sw) * (X_sw - X_ew)) / inter;
        double scalar2 = ((nextPosiX - currX) * (currY - Y_sw) - (nextPosiY - currY) * (currX - X_sw)) / inter;

        // Check if there is an intersection within the line segments
        return scalar1 >= 0 && scalar1 <= 1 && scalar2 >= 0 && scalar2 <= 1;
    }


// check the wall bounce of the particles
    private void checkParticleBounce(Wall wall) {
        // Calculate the incident vector components
        double incidentX = Math.cos(Math.toRadians(theta));
        double incidentY = Math.sin(Math.toRadians(theta));

        double wallDx = wall.endWall.x - wall.startWall.x;
        double wallDy = wall.endWall.y - wall.startWall.y;


        double length = Math.hypot(wallDx, wallDy);
        double normalX = -wallDy / length;
        double normalY = wallDx / length;

        // Calculate the dot product between the incident vector and the wall's normal vector
        double dotProduct = incidentX * normalX + incidentY * normalY;

        double reflectX = incidentX - 2 * dotProduct * normalX;
        double reflectY = incidentY - 2 * dotProduct * normalY;

        // Convert the reflected vector back to an angle
        theta = Math.toDegrees(Math.atan2(reflectY, reflectX));

        theta = (theta + 360) % 360;
    }

}
