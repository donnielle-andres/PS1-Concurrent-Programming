import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
public class Particle {
    private static final int particleSize = 3, SCREEN_HEIGHT = 1280, SCREEN_WIDTH = 720;

    private static final Random random = new Random();
    private int x, y;
    private double theta, velocity;
    private Color color;

    public Particle(int x, int y, double theta, double velocity){
        this.x = x;
        this.y = y;
        this.theta = Math.toRadians(theta);
        this.velocity = velocity;
        this.color = randomColor();
    }

    public void draw(Graphics graphics){
        graphics.setColor(color);
        graphics.fillRect(x, y, particleSize, particleSize);
    }

    public void move(ArrayList<Wall> wallList){
        x = (int)(x + velocity * Math.cos(theta));
        y = (int)(y + velocity * Math.cos(theta));

        //wall collision
        for(Wall wall: wallList){

        }

        if (x <= 0 || x >= SCREEN_WIDTH) {
            theta = Math.PI - theta;
            x = Math.max(0, Math.min(x, SCREEN_WIDTH));
        }
        if (y <= 0 || y >= SCREEN_HEIGHT) {
            theta = -theta;
            y = Math.max(0, Math.min(y, SCREEN_HEIGHT));
        }

    }

    public Color randomColor(){
        int r  = 128 + random.nextInt(128);
        int g = 128 + random.nextInt(128);
        int b = 128 + random.nextInt(128);

        return new Color(r, g, b);
    }
}
