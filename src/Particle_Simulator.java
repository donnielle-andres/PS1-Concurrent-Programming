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
            app.runSimulation();
        });
    }
}
