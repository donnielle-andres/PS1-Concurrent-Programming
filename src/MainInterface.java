import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.*;


public class MainInterface extends JFrame {

    private ArrayList<Particle> particleList = new ArrayList<Particle>();
    private ArrayList<Wall> wallList = new ArrayList<Wall>();
    private final ForkJoinPool taskThread= new ForkJoinPool();

    //FPS VARIABLES
    private int frameCtr = 0;
    private long lastFpsUpdateTime = System.nanoTime();

    //FRAMES
    private JPanel PARTICLE_FRAME;
    private JPanel INPUT_FRAME;
    private int PARTFRAME_WIDTH = 1280;
    private int PARTFRAME_HEIGHT = 720;

    //LABELS
    private JLabel x_label, y_label;
    private JLabel numpart_label;
    private JLabel Xsp_label, Ysp_label;
    private JLabel Xep_label, Yep_label;
    private JLabel vel_label;
    private JLabel sv_label, ev_label;
    private JLabel theta_label;
    private JLabel st_label, et_label;
    private JLabel X_startwall_label, Y_startwall_label;
    private JLabel X_endwall_label, Y_endwall_label;
    private JLabel fps_label;

    /* INPUTS */
        //PARTICLES
        public JTextField x_particle, y_particle;
        public JTextField num_particle;
        public JTextField X_sp, Y_sp;
        public JTextField X_ep, Y_ep;

        //VELOCITY
        public JTextField velocity;
        public JTextField start_velocity, end_velocity;

        //ANGLE
        public JTextField theta;
        public JTextField start_theta, end_theta;

        //WALL
        public JTextField X_startwall, Y_startwall;
        public JTextField X_endwall, Y_endwall;

        //BUTTON
        public JButton addSpec;
        public JButton addSpec1;
        public JButton addSpec2;
        public JButton addSpec3;
        public JButton addWall;

    

