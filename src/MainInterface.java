import javax.swing.*;
import java.awt.*;
import java.nio.file.DirectoryNotEmptyException;
import java.util.*;
import java.util.concurrent.*;


public class MainInterface extends JFrame {

    private ArrayList<Particle> particleList = new ArrayList<Particle>();
    private ArrayList<Wall> wallList = new ArrayList<Wall>();
    private final ForkJoinPool taskThread= new ForkJoinPool(); // physicsThreadPool
    private final ForkJoinPool renderThread = new ForkJoinPool();

    //WINDOW SIZE
    private int WINDOW_WIDTH = 1580;
    private int WINDOW_HEIGHT = 720;

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
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setTitle("STDISCM PARTICLE SIMULATOR");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(true);

        // PARTICLE FRAME
        PARTICLE_FRAME = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Draw particles
                for (Particle particle : particleList){
                    particle.draw(g);
                }

                // Draw wall
                for (Wall wall : wallList) {
                    wall.draw(g);
                }

                fps_label.setBounds(1180, 0, 100, 20);
                
            }
        };

        PARTICLE_FRAME.setPreferredSize(new Dimension(PARTFRAME_WIDTH, PARTFRAME_HEIGHT));
        PARTICLE_FRAME.setBackground(Color.BLACK);
        add(PARTICLE_FRAME, BorderLayout.CENTER);

        // FPS LABEL
        fps_label = new JLabel("FPS: 0");
        fps_label.setForeground(Color.WHITE); 
        fps_label.setHorizontalAlignment(SwingConstants.LEFT);
        PARTICLE_FRAME.setLayout(null); 
        PARTICLE_FRAME.add(fps_label);
        new Thread(this::fpsCounter).start();

        // INPUT FRAME
        INPUT_FRAME = new JPanel(new BorderLayout());
        INPUT_FRAME.setPreferredSize(new Dimension(280, 720));
        INPUT_FRAME.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("PARTICLE SPECIFICATIONS"), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        

        // INPUTS FOR INITIAL SPECS
        JPanel particlePanel = new JPanel(new BorderLayout());
        particlePanel.setPreferredSize(new Dimension(280, 200));
        particlePanel.setBorder(BorderFactory.createTitledBorder("PARTICLES"));

        JPanel initialSpec = new JPanel(new GridLayout(0, 2, 2, 2));
        JLabel x_label = new JLabel("X:");
        JTextField x_particle = new JTextField(20);
            initialSpec.add(x_label);
            initialSpec.add(x_particle);

            y_label = new JLabel("Y:");
            JTextField y_particle = new JTextField(20);
            initialSpec.add(y_label);
            initialSpec.add(y_particle);

            vel_label = new JLabel("Velocity:");
            JTextField velocity = new JTextField(20);
            initialSpec.add(vel_label);
            initialSpec.add(velocity);

            theta_label = new JLabel("Theta:");
            JTextField theta = new JTextField(20);
            initialSpec.add(theta_label);
            initialSpec.add(theta);

            addSpec = new JButton("ADD PARTICLE");
            addSpec.setPreferredSize(new Dimension(100, 20));
            initialSpec.add(addSpec);

            INPUT_FRAME.add(initialSpec, BorderLayout.NORTH);

        // SPECS 1-3 TABS
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.setSize(300, 250);

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
        JPanel wallPanel = new JPanel(new BorderLayout());
        wallPanel.setBorder(BorderFactory.createTitledBorder("WALL"));

        JPanel wallSpec = new JPanel(new GridLayout(0, 2, 2, 2));
            X_startwall_label= new JLabel("Wall Start Point X:");
            X_startwall = new JTextField(20);
            wallSpec.add(X_startwall_label);
            wallSpec.add(X_startwall);

            Y_startwall_label= new JLabel("Wall Start Point Y:");
            Y_startwall = new JTextField(20);
            wallSpec.add(Y_startwall_label);
            wallSpec.add(Y_startwall);

            X_endwall_label= new JLabel("Wall End Point X:");
            X_endwall = new JTextField(20);
            wallSpec.add(X_endwall_label);
            wallSpec.add(X_endwall);

            Y_endwall_label= new JLabel("Wall End Point Y:");
            Y_endwall = new JTextField(20);
            wallSpec.add(Y_endwall_label);
            wallSpec.add(Y_endwall);

            addWall = new JButton("ADD WALL");
            addWall.setPreferredSize(new Dimension(100, 20));
            wallSpec.add(addWall);

            INPUT_FRAME.add(wallSpec, BorderLayout.SOUTH);

        add(INPUT_FRAME, BorderLayout.EAST);
        setVisible(true);


        // ACTION LISTENER - INITIAL SPEC
            addSpec.addActionListener(e -> {
                try {
                    int x_part = Integer.parseInt(x_particle.getText());
                    int y_part = Integer.parseInt(y_particle.getText());
                    double velo_val = Double.parseDouble(velocity.getText());
                    double theta_val = Double.parseDouble(theta.getText());

                    System.out.printf("InitSpec %d , %d , %f , %f ", x_part, y_part, velo_val, theta_val);
                    
                    // Create a new Particle object with the retrieved values
                    Particle newParticle = new Particle(x_part, y_part, velo_val, theta_val);
                    particleList.add(newParticle);
                    PARTICLE_FRAME.repaint();

                } catch (NumberFormatException num) {
                    JOptionPane.showMessageDialog(this, "Invalid input format!");

                }
                            
            });

            // WALL
            addWall.addActionListener(e -> {
                try {
                    int sw_X = Integer.parseInt(X_startwall.getText());
                    int sw_Y = Integer.parseInt(Y_startwall.getText());
                    int ew_X = Integer.parseInt(X_endwall.getText());
                    int ew_Y = Integer.parseInt(Y_endwall.getText());

                    System.out.printf("\n" + "Wall %d , %d , %d , %d", sw_X, sw_Y, ew_X, ew_Y);
                    
                    // Create a new Wall object with the retrieved values
                    Wall newWall = new Wall(sw_X, sw_Y, ew_X, ew_Y);
                    wallList.add(newWall);
                    PARTICLE_FRAME.repaint();

                } catch (NumberFormatException num) {
                    JOptionPane.showMessageDialog(this, "Invalid input format!");

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
    
        Xsp_label = new JLabel("X:");
        JTextField X_sp = new JTextField(10);
        tabPanel.add(Xsp_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(X_sp, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        Ysp_label = new JLabel("Y:");
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
    
        Xep_label = new JLabel("X:");
        JTextField X_ep = new JTextField(10);
        tabPanel.add(Xep_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(X_ep, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        Yep_label = new JLabel("Y:");
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
        addSpec1 = new JButton("Specs 1");
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

                System.out.printf( "\n" + "Spec 1 Check: %d , %d , %d , %d , %d , %f , %f ", num_part, sp_X, sp_Y, ep_X, ep_Y, velo_val, theta_val);

                if (num_part > 0) {
                    for (int i = 0; i < num_part; i++) {
                        double ratio = (double) i / (num_part - 1);
                        int x = sp_X + (int) ((ep_X - sp_X) * ratio);
                        int y = sp_Y + (int) ((ep_Y - sp_Y) * ratio);
                        Particle newParticle = (new Particle(x, y, theta_val, velo_val));
                        particleList.add(newParticle);
                        PARTICLE_FRAME.repaint();
                    }
                }


            } catch (NumberFormatException num) {
                JOptionPane.showMessageDialog(this, "Invalid input format!");

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
    
        x_label = new JLabel("X:");
        JTextField x_particle= new JTextField(10);
        tabPanel.add(x_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(x_particle, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        y_label = new JLabel("Y:");
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
        addSpec2 = new JButton("Specs 2");
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

                System.out.printf("\n" + "Spec 2 Check: %d , %d , %d , %f , %f , %f", num_part, x_part, y_part, theta_start, theta_end, velo_val);

                double angleIncrement = (theta_end - theta_start) / (num_part - 1);

                for (int i = 0; i < num_part; i++) {
                    double theta_final = theta_start + (angleIncrement * i);
                    Particle newParticle = new Particle(x_part, y_part, theta_final, velo_val);
                    particleList.add(newParticle);
                    PARTICLE_FRAME.repaint();
                }

            } catch (NumberFormatException num) {
                JOptionPane.showMessageDialog(this, "Invalid input format!");

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
    
        x_label = new JLabel("X:");
        JTextField x_particle= new JTextField(10);
        tabPanel.add(x_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(x_particle, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        y_label = new JLabel("Y:");
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
        addSpec3 = new JButton("Specs 3");
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

                System.out.printf("\n" + "Spec 3 Check: %d , %d , %d , %f , %f , %f", num_part, x_part, y_part, velo_start, velo_end, theta_val);
                
                double veloIncrement = (velo_end - velo_start) / (num_part - 1);

                for (int i = 0; i < num_part; i++) {
                    double velo_final = velo_start + (veloIncrement * i);
                    Particle newParticle = new Particle(x_part, y_part, theta_val, velo_final);
                    particleList.add(newParticle);
                    PARTICLE_FRAME.repaint();
                }

            } catch (NumberFormatException num) {
                JOptionPane.showMessageDialog(this, "Invalid input format!");

            }
                        
        });
    
        return tabPanel;
    }
    

// MAS BETTER ATA IF NASA PARTICLE_SIMULATOR TOH 
    private void fpsCounter() {
        int frames = 0;
        long last_time = System.currentTimeMillis();

        while (true) {
            frames++;

            if (System.currentTimeMillis() - last_time >= 1000) {
                last_time += 1000;
                int finalFrames = frames;
                SwingUtilities.invokeLater(() -> fps_label.setText("FPS: " + finalFrames));
                frames = 0;
            }

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //
    public void addParticle(Particle particle) {
        particleList.add(particle);
    }

    public void addWall(Wall wall) {
        wallList.add(wall);
    }

    public void runSimulation() {
        new Thread(() -> {
            while (true) {
                long startTime = System.nanoTime();
                processParticles(Particle_Simulator.TIME_STEP); // Update particle physics
                SwingUtilities.invokeLater(this::repaint); // Repaint the canvas

                try {
                    long sleepTime = (Particle_Simulator.OPTIMAL_TIME - (System.nanoTime() - startTime)) / 1000000;
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
    

    private void processParticles(double deltaTime) {
        taskThread.submit(() -> particleList.parallelStream().forEach(particle -> {
            particle.updatePosition(deltaTime);
            particle.partCollision(wallList);
        })).join();
    }

}
