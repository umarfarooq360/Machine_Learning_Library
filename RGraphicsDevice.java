import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

import org.rosuda.REngine.*;
import org.rosuda.javaGD.GDInterface;
import org.rosuda.javaGD.JGDBufferedPanel;

/**
 * Write a description of class RGraphicsDevice here.
 * 
 * @author (Omar) 
 * @version (6/4/14)
 */
public class RGraphicsDevice extends GDInterface
{
    JGDBufferedPanel panel;
     
    public RGraphicsDevice(int x,int y) {
        panel = new JGDBufferedPanel(x,y);
    }

    public static void main(String[] args) {
    	try{REngine R = REngine.engineForClass("org.rosuda.REngine.JRI.JRIEngine");

    	//R.parseAndEval("library(JavaGD);JavaGD() ; hist(rnorm(100),col=\"lightblue\");");

    	String x = R.parseAndEval("print('Hello A');x= scan(file.choose());").asString();
    	System.out.println(x);
    	
    	R.close();}
    	catch(Exception e){
    	    //asd
    	   }
    }
    
}