    public MainInterface(){
        setTitle("STDISCM PARTICLE SIMULATOR");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        // PARTICLE FRAME
        PARTICLE_FRAME = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                int numThreads = Runtime.getRuntime().availableProcessors(); // Get number of available processors
                ConcurrentLinkedQueue<Particle> particleQueue = new ConcurrentLinkedQueue<>(particleList); // Create a concurrent queue

                // Create and start rendering threads
                Thread[] renderingThreads = new Thread[numThreads];
                for (int i = 0; i < numThreads; i++) {
                    renderingThreads[i] = new Thread(() -> renderParticles(particleQueue, g));
                    renderingThreads[i].start();
                }

                // Wait for all rendering threads to finish
                try {
                    for (Thread thread : renderingThreads) {
                        thread.join();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Draw walls
                for (Wall wall : wallList) {
                    wall.draw(g);
                }

                fps_label.setBounds(1180, 0, 100, 20);
                updateFPS();
                
            }
        };

        PARTICLE_FRAME.setPreferredSize(new Dimension(PARTFRAME_WIDTH, PARTFRAME_HEIGHT));
        PARTICLE_FRAME.setBackground(Color.BLACK);
        PARTICLE_FRAME.setBorder(BorderFactory.createLineBorder(Color.BLUE));

        // FPS LABEL
        fps_label = new JLabel("FPS: 0");
        fps_label.setForeground(Color.WHITE);
        fps_label.setHorizontalAlignment(SwingConstants.LEFT);
        PARTICLE_FRAME.add(fps_label); // Add the label to the particle frame

        // INPUT FRAME
        INPUT_FRAME = new JPanel(new BorderLayout());
        INPUT_FRAME.setPreferredSize(new Dimension(280, PARTFRAME_HEIGHT)); // Set height to match PARTICLE_FRAME
        INPUT_FRAME.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("PARTICLE SPECIFICATIONS"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        

        // INPUTS FOR INITIAL SPECS
        JPanel initialSpec = new JPanel(new GridLayout(0, 2, 2, 2));
        JLabel x_label = new JLabel("X (Max: 1280):");
        JTextField x_particle = new JTextField(20);
            initialSpec.add(x_label);
            initialSpec.add(x_particle);

            y_label = new JLabel("Y (Max: 720):");
            JTextField y_particle = new JTextField(20);
            initialSpec.add(y_label);
            initialSpec.add(y_particle);

            vel_label = new JLabel("Velocity:");
            JTextField velocity = new JTextField(20);
            initialSpec.add(vel_label);
            initialSpec.add(velocity);

            theta_label = new JLabel("Theta (Max: 360):");
            JTextField theta = new JTextField(20);
            initialSpec.add(theta_label);
            initialSpec.add(theta);

            addSpec = new JButton("ADD PARTICLE");
            addSpec.setPreferredSize(new Dimension(100, 20));
            initialSpec.add(addSpec);

            INPUT_FRAME.add(initialSpec, BorderLayout.NORTH);

        // SPECS 1-3 TABS
            JTabbedPane tabbedPane = new JTabbedPane();
            //tabbedPane.setSize(280, 250);

            // Create tab panels
            JPanel specsTab1 = onespecsTab();
            JPanel specsTab2 = twospecsTab();
            JPanel specsTab3 = threespecsTab();

            // Add tab panels to the tabbed pane
            tabbedPane.addTab("Point", specsTab1);
            tabbedPane.addTab("Theta", specsTab2);
            tabbedPane.addTab("Velocity", specsTab3);
            
            
            // Add the top panel and tabbed pane to the input frame
            INPUT_FRAME.add(tabbedPane, BorderLayout.CENTER);


        // WALL
        JPanel wallSpec = new JPanel(new GridLayout(0, 2, 2, 2));
            X_startwall_label= new JLabel("Wall Start X (Max: 1280):");
            X_startwall = new JTextField(20);
            wallSpec.add(X_startwall_label);
            wallSpec.add(X_startwall);

            Y_startwall_label= new JLabel("Wall Start Y (Max: 720):");
            Y_startwall = new JTextField(20);
            wallSpec.add(Y_startwall_label);
            wallSpec.add(Y_startwall);

            X_endwall_label= new JLabel("Wall End X (Max: 1280):");
            X_endwall = new JTextField(20);
            wallSpec.add(X_endwall_label);
            wallSpec.add(X_endwall);

            Y_endwall_label= new JLabel("Wall End Y (Max: 720):");
            Y_endwall = new JTextField(20);
            wallSpec.add(Y_endwall_label);
            wallSpec.add(Y_endwall);

            addWall = new JButton("ADD WALL");
            addWall.setPreferredSize(new Dimension(100, 20));
            wallSpec.add(addWall);

            INPUT_FRAME.add(wallSpec, BorderLayout.SOUTH);

        // Add components to JFrame
        add(PARTICLE_FRAME, BorderLayout.CENTER);
        add(INPUT_FRAME, BorderLayout.EAST);

        pack(); // Resize frame to fit components
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true);


        // ACTION LISTENER - INITIAL SPEC
            addSpec.addActionListener(e -> {
                try {
                    int x_part = Integer.parseInt(x_particle.getText());
                    int y_part = Integer.parseInt(y_particle.getText());
                    double velo_val = Double.parseDouble(velocity.getText());
                    double theta_val = Double.parseDouble(theta.getText());
            
                    // Check canvas boundaries
                    if (x_part < 0 || x_part > PARTFRAME_WIDTH || y_part < 0 || y_part > PARTFRAME_HEIGHT) {
                        throw new IllegalArgumentException("Particle position must be within canvas boundaries (1280x720)!");
                    }
            
                    // Check theta range
                    if (theta_val < 0 || theta_val > 360) {
                        throw new IllegalArgumentException("Theta angle must be between 1 and 360 degrees!");
                    }
            
                    // Create a new Particle object with the retrieved values
                    Particle newParticle = new Particle(x_part, y_part, velo_val, theta_val);
                    particleList.add(newParticle);
                    PARTICLE_FRAME.repaint();
            
                } catch (NumberFormatException num) {
                    JOptionPane.showMessageDialog(this, "Invalid input format!");
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
            });
            
        // WALL
            addWall.addActionListener(e -> {
                try {
                    int sw_X = Integer.parseInt(X_startwall.getText());
                    int sw_Y = Integer.parseInt(Y_startwall.getText());
                    int ew_X = Integer.parseInt(X_endwall.getText());
                    int ew_Y = Integer.parseInt(Y_endwall.getText());
            
                    // Check canvas boundaries
                    if ((sw_X < 0 || sw_X > PARTFRAME_WIDTH || sw_Y < 0 || sw_Y > PARTFRAME_HEIGHT) ||
                        (ew_X < 0 || ew_X > PARTFRAME_WIDTH || ew_Y < 0 || ew_Y > PARTFRAME_HEIGHT)) {
                        throw new IllegalArgumentException("Wall coordinates must be within canvas boundaries (1280x720)!");
                    }
            
                    // Create a new Wall object with the retrieved values
                    Wall newWall = new Wall(sw_X, sw_Y, ew_X, ew_Y);
                    wallList.add(newWall);
                    PARTICLE_FRAME.repaint();
            
                } catch (NumberFormatException num) {
                    JOptionPane.showMessageDialog(this, "Invalid input format!");
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
            });
        

    }

    /*  Method to create a tab panel */
    private JPanel onespecsTab() {
        JPanel tabPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcTab = new GridBagConstraints();
        gbcTab.gridx = 0;
        gbcTab.gridy = 0;
        gbcTab.anchor = GridBagConstraints.WEST;
        
    
        // Number of Particles
        numpart_label = new JLabel("Number of Particles:");
        JTextField num_particle = new JTextField(10);
        tabPanel.add(numpart_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(num_particle, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        // Start Point
        JLabel startPoint_label = new JLabel("Start Point");
        tabPanel.add(startPoint_label, gbcTab);
        gbcTab.gridy++;
    
        Xsp_label = new JLabel("X (Max: 1280):");
        JTextField X_sp = new JTextField(10);
        tabPanel.add(Xsp_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(X_sp, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        Ysp_label = new JLabel("Y (Max: 720):");
        JTextField Y_sp = new JTextField(10);
        tabPanel.add(Ysp_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(Y_sp, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        // End Point
        JLabel endPoint_label = new JLabel("End Point");
        tabPanel.add(endPoint_label, gbcTab);
        gbcTab.gridy++;
    
        Xep_label = new JLabel("X (Max: 1280):");
        JTextField X_ep = new JTextField(10);
        tabPanel.add(Xep_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(X_ep, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        Yep_label = new JLabel("Y (Max: 720):");
        JTextField Y_ep = new JTextField(10);
        tabPanel.add(Yep_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(Y_ep, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        // Velocity
        vel_label = new JLabel("Velocity:");
        JTextField velocity = new JTextField(10);
        tabPanel.add(vel_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(velocity, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        // Theta
        theta_label = new JLabel("Theta:");
        JTextField theta = new JTextField(10);
        tabPanel.add(theta_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(theta, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        // Add Button
        gbcTab.gridx = 0;
        gbcTab.gridwidth = 2;
        addSpec1 = new JButton("Add Points");
        tabPanel.add(addSpec1, gbcTab);
    
        // Adjust the size of the tabPanel to fit its content
        tabPanel.setPreferredSize(new Dimension(280, 300));

        // ACTION LISTENER - SPEC 1
        addSpec1.addActionListener(e -> {
            try {
                int num_part = Integer.parseInt(num_particle.getText());
                int sp_X = Integer.parseInt(X_sp.getText());
                int sp_Y = Integer.parseInt(Y_sp.getText());
                int ep_X = Integer.parseInt(X_ep.getText());
                int ep_Y = Integer.parseInt(Y_ep.getText());
                double velo_val = Double.parseDouble(velocity.getText());
                double theta_val = Double.parseDouble(theta.getText());
        
                // Check canvas boundaries for start and end points
                if ((sp_X < 0 || sp_X > PARTFRAME_WIDTH || sp_Y < 0 || sp_Y > PARTFRAME_HEIGHT) ||
                    (ep_X < 0 || ep_X > PARTFRAME_WIDTH || ep_Y < 0 || ep_Y > PARTFRAME_HEIGHT)) {
                    throw new IllegalArgumentException("Start and end points must be within canvas boundaries (1280x720)!");
                }
        
                // Check theta range
                if (theta_val < 0 || theta_val > 360) {
                    throw new IllegalArgumentException("Theta angle must be between 1 and 360 degrees!");
                }
        
                // Check if number of particles is positive
                if (num_part <= 0) {
                    throw new IllegalArgumentException("Number of particles must be positive!");
                }
        
                for (int i = 0; i < num_part; i++) {
                    double ratio = (double) i / (num_part - 1);
                    int x = sp_X + (int) ((ep_X - sp_X) * ratio);
                    int y = sp_Y + (int) ((ep_Y - sp_Y) * ratio);
                    Particle newParticle = (new Particle(x, y, velo_val, theta_val));
                    particleList.add(newParticle);
                    PARTICLE_FRAME.repaint();
                }
        
            } catch (NumberFormatException num) {
                JOptionPane.showMessageDialog(this, "Invalid input format!");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
             
    
        return tabPanel;
    }

    private JPanel twospecsTab() {
        JPanel tabPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcTab = new GridBagConstraints();
        gbcTab.gridx = 0;
        gbcTab.gridy = 0;
        gbcTab.anchor = GridBagConstraints.WEST;
    
        // Number of Particles
        numpart_label = new JLabel("Number of Particles:");
        JTextField num_particle = new JTextField(10);
        tabPanel.add(numpart_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(num_particle, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        // Start Point
        JLabel particle_label = new JLabel("Particle Location");
        tabPanel.add(particle_label, gbcTab);
        gbcTab.gridy++;
    
        x_label = new JLabel("X (Max: 1280):");
        JTextField x_particle= new JTextField(10);
        tabPanel.add(x_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(x_particle, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        y_label = new JLabel("Y (Max: 720):");
        JTextField y_particle = new JTextField(10);
        tabPanel.add(y_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(y_particle, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;

        // Theta
        st_label = new JLabel("Starting Theta:");
        JTextField start_theta = new JTextField(10);
        tabPanel.add(st_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(start_theta, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;

        et_label = new JLabel("Ending Theta:");
        JTextField end_theta = new JTextField(10);
        tabPanel.add(et_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(end_theta, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        // Velocity
        vel_label = new JLabel("Velocity:");
        JTextField velocity = new JTextField(10);
        tabPanel.add(vel_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(velocity, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        
        // Add Button
        gbcTab.gridx = 0;
        gbcTab.gridwidth = 2;
        addSpec2 = new JButton("Add Angles");
        tabPanel.add(addSpec2, gbcTab);
    
        // Adjust the size of the tabPanel to fit its content
        tabPanel.setPreferredSize(new Dimension(280, 300));

        // ACTION LISTENER - SPEC 2
        addSpec2.addActionListener(e -> {
            try {
                int num_part = Integer.parseInt(num_particle.getText());
                int x_part = Integer.parseInt(x_particle.getText());
                int y_part = Integer.parseInt(y_particle.getText());
                double theta_start = Double.parseDouble(start_theta.getText());
                double theta_end = Double.parseDouble(end_theta.getText());
                double velo_val = Double.parseDouble(velocity.getText());
        
                // Check if x and y coordinates are within canvas width and height
                if (x_part < 0 || x_part > PARTFRAME_WIDTH || y_part < 0 || y_part > PARTFRAME_HEIGHT) {
                    throw new IllegalArgumentException("Coordinates (x, y) must be within the canvas bounds (1280x720)!");
                }
        
                // Check theta range
                if (theta_start < 0 || theta_start > 360 || theta_end < 0 || theta_end > 360) {
                    throw new IllegalArgumentException("Theta angles must be between 1 and 360 degrees!");
                }
        
                // Check if number of particles is positive
                if (num_part <= 0) {
                    throw new IllegalArgumentException("Number of particles must be positive!");
                }
        
                double angleIncrement = (theta_end - theta_start) / (num_part-1);
        
                for (int i = 0; i < num_part; i++) {
                    double theta_final = theta_start + (angleIncrement * i);
                    Particle newParticle = new Particle(x_part, y_part, velo_val, theta_final);
                    particleList.add(newParticle);
                    PARTICLE_FRAME.repaint();
                }
        
            } catch (NumberFormatException num) {
                JOptionPane.showMessageDialog(this, "Invalid input format!");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
        
    
        return tabPanel;
    }
    
    private JPanel threespecsTab() {
        JPanel tabPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcTab = new GridBagConstraints();
        gbcTab.gridx = 0;
        gbcTab.gridy = 0;
        gbcTab.anchor = GridBagConstraints.WEST;

    
        // Number of Particles
        numpart_label = new JLabel("Number of Particles:");
        JTextField num_particle = new JTextField(10);
        tabPanel.add(numpart_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(num_particle, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        // Start Point
        JLabel particle_label = new JLabel("Particle Location");
        tabPanel.add(particle_label, gbcTab);
        gbcTab.gridy++;
    
        x_label = new JLabel(" (Max: 1280):");
        JTextField x_particle= new JTextField(10);
        tabPanel.add(x_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(x_particle, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        y_label = new JLabel("Y (Max: 720):");
        JTextField y_particle = new JTextField(10);
        tabPanel.add(y_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(y_particle, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;

        // Velocity
        sv_label = new JLabel("Starting Velocity:");
        JTextField start_velocity = new JTextField(10);
        tabPanel.add(sv_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(start_velocity, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;

        ev_label = new JLabel("Ending Velocity:");
        JTextField end_velocity = new JTextField(10);
        tabPanel.add(ev_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(end_velocity, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;

        // Theta
        theta_label = new JLabel("Theta:");
        JTextField theta = new JTextField(10);
        tabPanel.add(theta_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(theta, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        
        // Add Button
        gbcTab.gridx = 0;
        gbcTab.gridwidth = 2;
        addSpec3 = new JButton("Add Velocities");
        tabPanel.add(addSpec3, gbcTab);
    
        // Adjust the size of the tabPanel to fit its content
        tabPanel.setPreferredSize(new Dimension(280, 300));

        // ACTION LISTENER - SPEC 3
        addSpec3.addActionListener(e -> {
            try {
                int num_part = Integer.parseInt(num_particle.getText());
                int x_part = Integer.parseInt(x_particle.getText());
                int y_part = Integer.parseInt(y_particle.getText());
                double velo_start = Double.parseDouble(start_velocity.getText());
                double velo_end = Double.parseDouble(end_velocity.getText());
                double theta_val = Double.parseDouble(theta.getText());
        
                // Check if x and y coordinates are within canvas width and height
                if (x_part < 0 || x_part > PARTFRAME_WIDTH || y_part < 0 || y_part > PARTFRAME_HEIGHT) {
                    throw new IllegalArgumentException("Coordinates (x, y) must be within the canvas bounds (1280x720)!");
                }
        
                // Check velocity range
                if (velo_start < 0 || velo_end < 0 || velo_start > velo_end) {
                    throw new IllegalArgumentException("Velocity values must be positive and Start Velocity must be less than End Velocity!");
                }
        
                // Check theta range
                if (theta_val < 0 || theta_val > 360) {
                    throw new IllegalArgumentException("Theta angle must be between 1 and 360 degrees!");
                }
        
                // Check if number of particles is positive
                if (num_part <= 0) {
                    throw new IllegalArgumentException("Number of particles must be positive!");
                }
        
                double veloIncrement = (velo_end - velo_start) / (num_part - 1);
        
                for (int i = 0; i < num_part; i++) {
                    double velo_final = velo_start + (veloIncrement * i);
                    Particle newParticle = new Particle(x_part, y_part, velo_final, theta_val);
                    particleList.add(newParticle);
                    PARTICLE_FRAME.repaint();
                }
        
            } catch (NumberFormatException num) {
                JOptionPane.showMessageDialog(this, "Invalid input format!");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
        
    
        return tabPanel;
    }

    public void runSimulation() {
        ExecutorService executorCalc = Executors.newFixedThreadPool(8);
        ExecutorService executorPaint = Executors.newFixedThreadPool(8);

        executorCalc.execute(() -> {
            while (true) {

                long startTime = System.nanoTime();
                processParticles(Particle_Simulator.TIME_STEP); // Update particle physics

                // Submit repaint task to the executor
                executorPaint.execute(this::repaint); // Repaint the canvas

                try {
                    long sleepTime = (Particle_Simulator.OPTIMAL_TIME - (System.nanoTime() - startTime)) / 1000000;
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }


    private void processParticles(double moveTime) {
        // Make a defensive copy of particleList
        ArrayList<Particle> particlesCopy;
        synchronized (particleList) {
            particlesCopy = new ArrayList<>(particleList);
        }

        // Process the particles in parallel
        taskThread.submit(() -> particlesCopy.parallelStream().forEach(particle -> {
            particle.updatePosition(moveTime);
            particle.partCollision(wallList);
        })).join();
    }

    private void renderParticles(ConcurrentLinkedQueue<Particle> particleQueue, Graphics g) {
        Particle particle;
        while ((particle = particleQueue.poll()) != null) { // Poll particles from the queue until empty
            particle.draw(g);
        }
    }

    private void updateFPS() {
        long currentTime = System.nanoTime();
        frameCtr++;
        
        // Calculate elapsed time since last FPS update
        long elapsedTime = currentTime - lastFpsUpdateTime;
        
        // Update FPS if elapsed time is greater than or equal to 500 milliseconds
        if (elapsedTime >= 500_000_000L) {
            double elapsedTimeInSeconds = elapsedTime / 1_000_000_000.0;
            double fps = frameCtr / elapsedTimeInSeconds;
            fps_label.setText(String.format("FPS: %.0f", fps));
            frameCtr = 0;
            lastFpsUpdateTime = currentTime;
        }
    }
    

}
