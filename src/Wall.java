import java.awt.*;
import java.awt.geom.*;

public class Wall{
    private int x1, y1, x2, y2;

    private double rotAngle; // Rotation Angle

    private float midX, midY; // Midpoint X and Y
    private final int HEIGHT = 720;
    private final int WIDTH = 720;

    public Wall(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.rotAngle = Math.atan2(y2 - y1, x2 - x1);
        this.midX = (float) (x1 + x2) / 2;
        this.midY = (float) (y1 + y2) / 2;
    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }

    public double getRotAngle() {
        return rotAngle;
    }

    public float getMidX() {
        return midX;
    }

    public float getMidY() {
        return midY;
    }

    public double getWidth() {
        double width = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        return width;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawLine(x1, HEIGHT - y1, x2, HEIGHT - y2);
    }

    public boolean intersects(int particleX, int particleY, int ball_radius) {
        // Calculate the distance between the ball center and the line segment.
        double distance = Line2D.ptSegDist(x1, HEIGHT - y1, x2, HEIGHT - y2, particleX, HEIGHT - particleY);

        // Check if the ball intersects with the line segment.
        return distance <= ball_radius;
    }

}