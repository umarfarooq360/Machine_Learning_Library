import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
/**
 * Write a description of class ImagePanel here.
 * 
 * @author (Omar Farooq) 
 * @version (a version number or a date)
 */


public class ImagePanel extends JPanel {

    private BufferedImage image;
    
    public ImagePanel(){
        //default constructor
    }
    
    public ImagePanel(String filename) {
       try {                
          image = ImageIO.read(new File(filename));
       } catch (IOException ex) {
            System.err.println("Image not found");
       }
    }
    
    public void setImage (String filename){
        try {                
          image = ImageIO.read(new File(filename));
       } catch (IOException ex) {
            System.err.println("Image not found");
       }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image!=null)g.drawImage(image, 0, 0, null); // see javadoc for more info on the parameters            
    }

}