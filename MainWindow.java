import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;

/**
 * This is the main window of the GUI
 * 
 * @author (Omar Farooq) 
 * @version (6/2/14)
 */
public class MainWindow extends JFrame implements ActionListener
{
    //CLASS CONSTANTS
    final String NN_SOURCE = ""; //this is the name of the R script with code for NN 
    final Dimension WINDOW_SIZE = new Dimension(800,700); //the size of the main window
    final Dimension BUTTON_SIZE = new Dimension(120,30);

    //ALL THE PANELS 
    private JPanel mainPanel; //the main panel that holds everything together
    private JPanel guiPanel;  //the panel with the GUI
    private JPanel butPanel;  //the button panel
    private JPanel butPanel2; //the second button panel
    private ImagePanel plotPanel; //the device where the ANN or SVM is plotted
    private JTextPane textPanel; //the
    JScrollPane scrollPane;

    //RADIO BUTTONS
    JRadioButton annBut; //the button for choosing the ANN option
    JRadioButton svmBut; //the button for choosing the SVM option
    JRadioButton bayBut;  //the button for choosing the Naive Bayesian option
    JRadioButton fileWriteBut; //the button to choose if w should write out to file

    private String configFilePath = Main.CLASS_PATH + "/conf";
    

    //THE DEBUG STUFF
    private JRadioButton debugBut; //the Radio Button for debugging 
    static boolean DEBUG = false; 

    public static void main(String [] args){
        new MainWindow();
    }
    
    public MainWindow(){
        //creating the main window
        this.setSize(new Dimension(500,600));
        this.setTitle("Machine Learning Tools");

        //adding the jpanels to the window
        //This is 
        mainPanel = new JPanel();
        mainPanel.setPreferredSize(WINDOW_SIZE);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        //now the guiPanel
        guiPanel = new JPanel();
        guiPanel.setPreferredSize(new Dimension((int)WINDOW_SIZE.getWidth(),(int)(WINDOW_SIZE.getHeight()*4.0/5.0)));
        guiPanel.setBackground(Color.lightGray);        
        guiPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        guiPanel.setLayout(new BoxLayout(guiPanel, BoxLayout.X_AXIS));

        plotPanel = new ImagePanel();
        plotPanel.setPreferredSize(new Dimension( (int)(WINDOW_SIZE.getWidth()*3.0/5.0), (int)(WINDOW_SIZE.getHeight()*4.0/5.0))); 
        //guiPanel.add(plotPanel);

        //the panel that will show text output from ANN or SVM
        textPanel = new JTextPane(); // setting size color , border
        textPanel.setPreferredSize(new Dimension((int)(WINDOW_SIZE.getWidth()*2.0/5.0),(int)(WINDOW_SIZE.getHeight()*4.0/5.0)));
        textPanel.setBackground(new Color(210,210,230));        
        textPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        //setting text type
        textPanel.setContentType("text/html");
        textPanel.setEditable(false);

        //creating the scrolling thing
        scrollPane = new JScrollPane(textPanel);

        JScrollPane scrollFrame = new JScrollPane(textPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        textPanel.setAutoscrolls(true);
        //scrollFrame.setPreferredSize(new Dimension( 20,(int)(WINDOW_SIZE.getHeight()*4.0/5.0)));

        //guiPanel.add(textPanel);
        guiPanel.add(scrollFrame);

        //now the button panel
        butPanel = new JPanel();
        butPanel.setPreferredSize(new Dimension((int)WINDOW_SIZE.getWidth(),(int)(WINDOW_SIZE.getHeight()/10.0)));
        butPanel.setBackground(Color.darkGray);
        butPanel.setBorder(BorderFactory.createLineBorder(Color.white ) );

        butPanel2 = new JPanel();
        butPanel2.setPreferredSize(new Dimension((int)WINDOW_SIZE.getWidth(),(int)(WINDOW_SIZE.getHeight()/10.0)));
        butPanel2.setBackground(Color.blue);
        butPanel2.setBorder(BorderFactory.createLineBorder(Color.white ) );

        //adding the buttons to the butPanel

        //the button to open the data file that contains training and testing data
        JButton openDataBut = new JButton("Open Config File");
        openDataBut.setPreferredSize(BUTTON_SIZE); //setting size
        openDataBut.addActionListener(this); //adding the action listener 
        openDataBut.setActionCommand("openConf");
        butPanel.add(openDataBut);

        //the button to run the Neural Network or SVM or other ML tools
        JButton runBut = new JButton("Train");
        runBut.setPreferredSize(BUTTON_SIZE); //setting size
        runBut.addActionListener(this); //adding the action listener 
        runBut.setActionCommand("run");
        butPanel.add(runBut);

        
        //the button to run the Neural Network or SVM or other ML tools
        JButton testBut = new JButton("Test");
        testBut.setPreferredSize(BUTTON_SIZE); //setting size
        testBut.addActionListener(this); //adding the action listener 
        testBut.setActionCommand("test");
        butPanel.add(testBut);
        
        //the button to supply weights to ANN
        JButton getWeightsBut = new JButton("Save Model");
        getWeightsBut.setPreferredSize(BUTTON_SIZE); //setting size
        getWeightsBut.addActionListener(this); //adding the action listener 
        getWeightsBut.setActionCommand("saveModel");
        butPanel.add(getWeightsBut);

        debugBut = new JRadioButton("DEBUG");
        debugBut.addActionListener(this);
        debugBut.setActionCommand("debug");
        if( DEBUG ){
            debugBut.setSelected(true);
        }
        butPanel.add(debugBut);

        fileWriteBut = new JRadioButton("Write to File");
        fileWriteBut.addActionListener(this);
        fileWriteBut.setActionCommand("writeOut");
        fileWriteBut.setSelected(false);
        butPanel.add(fileWriteBut);
        
        //adding buttons to the second button
        //the ann radio button
        annBut = new JRadioButton("ANN");
        debugBut.addActionListener(this);
        debugBut.setActionCommand("ann");
        annBut.setSelected(true);

        //the svm radio button
        svmBut = new JRadioButton("SVM");
        debugBut.addActionListener(this);
        debugBut.setActionCommand("svm");

        //the naive bayesian radio button
        bayBut = new JRadioButton("N-Bay");
        debugBut.addActionListener(this);
        debugBut.setActionCommand("bay");

        //adding the buttons as a button group to the panel
        ButtonGroup grp1 = new ButtonGroup();
        grp1.add(annBut); grp1.add(svmBut); grp1.add(bayBut);
        butPanel2.add(annBut); butPanel2.add(svmBut); butPanel2.add(bayBut);

        //adding panels to mainPanel
        mainPanel.add(guiPanel);
        mainPanel.add(butPanel);
        mainPanel.add(butPanel2);
        //adding the mainPanel to the JFrame
        this.add(mainPanel);
        //setting up the JFrame, this
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);

    }

