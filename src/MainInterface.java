import javax.swing.*;
import java.awt.*;
import java.nio.file.DirectoryNotEmptyException;
import java.util.*;


public class MainInterface extends JFrame {

    //WINDOW SIZE
    private int WINDOW_WIDTH = 1580;
    private int WINDOW_HEIGHT = 720;

    //PANELS
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
        private JTextField x_particle, y_particle;
        private JTextField num_particle;
        private JTextField X_sp, Y_sp;
        private JTextField X_ep, Y_ep;

        //VELOCITY
        private JTextField velocity;
        private JTextField start_velocity, end_velocity;

        //ANGLE
        private JTextField theta;
        private JTextField start_theta, end_theta;

        //WALL
        private JTextField X_startwall, Y_startwall;
        private JTextField X_endwall, Y_endwall;

        //BUTTON
        private JButton addSpec;
        private JButton addSpec1;
        private JButton addSpec2;
        private JButton addSpec3;
        private JButton addWall;

    

    public MainInterface(){
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setTitle("STDISCM PARTICLE SIMULATOR");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        PARTICLE_FRAME = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
            }
        };

        PARTICLE_FRAME.setPreferredSize(new Dimension(PARTFRAME_WIDTH, PARTFRAME_HEIGHT));
        PARTICLE_FRAME.setBackground(Color.BLACK);
        add(PARTICLE_FRAME, BorderLayout.CENTER);


        INPUT_FRAME = new JPanel(new BorderLayout());
        INPUT_FRAME.setPreferredSize(new Dimension(300, 720));
        INPUT_FRAME.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("PARTICLE SPECIFICATIONS"), 
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        

        // INPUTS FOR INITIAL SPECS
        JPanel particlePanel = new JPanel(new BorderLayout());
        particlePanel.setPreferredSize(new Dimension(300, 200));
        particlePanel.setBorder(BorderFactory.createTitledBorder("PARTICLES"));

        JPanel initialSpec = new JPanel(new GridLayout(0, 2, 2, 2));
            x_label = new JLabel("X:");
            x_particle = new JTextField(20);
            initialSpec.add(x_label);
            initialSpec.add(x_particle);

            y_label = new JLabel("Y:");
            y_particle = new JTextField(20);
            initialSpec.add(y_label);
            initialSpec.add(y_particle);

            vel_label = new JLabel("Velocity:");
            velocity = new JTextField(20);
            initialSpec.add(vel_label);
            initialSpec.add(velocity);

            theta_label = new JLabel("Theta:");
            theta = new JTextField(20);
            initialSpec.add(theta_label);
            initialSpec.add(theta);

            addSpec = new JButton("ADD PARTICLE");
            addSpec.setPreferredSize(new Dimension(100, 20));
            initialSpec.add(addSpec);

            INPUT_FRAME.add(initialSpec, BorderLayout.NORTH);

        // SPECS 1-3 TABS

            /* SPECS 1 - START AND END POINT */
            // Create the tabbed pane for the bottom section
                JTabbedPane tabbedPane = new JTabbedPane();
                tabbedPane.setSize(300, 250);

                // Create tab panels
                JPanel specsTab1 = onespecsTab();
                JPanel specsTab2 = twospecsTab();
                JPanel specsTab3 = threespecsTab();

                // Add tab panels to the tabbed pane
                tabbedPane.addTab("Specs 1", specsTab1);
                tabbedPane.addTab("Specs 2", specsTab2);
                tabbedPane.addTab("Specs 3", specsTab3);
                
                
                // Add the top panel and tabbed pane to the input frame
                INPUT_FRAME.add(tabbedPane, BorderLayout.CENTER);


        // WALL
        JPanel wallPanel = new JPanel(new BorderLayout());
        wallPanel.setBorder(BorderFactory.createTitledBorder("WALL"));

        JPanel wallSpec = new JPanel(new GridLayout(0, 2, 2, 2));
            X_startwall_label= new JLabel("X:");
            X_startwall = new JTextField(20);
            wallSpec.add(X_startwall_label);
            wallSpec.add(X_startwall);

            Y_startwall_label= new JLabel("Y:");
            Y_startwall = new JTextField(20);
            wallSpec.add(Y_startwall_label);
            wallSpec.add(Y_startwall);

            X_endwall_label= new JLabel("X:");
            X_endwall = new JTextField(20);
            wallSpec.add(X_endwall_label);
            wallSpec.add(X_endwall);

            Y_endwall_label= new JLabel("Y:");
            Y_endwall = new JTextField(20);
            wallSpec.add(Y_endwall_label);
            wallSpec.add(Y_endwall);

            addWall = new JButton("ADD WALL");
            addWall.setPreferredSize(new Dimension(100, 20));
            wallSpec.add(addWall);

            INPUT_FRAME.add(wallSpec, BorderLayout.SOUTH);

        // Add input frame to the main frame
        add(INPUT_FRAME, BorderLayout.EAST);
        setVisible(true);
    }

    /*  Method to create a tab panel */
    private JPanel onespecsTab() {
        JPanel tabPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcTab = new GridBagConstraints();
        gbcTab.gridx = 0;
        gbcTab.gridy = 0;
        gbcTab.anchor = GridBagConstraints.WEST;
        gbcTab.insets = new Insets(5, 5, 5, 5);
    
        // Number of Particles
        JLabel numpart_label = new JLabel("Number of Particles:");
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
    
        JLabel Xsp_label = new JLabel("X:");
        JTextField X_sp = new JTextField(10);
        tabPanel.add(Xsp_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(X_sp, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        JLabel Ysp_label = new JLabel("Y:");
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
    
        JLabel Xep_label = new JLabel("X:");
        JTextField X_ep = new JTextField(10);
        tabPanel.add(Xep_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(X_ep, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        JLabel Yep_label = new JLabel("Y:");
        JTextField Y_ep = new JTextField(10);
        tabPanel.add(Yep_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(Y_ep, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        // Velocity
        JLabel vel_label = new JLabel("Velocity:");
        JTextField velocity = new JTextField(10);
        tabPanel.add(vel_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(velocity, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        // Theta
        JLabel theta_label = new JLabel("Theta:");
        JTextField theta = new JTextField(10);
        tabPanel.add(theta_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(theta, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        // Add Button
        gbcTab.gridx = 0;
        gbcTab.gridwidth = 2;
        JButton addSpec1 = new JButton("Specs 1");
        tabPanel.add(addSpec1, gbcTab);
    
        // Adjust the size of the tabPanel to fit its content
        tabPanel.setPreferredSize(new Dimension(300, 350));
    
        return tabPanel;
    }

    private JPanel twospecsTab() {
        JPanel tabPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcTab = new GridBagConstraints();
        gbcTab.gridx = 0;
        gbcTab.gridy = 0;
        gbcTab.anchor = GridBagConstraints.WEST;
        gbcTab.insets = new Insets(5, 5, 5, 5);
    
        // Number of Particles
        JLabel numpart_label = new JLabel("Number of Particles:");
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
    
        JLabel x_label = new JLabel("X:");
        JTextField x_particle= new JTextField(10);
        tabPanel.add(x_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(x_particle, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        JLabel y_label = new JLabel("Y:");
        JTextField y_particle = new JTextField(10);
        tabPanel.add(y_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(y_particle, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;

        // Theta
        JLabel st_label = new JLabel("Starting Theta:");
        JTextField start_theta = new JTextField(10);
        tabPanel.add(st_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(start_theta, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;

        JLabel et_label = new JLabel("Ending Theta:");
        JTextField end_theta = new JTextField(10);
        tabPanel.add(et_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(end_theta, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        // Velocity
        JLabel vel_label = new JLabel("Velocity:");
        JTextField velocity = new JTextField(10);
        tabPanel.add(vel_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(velocity, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        
        // Add Button
        gbcTab.gridx = 0;
        gbcTab.gridwidth = 2;
        JButton addSpec2 = new JButton("Specs 2");
        tabPanel.add(addSpec2, gbcTab);
    
        // Adjust the size of the tabPanel to fit its content
        tabPanel.setPreferredSize(new Dimension(300, 350));
    
        return tabPanel;
    }
    
    private JPanel threespecsTab() {
        JPanel tabPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcTab = new GridBagConstraints();
        gbcTab.gridx = 0;
        gbcTab.gridy = 0;
        gbcTab.anchor = GridBagConstraints.WEST;
        gbcTab.insets = new Insets(5, 5, 5, 5);
    
        // Number of Particles
        JLabel numpart_label = new JLabel("Number of Particles:");
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
    
        JLabel x_label = new JLabel("X:");
        JTextField x_particle= new JTextField(10);
        tabPanel.add(x_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(x_particle, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        JLabel y_label = new JLabel("Y:");
        JTextField y_particle = new JTextField(10);
        tabPanel.add(y_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(y_particle, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;

        // Velocity
        JLabel sv_label = new JLabel("Starting Velocity:");
        JTextField start_velocity = new JTextField(10);
        tabPanel.add(sv_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(start_velocity, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;

        JLabel ev_label = new JLabel("Ending Velocity:");
        JTextField end_velocity = new JTextField(10);
        tabPanel.add(ev_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(end_velocity, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;

        // Theta
        JLabel theta_label = new JLabel("Theta:");
        JTextField theta = new JTextField(10);
        tabPanel.add(theta_label, gbcTab);
        gbcTab.gridx = 1;
        tabPanel.add(theta, gbcTab);
        gbcTab.gridx = 0;
        gbcTab.gridy++;
    
        
        // Add Button
        gbcTab.gridx = 0;
        gbcTab.gridwidth = 2;
        JButton addSpec3 = new JButton("Specs 3");
        tabPanel.add(addSpec3, gbcTab);
    
        // Adjust the size of the tabPanel to fit its content
        tabPanel.setPreferredSize(new Dimension(300, 350));
    
        return tabPanel;
    }
    


    //RUN
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainInterface app = new MainInterface();
            app.setVisible(true);
        });
    }

}
