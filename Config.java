import java.util.*;
import java.io.*;
/**
 * Write a description of class Config here.
 * 
 * @author (Omar Farooq) 
 * @version (6/5/14)
 */
public class Config
{
    Properties configFile; //hold the configuration file

    //constructor
    public Config(String filename)
    {
        configFile = new java.util.Properties();
        try {
            //read file
            FileInputStream file = new FileInputStream(filename);
            configFile.load(file);

        }catch(Exception eta){ //error catching
            System.err.println("ERROR: Couldn''t load configuration file");
            eta.printStackTrace();
        }
    }

    //constructor
    public Config(File input)
    {
        configFile = new java.util.Properties();
        try {
            configFile.load(new FileInputStream(input));

        }catch(Exception eta){ //catch exception
            System.err.println("ERROR: Couldn''t load configuration file");
            eta.printStackTrace();
        }

    }

    //get the value of key
    public String getProperty(String key)
    {
        String value = this.configFile.getProperty(key);
        return value;
    }

    public static void main(String[] args){
        if(args.length!= 1){
            System.err.println("Please give configuration file");
        }
        Config configuration = new Config(args[0]);

        Main.readWeights  = Boolean.parseBoolean( configuration.getProperty("readWeights")); //was the network already trained which means weights would be read from file
        Main.saveWeights = Boolean.parseBoolean(configuration.getProperty("saveWeights")); //should you save weights in a file
        //Main.percentTrain = Double.parseDouble(configuration.getProperty("percentTrain")); //the percent of the input data used for training
        Main.predVariable = configuration.getProperty("predVariable").trim();
        Main.numFeatures = Integer.parseInt(configuration.getProperty("numFeatures"));
        Main.featureVariables = parseFeatures(configuration.getProperty("featureVariables" ));            
        Main.printColumn = configuration.getProperty("printColumn");
        
        if(MainWindow.DEBUG){
            System.out.println("CONFIG: \n "+ Boolean.parseBoolean( configuration.getProperty("readWeights")) +"\n"//was the network already trained which means weights would be read from file
                + configuration.getProperty("saveWeights")  +"\n" //should you save weights in a file
                 //the percent of the input data used for training
                + configuration.getProperty("predVariable").trim() +"\n"
                +Integer.parseInt(configuration.getProperty("numFeatures")) +"\n"
                + Arrays.toString(parseFeatures(configuration.getProperty("featureVariables" ))) + "\n"
                +configuration.getProperty("printColumn"));
        }

    }
    //Goes through comma seperated features read from a file and stores them in an array
    static String[] parseFeatures(String features ){

        return features.trim().split(",");

    }

}