    public void actionPerformed(ActionEvent e){
        String cmd = e.getActionCommand();
        if(cmd.equals("run")){ //this  is the command for train button 
            //show the progress popup
            String processingString = "<html><pre>"+"\tPlease select a training data file or a saved model\n\tPROCESSING . . .\n\t This may take a while "+"</pre></html>";
            textPanel.setText(processingString);
            
            //repaint the pane
            scrollPane.repaint();
            
            //calling the main in the main class
            
            if(annBut.isSelected()){
                Main.main(new String[]{configFilePath , "ANN"});
            }else if (svmBut.isSelected() ){
                 Main.main(new String[]{configFilePath , "SVM"});
            }  else if( bayBut.isSelected() ){
                Main.main(new String[]{configFilePath , "BAY"});
            }
            
            //getting the output string
            String toPrint = "Training Succesfull";
            toPrint= "<html><pre>"+toPrint+"</pre></html>";
            toPrint = toPrint.replaceAll("[\r\n]+","<br>" );

            //print the string to the output
            textPanel.setText(toPrint  );

            //set the image //this code is useless
            //plotPanel.setImage(Main.CLASS_PATH +"/ann.png");
            //plotPanel.repaint();

            //repaint the pane
            scrollPane.repaint();
        }else if(cmd.equals("openConf")){
            //open file chooser
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File(Main.CLASS_PATH));
            int returnVal = fc.showOpenDialog(this);
            
            //check if some file was chosen
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                configFilePath = fc.getSelectedFile().getAbsolutePath(); 
                String toPrint= "Configuration file READ successfully!";
                toPrint = "<html><pre>"+toPrint+"</pre></html>";
                 //print the string to the output
                 textPanel.setText(toPrint  );
                 //repaint the pane
                 scrollPane.repaint();

            } else {
                System.out.println("Open command cancelled by user." );
            }
        }else if(cmd.equals("test")){
            if(annBut.isSelected()){
                String res = Main.testANN();
                String toPrint = "<html><pre>"+res+"</pre></html>";
                 //print the string to the output
                 textPanel.setText(toPrint  );
                 //repaint the pane
                 scrollPane.repaint();
            }else if (svmBut.isSelected() ){
                String res = Main.testSVM();
                String toPrint = "<html><pre>"+res+"</pre></html>";
                 //print the string to the output
                 textPanel.setText(toPrint  );
                 //repaint the pane
                 scrollPane.repaint(); 
                
            }  else if( bayBut.isSelected() ){
                 String res = Main.testBAY();
                String toPrint = "<html><pre>"+res+"</pre></html>";
                 //print the string to the output
                 textPanel.setText(toPrint  );
                 //repaint the pane
                 scrollPane.repaint(); 
            }
            
        }      
        else if(cmd.equals("debug")){
            if(debugBut.isSelected()){
                DEBUG = true;
            }else{
                DEBUG =false;
            }

        }else if(cmd.equals("writeOut")){
            if(debugBut.isSelected()){
                Main.saveOutput = true;
            }else{
                Main.saveOutput =false;
            }

        }
        else if(cmd.equals("saveModel")){ //gets called when we save the model
            String res="";
            if(annBut.isSelected()){
                res = Main.saveModel("ANN");
            }else if (svmBut.isSelected() ){
                res= Main.saveModel("SVM");
            }  else if( bayBut.isSelected() ){
                res = Main.saveModel("BAY");
            }

             String toPrint = res+"\n\n" + textPanel.getText();
                 //print the string to the output
                 textPanel.setText(toPrint  );
                 //repaint the pane
                 scrollPane.repaint(); 
        }
    }

    public void mouseClicked(MouseEvent e) {
    }
    //Unimplememted methods
    public void mouseExited(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {}

    public void mouseDragged(MouseEvent e) {}

}
