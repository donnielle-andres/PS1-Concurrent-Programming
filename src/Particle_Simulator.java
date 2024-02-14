import java.awt.*;
import javax.swing.*;

// MAIN RUN

public class Particle_Simulator {

    public static final double TIME_STEP = 1.0 / 240.0; // Target 60 updates per second
    public static final int TARGET_FPS = 60;
    public static final long OPTIMAL_TIME = 1000000000 / TARGET_FPS; // Nanoseconds per frame
    MainInterface mainInterface= new MainInterface();
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainInterface app = new MainInterface();
            app.setVisible(true);
        });
    }

    // Spec 0 - add only 1 particle
    public void addSingleParticle(Point start, double angle, double velocity) {
        mainInterface.addParticle(new Particle(start.x, start.y, angle, velocity));
    }

    // Spec 1 - add between points
    public void addParticlesBetweenPoints(int n, Point start, Point end, double theta, double velocity) {
        if (n <= 0) return; // No particles to add
        for (int i = 0; i < n; i++) {
            double ratio = (double) i / (n - 1);
            int x = start.x + (int) ((end.x - start.x) * ratio);
            int y = start.y + (int) ((end.y - start.y) * ratio);
            mainInterface.addParticle(new Particle(x, y, theta, velocity));
        }
    }

    // Spec 2 - add between theta
    public void addParticlesVaryingAngles(int n, Point start, double start_theta, double end_theta, double velocity) {
        if (n <= 0) {
            return; // No particles to add
        }
    
        double angleIncrement = (end_theta - start_theta) / (n - 1);
    
        for (int i = 0; i < n; i++) {
            double finaltheta = start_theta + (angleIncrement * i);
            mainInterface.addParticle(new Particle(start.x, start.y, finaltheta, velocity));
        }
    }

    // Spec 3 - add between velocity
    public void addParticlesVaryingVelocities(int n, Point start, double theta, double startVelocity, double endVelocity) {
        if (n <= 0) {
            return; // No particles to add
        }
    
        double velocityIncrement = (endVelocity - startVelocity) / (n - 1);
    
        for (int i = 0; i < n; i++) {
            double velocity = startVelocity + (velocityIncrement * i);
            mainInterface.addParticle(new Particle(start.x, start.y, theta, velocity));
        }
    }



}
