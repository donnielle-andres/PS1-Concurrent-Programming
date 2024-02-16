import javax.swing.*;

// MAIN RUN

public class Particle_Simulator {


    MainInterface mainInterface= new MainInterface();
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainInterface app = new MainInterface();
            app.setVisible(true);
            app.runSimulation();
        });
    }
}
