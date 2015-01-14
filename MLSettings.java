import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
/**
*This class encompasses all the parameters necessary to crete a ML model 
* @author Omar Farooq
* @date 7/28/14
*/
public class MLSettings extends JFrame{
	private final Dimension WINDOW_SIZE= new Dimension(400,700);
	private final Dimension TEXT_FIELD_SIZE =  new Dimension(300,40);
	private final Dimension SPACE_SIZE =  new Dimension(00,25);
	final int MAX_SETTINGS = 6;

	//this contains all the settings 
	String [] settings_array = new String[MAX_SETTINGS];

	public MLSettings(int modelType){
		this.setSize(WINDOW_SIZE);
		this.setTitle("Settings");

		//the main content panel that contains everything
		JPanel mainPanel = new JPanel();
		mainPanel.setPreferredSize(WINDOW_SIZE);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JPanel empty_panel = new JPanel();
		empty_panel.setPreferredSize(SPACE_SIZE);
		empty_panel.setMaximumSize(SPACE_SIZE);
		empty_panel.setMinimumSize(SPACE_SIZE);

		//creating dem lables
        JLabel l1 = new JLabel("Variable to Predict");
        JLabel l2 = new JLabel("Feature Variables (comma seperated)");
        JLabel l3 = new JLabel("Percent Data used Train (0.0-1.0)");
        JLabel l4 = new JLabel("The column to be printed with output");

        //creating dem textfields
        JTextField t1 = new JTextField();
        JTextField t2 = new JTextField();
        JTextField t3 = new JTextField();
        JTextField t4 = new JTextField();
        t1.setPreferredSize(TEXT_FIELD_SIZE);
        t1.setMaximumSize(TEXT_FIELD_SIZE);
        t2.setPreferredSize(TEXT_FIELD_SIZE);
        t2.setMaximumSize(TEXT_FIELD_SIZE);
        t3.setPreferredSize(TEXT_FIELD_SIZE);
        t3.setMaximumSize(TEXT_FIELD_SIZE);
        t4.setPreferredSize(TEXT_FIELD_SIZE);
        t4.setMaximumSize(TEXT_FIELD_SIZE);


        //adding things to the main  panel
        mainPanel.add(l1);
        mainPanel.add(t1);
        mainPanel.add(Box.createRigidArea(SPACE_SIZE));
		mainPanel.add(l2);
		mainPanel.add(t2);
		mainPanel.add(Box.createRigidArea(SPACE_SIZE));
		mainPanel.add(l3);
		mainPanel.add(t3);
		mainPanel.add(Box.createRigidArea(SPACE_SIZE));
		mainPanel.add(l4);
		mainPanel.add(t4);
		mainPanel.add(Box.createRigidArea(SPACE_SIZE));
		
		if(modelType==0){
			//Max nodes field
			JLabel l5 = new JLabel("Hidden Layer Nodes");
			JTextField t5 = new JTextField();
			t5.setPreferredSize(TEXT_FIELD_SIZE);
			t5.setMaximumSize(TEXT_FIELD_SIZE);

			//Scale output field
			JLabel l6 = new JLabel("Scale Output to Boolean");
			String[] options =  {"FALSE","TRUE"};
			JComboBox t6 = new JComboBox(options);
			t6.setPreferredSize(TEXT_FIELD_SIZE);
			t6.setMaximumSize(TEXT_FIELD_SIZE);

			mainPanel.add(l5);
			mainPanel.add(t5);
			mainPanel.add(Box.createRigidArea(SPACE_SIZE));
			mainPanel.add(l6);
			mainPanel.add(t6);

		}else if (modelType ==1){

		}else if (modelType==2){

		}

		this.add(mainPanel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.pack();
		this.setVisible(true);
	}

}