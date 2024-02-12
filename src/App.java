import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class App extends JFrame {

    private final Object particleListLock = new Object();

    // GUI attributes
    private JPanel particlePanel;
    private JPanel inputPanel;
    private JTextField xField, yField, thetaField, velocityField;
    private JTextField x1Field, y1Field, x2Field, y2Field;
    private JButton submitParticleButton, submitWallButton;

    private JLabel fpsLabel;

    private ArrayList<Particle> particleList = new ArrayList<Particle>();
    private ArrayList<Wall> wallList = new ArrayList<>();
    public App() {
        setTitle("Particle System App");
        setSize(1700, 760); // The window itself
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        getContentPane().setBackground(Color.GRAY); // Set the default window color

        // Particle System Panel
        particlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Draw particles
                for (Particle particle : particleList){
                    particle.draw(g);
                }

                // Draw walls
                for (Wall wall : wallList){
                    wall.draw(g);
                }

                // Update FPS label position dynamically
                fpsLabel.setBounds(getWidth() - 110, getHeight() - 30, 100, 20);
            }
        };
        particlePanel.setPreferredSize(new Dimension(1280, 720)); // Main -> width is - input panel width
        particlePanel.setBackground(Color.BLACK);
        add(particlePanel, BorderLayout.CENTER);

        // Input Panel
        inputPanel = new JPanel();
        inputPanel.setPreferredSize(new Dimension(404, 720));
        inputPanel.setLayout(new GridLayout(10, 2));

        xField = new JTextField();
        yField = new JTextField();
        thetaField = new JTextField();
        velocityField = new JTextField();
        x1Field = new JTextField();
        y1Field = new JTextField();
        x2Field = new JTextField();
        y2Field = new JTextField();
        submitParticleButton = new JButton("Add Particle");
        submitWallButton = new JButton("Add Wall");

        // Method to add a label with border
        addToInputPanel("X:", xField);
        addToInputPanel("Y:", yField);
        addToInputPanel("Theta:", thetaField);
        addToInputPanel("Velocity:", velocityField);
        addToInputPanel("Wall X1:", x1Field);
        addToInputPanel("Wall Y1:", y1Field);
        addToInputPanel("Wall X2:", x2Field);
        addToInputPanel("Wall Y2:", y2Field);
        inputPanel.add(submitParticleButton);
        inputPanel.add(submitWallButton);

        // Particle button inputs
        submitParticleButton.addActionListener(e -> {
            try {
                int x = Integer.parseInt(xField.getText());
                int y = Integer.parseInt(yField.getText());
                double theta = Double.parseDouble(thetaField.getText());
                double velocity = Double.parseDouble(velocityField.getText());

                // Validate inputs (e.g., check if coordinates are within the window bounds)
                if (x < 0 || x > particlePanel.getWidth() || y < 0 || y > particlePanel.getHeight()) {
                    JOptionPane.showMessageDialog(this, "Invalid coordinates!");
                    return;
                }
                if (theta < 0 || theta > 360) {
                    JOptionPane.showMessageDialog(this, "Theta must be between 0 and 360 degrees!");
                    return;
                }
                if (velocity < 0) {
                    JOptionPane.showMessageDialog(this, "Velocity must be non-negative!");
                    return;
                }

                // Create and add particle
                synchronized (particleListLock) {
                    
                }

                // Update particle system
                particlePanel.repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input format!");
            }
        });

        // Wall handling
        submitWallButton.addActionListener(e -> {
            try {
                int x1 = Integer.parseInt(x1Field.getText());
                int y1 = Integer.parseInt(y1Field.getText());
                int x2 = Integer.parseInt(x2Field.getText());
                int y2 = Integer.parseInt(y2Field.getText());

                // Validate inputs (e.g., check if coordinates are within the window bounds)
                if (x1 < 0 || x1 > particlePanel.getWidth() || y1 < 0 || y1 > particlePanel.getHeight() ||
                        x2 < 0 || x2 > particlePanel.getWidth() || y2 < 0 || y2 > particlePanel.getHeight()) {
                    JOptionPane.showMessageDialog(this, "Invalid wall coordinates!");
                    return;
                }

                // Create and add wall
                synchronized (particleListLock) {
                    wallList.add(new Wall(x1, y1, x2, y2));
                }

                // Update particle system
                particlePanel.repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input format!");
            }
        });

        add(inputPanel, BorderLayout.EAST);

        // FPS Label
        fpsLabel = new JLabel("FPS: 0");
        fpsLabel.setForeground(Color.WHITE); 
        fpsLabel.setHorizontalAlignment(SwingConstants.LEFT);
        particlePanel.setLayout(null); 
        particlePanel.add(fpsLabel);

        // Particle initialization

        // test particles
        


        // testwalls
        //wallList.add(new Wall(200, 300, 700, 70));
        //wallList.add(new Wall(200, 700, 700, 70));

        // Start a thread to update FPS
        new Thread(this::runFPSCounter).start();
        // Start gamelogic thread
        new Thread(this::gameLoop).start();

    }

    private void gameLoop() {
        long lastTime = System.nanoTime();
        double nsPerUpdate = 1000000000D / 60D; // 60 updates per second

        double delta = 0;

        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerUpdate;
            lastTime = now;

//            while (delta >= 1) {
//                synchronized (particleListLock) {
//                    // Update particle positions
//                    for (Particle particle : particleList) {
//                        //particle.update();
//                    }
//                }
//                delta -= 1;
//            }

            SwingUtilities.invokeLater(particlePanel::repaint);

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void runFPSCounter() {
        int frames = 0;
        long lastTimer = System.currentTimeMillis();

        while (true) {
            frames++;

            if (System.currentTimeMillis() - lastTimer >= 1000) {
                lastTimer += 1000;
                int finalFrames = frames;
                SwingUtilities.invokeLater(() -> fpsLabel.setText("FPS: " + finalFrames));
                frames = 0;
            }

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void addToInputPanel(String labelText, JTextField textField) {
        JLabel label = new JLabel(labelText);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Adding border
        inputPanel.add(label);
        inputPanel.add(textField);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            App app = new App();
            app.setVisible(true);
        });
    }
}