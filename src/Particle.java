import java.awt.*;
import java.util.*;

public class Particle {
    private static final int particleSize = 3, PARTFRAME_WIDTH = 1280, PARTFRAME_HEIGHT = 720;

    private int x_part, y_part;
    private double theta, velocity;
    private Color color;

    public Particle(int x, int y, double velocity, double theta){
        this.x_part = x;
        this.y_part = y;
        this.theta = Math.toRadians(theta);
        this.velocity = velocity;
        this.color = Color.white;

        System.out.printf("Particle Class: %d , %d , %f , %f ", x_part, y_part, velocity, theta);

    }

    public void draw(Graphics graphics){
        graphics.setColor(color);
        graphics.fillRect(x_part, y_part, particleSize, particleSize);
    }

    public void move(ArrayList<Wall> wallList){
        x_part = (int)(x_part + velocity * Math.cos(theta));
        y_part = (int)(y_part + velocity * Math.cos(theta));

        //wall collision
        for(Wall wall: wallList){

        }

        if (x_part <= 0 || x_part >= PARTFRAME_WIDTH) {
            theta = Math.PI - theta;
            x_part = Math.max(0, Math.min(x_part, PARTFRAME_WIDTH));
        }
        if (y_part <= 0 || y_part >= PARTFRAME_HEIGHT) {
            theta = -theta;
            y_part = Math.max(0, Math.min(y_part, PARTFRAME_HEIGHT));
        }

    }

}
