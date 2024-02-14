import java.awt.*;
import java.awt.geom.*;

public class Wall{
    private int X_sw, Y_sw, X_ew, Y_ew;
    public Point startWall;
    public Point endWall;

    public Wall(int X_sw, int Y_sw, int X_ew, int Y_ew) {
        this.startWall = new Point(X_sw, Y_sw);
        this.endWall = new Point(X_ew, Y_ew);
    }

    public int getX_sw() {
        return X_sw;
    }

    public int getY_sw() {
        return Y_sw;
    }

    public int getX_ew() {
        return X_ew;
    }

    public int getY_ew() {
        return Y_ew;
    }

    /* 
    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawLine(x1, HEIGHT - y1, x2, HEIGHT - y2);
    }*/

    

}