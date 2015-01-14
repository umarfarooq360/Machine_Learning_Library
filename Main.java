import org.rosuda.REngine.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;

/**
 * This is the main class containing interface code for R and Java. This will call machine learning code in 
 * R to use and create the ML models.
 * 
 * @author (Omar Farooq) 
 * @version (6/2/14)
 */
public class Main
{
    static final double MATCH_THRESHOLD = 0.5; // this threshold determines if two output given by ANN match  
    static final String CLASS_PATH = (new File("").getAbsolutePath());
    static final String DEFAULT_CONFIG_NAME = "def.conf";
    //String sourceCode ; //this String contains the R source code
    //REXP results ; //this is the REXP object that contains the results

    static REngine re; //the R engine for calling the R code

    static boolean readWeights  = false; //was the network already trained which means weights would be read from file
    static boolean saveWeights = false; //should you save weights in a file
    static double percentTrain = 1.0; //the percent of the input data used for training
    static String predVariable = "default10yr";
    static int numFeatures = 0;
    static String[] featureVariables =  new String[]{"age","LTI" } ;
    static boolean saveOutput = false; //should the output be written to a file
    static String printColumn = "";

    static boolean wasModelCreated = false; //was the program run and a model  created

    static String outputString;
    public static void main (String[] args)
    {
        try{
            String modelType="";
            if(args.length==0){
                System.err.println("ERROR: There were no arguments specified.Usage = <Configuration file> <Type of model>");
                System.err.println("Type of Model -> ANN,SVM,BAY for Neural network, support vectore machine or naive bayesian"); 
                System.exit(1);
            }
            else if(args.length ==1){
                System.err.println("WARNING: No config file was specified, arg [0] used as model type ");
                Config.main(new String[] {new String(CLASS_PATH+"/def.conf")});  //this line causes errors
                modelType = args[0];
            }else{
                //reading the configuration file
                Config.main(new String[] {args[0]} );
                modelType = args[1];
            }
            //checking for errors
            if(numFeatures != featureVariables.length){
                System.err.println("ERROR: Feature list and number don't match");
            }

            //initializing new R-engine which imports the R library into java
            re= REngine.engineForClass("org.rosuda.REngine.JRI.JRIEngine");

            //dont know that this does
            re.assign("num", new int[] { 1 });
            //checks if the engine is working
            if(MainWindow.DEBUG)System.out.println(re.parseAndEval ("num;").asInteger());
            if(re.parseAndEval ("num;").asInteger()!=1){
                System.out.println ("R Engine could not be started \n ABORTING...");
                System.exit(1);
            }
            //             String ss  = getSourceCode(CLASS_PATH+"/Rsource/ANN.R");
            //             String[] ssp = splitCode(ss);

            if(MainWindow.DEBUG)System.out.println(CLASS_PATH);

            if(modelType.equals("ANN")){
                createANN();
            }
            else if(modelType.equals("SVM")){
                createSVM();
            }else if (modelType.equals("BAY")){
                createBAY();
            }

            re.close();
        }catch(Exception e){
            System.err.println("ERROR: File was not found (main)");
            e.printStackTrace();
        }

    }

    /**
     * This method returns R code read from a file as a String.
     */
    public static String getSourceCode (String filename){
        try{//this only works for Java 7 onwards
            String code  = new Scanner(new File(filename)).useDelimiter("\\Z").next();
            return code.trim();
        }     
        catch(Exception e ){
            e.printStackTrace();
        }
        return null;
    }

    /**
     *This method checks if the package specified is installed in R.
     */
    static void checkPackages(String packageName){
        if(re == null){ //error check
            System.err.println("ERROR: re is null");
        }

        try{
            //the code will check if R has the package installed
            String code  = getSourceCode(CLASS_PATH+"/Rsource/pkgTest.R");
            //put the code in one line
            //code = code.replaceAll("[\r\n]+", " ");
            //load the R function that checks packages
            re.parseAndEval(code);
            //check if the package is installed by calling the function
            REXP retVal = re.parseAndEval("pkgTest(\""+ packageName  +"\");" );
            //give error if package couldn't be installed
            if (retVal!= null ) {
                if(retVal.asInteger() ==0 ){System.err.println("Package not found ERROR");
                    re.parseAndEval("install.packages(\""+packageName + "\");");
                    return;}
                else{  //print package was succesfully installed
                    if(MainWindow.DEBUG)System.out.println("Package "+ packageName +" was found");
                }
            }else{
                System.out.println("Bulls");}
        }catch(Exception e ){ 
            e.printStackTrace();
        }
    }

    /**
     *   This method breaks the R code into portions where each portion is seperated by
     *   "#BREAK#"
     */
    static String[] splitCode(String code){
        //arraylist holding all the portions
        ArrayList<String> array =  new ArrayList<String>();
        //scanner to scan through the code
        Scanner s  = new Scanner(code);
        String aPortion = "";
        String line  = "";
        while(s.hasNextLine()){
            line = s.nextLine(); //advance to next line
            if (line.contains("#BREAK#")) {//find the Break token
                array.add(aPortion);
                //System.out.println(aPortion);
                aPortion = "";
            }else if(line.startsWith("#")||line.contains("#")){
                continue; //skip the line
            }
            else{
                aPortion += line; //add chunks to the line
            }

        }
        array.add(aPortion);
        return array.toArray(new String[array.size()]); //return as an array
    }

    /**
     *   This method creates an ANN in R and returns the ANN
     */
    static REXP createANN(){
        // Checks if the neural net package is installed in R
        checkPackages("neuralnet");
        checkPackages("JavaGD");

        //imports the R source Code and breaks it into parts for execution //change back to ANN.R
        String [] sourceCode = splitCode(getSourceCode( CLASS_PATH+"/Rsource/ANN.R" ));

        //for debug purposes
        if(MainWindow.DEBUG){System.out.println(Arrays.toString(sourceCode));}

        try{
            //run all parts of source code
            re.parseAndEval("library(neuralnet);");

            //declaring parameters and variables in R using class variables
            //which were originally read from configuration file
            re.parseAndEval((String)("percentTrain="+String.valueOf(1.0)+";" ));
            re.parseAndEval("PATH = \""+ CLASS_PATH.replaceAll("\\\\","/")+"/Rsource\";");
            if(MainWindow.DEBUG){System.out.println("PATH in R : " + re.parseAndEval("PATH;").asString());}
            re.parseAndEval("saveWeights = " + Boolean.toString(saveWeights).toUpperCase() + ";" );
            int retVal = -1;
            JFileChooser fileChooser = null;
            for (int i = 0; i <sourceCode.length ;i++ ) {
                if(MainWindow.DEBUG){System.out.println("At code portion: "+ i);} //for debug

                if(i==1){
                     fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File(CLASS_PATH));
                     retVal = fileChooser.showOpenDialog(null);
                    if(retVal == JFileChooser.APPROVE_OPTION ){
                        String file_path = fileChooser.getSelectedFile().getAbsolutePath();
                        sourceCode[i]=sourceCode[i].replaceAll("_PATHTOFILE", "\""+file_path.replaceAll("\\\\","/")+"\"");
                    }

                }

                if(i==3){
                    sourceCode[i] = sourceCode[i].replaceAll("PREDVARIABLE_", predVariable );
                    sourceCode[i] = sourceCode[i].replaceAll("FEATURES_", createFeatureString(0)  );
                    
                    if(retVal == JFileChooser.APPROVE_OPTION ){
                        String file_path = fileChooser.getSelectedFile().getAbsolutePath();
                        sourceCode[i]=sourceCode[i].replaceAll("_PATHTOFILE", "\""+file_path.replaceAll("\\\\","/")+"\"");
                    }
                    
                    
                    if(MainWindow.DEBUG) System.out.println(sourceCode[i] );
                }else if( i==5 || i==6){
                    sourceCode[i] = sourceCode[i].replaceAll("FEATURES_", createFeatureString(1)  );
                    if(MainWindow.DEBUG) System.out.println(sourceCode[i] );
                }

                if(MainWindow.DEBUG) System.out.println(sourceCode[i] );
                re.parseAndEval (sourceCode[i]); //run the code portion
            }
            //debug stuff
            if(MainWindow.DEBUG){
                System.out.println("Path was set to : " + re.parseAndEval("PATH;").asString());
                //System.out.println(Arrays.toString(re.parseAndEval("NNet$weights[[1]][[1]][,1];").asDoubles()));

            }
            //re.parseAndEval("testSet_Features = subset(testSet,select=c( "+createFeatureString(1) +"))");
            //re.parseAndEval("NNresults = compute(NNet , testSet_Features );  final_results = as.vector(round(NNresults$net.result , 2 ))");
            REXP ANN = re.parseAndEval("NNet;"); //store the ANN in an REXP object
            wasModelCreated = true;
            return ANN; //return ann object
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    static  String testANN(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(CLASS_PATH));
        int retVal = fileChooser.showOpenDialog(null);
        if(retVal == JFileChooser.APPROVE_OPTION ){
            String file_path = fileChooser.getSelectedFile().getAbsolutePath();
            try{
                if( re.parseAndEval("exists(\"NNet\")" ).asInteger() != 1 ){
                    System.err.println("ERRORRR");
                }

                if(MainWindow.DEBUG){
                    System.out.println("Does ANN exist " + re.parseAndEval("exists(\"NNet\")" ).asString() );
                    System.out.println("TESTING COMMANDS \n\n "+"test_data = read.csv (\""+file_path.replaceAll("\\\\","/")+"\");"
                        +"\n"+"testSet_Features = subset(test_data,select=c("+ createFeatureString(1) +")); write.csv(testSet_Features, file='blah.csv')"+
                        "\n"+"NNresults = compute(NNet , testSet_Features )" +
                        "\n"+"final_results = as.vector(round(NNresults$net.result , 5 ))");
                }

                //The code for the actual testing
                //read the test file
                re.parseAndEval("test_data = read.csv (\""+file_path.replaceAll("\\\\","/")+"\");");
                //extract  only the specified features from the test data set
                re.parseAndEval("testSet_Features = subset(test_data,select=c("+ createFeatureString(1) +"));" );

                // if(MainWindow.DEBUG) {System.out.print( Arrays.toString(re.parseAndEval("testSet_Features[\"Class\"]" ).asDoubles() ) );  }

                //compute the stuff
                re.parseAndEval("NNresults = compute(NNet , testSet_Features );");
                //store the final results
                re.parseAndEval("final_results = as.vector(round(NNresults$net.result , 5 ));");

                if(saveOutput){//if we want to save the output as a csv
                    JFileChooser fileChooser2 = new JFileChooser();
                    fileChooser2.setCurrentDirectory(new File(CLASS_PATH));
                    int retVal2 = fileChooser2.showSaveDialog(null);
                    if(retVal2 == JFileChooser.APPROVE_OPTION ){
                        String file_path2 = fileChooser2.getSelectedFile().getAbsolutePath();
                        re.parseAndEval("testSet_Features[,'Predicted "+ predVariable+"'] = final_results;");
                        re.parseAndEval("write.csv(testSet_Features, file='" + file_path2.replaceAll("\\\\","/") +"')" );
                        if(MainWindow.DEBUG){System.out.println("write.csv(testSet_Features , file='" + file_path2.replaceAll("\\\\","/") +"')");}
                    }
                    else{
                        System.out.println("Didnt choose a place to save results");
                    }
                }

                double[] results = re.parseAndEval("final_results").asDoubles() ;
                System.out.println(Arrays.toString(results));
                String ans  = "Predicted "+ predVariable+"\n----------------\n\n\t";
                for (int i=0 ; i<results.length ; i++){
                    ans+= (results[i]) + "\n\t";
                }

                return ans;
            }catch(Exception e){

            }
        }
        else{
            System.out.print("You didnt choose a test file");
            return "Error testing ANN!";
        }
        return null;
    }

    static REXP createSVM(){
        //checking if packages are installed
        checkPackages("e1071");
        checkPackages("rpart");

        //imports the R source Code and breaks it into parts for execution
        String [] sourceCode = splitCode(getSourceCode( CLASS_PATH+"/Rsource/SVM_S.R" ));

        //for debug purposes
        if(MainWindow.DEBUG){System.out.println(Arrays.toString(sourceCode));}

        try{
            //run all parts of source code

            //declaring parameters and variables in R using class variables
            //which were originally read from configuration file
            re.parseAndEval((String)("percentTrain="+String.valueOf(percentTrain)+";" ));
            re.parseAndEval("PATH = \""+ CLASS_PATH.replaceAll("\\\\","/")+"/Rsource\";");
            re.parseAndEval("saveWeights = " + Boolean.toString(saveWeights).toUpperCase() + ";" );
            re.parseAndEval("readWeights = " + Boolean.toString(readWeights).toUpperCase() + ";" );
            
            int retVal = -1;
            JFileChooser fileChooser = null;
            //loops goes through and calls the source code
            for (int i = 0; i <sourceCode.length ;i++ ) {

                if(i==0){
                     fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File(CLASS_PATH));
                     retVal = fileChooser.showOpenDialog(null);
                    if(retVal == JFileChooser.APPROVE_OPTION ){
                        String file_path = fileChooser.getSelectedFile().getAbsolutePath();
                        sourceCode[i]=sourceCode[i].replaceAll("_PATHTOFILE", "\""+file_path.replaceAll("\\\\","/")+"\"");
                    }
                    else{
                        System.out.println("PLEASE CHOOSE A DATA FILE");
                    }

                }

                if(MainWindow.DEBUG){System.out.println("At code portion: "+ i+ "\n"+sourceCode[i]+"\n" );} //for debug

                //if(MainWindow.DEBUG && i==2){System.out.println("\n SVM SUMMARY :\n\n " + (Arrays.toString(re.parseAndEval("trainIndex;").asIntegers())));}
                if(i==2){
                    sourceCode[i] = sourceCode[i].replaceAll("PREDVARIABLE_", predVariable );
                    sourceCode[i] = sourceCode[i].replaceAll("FEATURES_", createFeatureString(0)  );
                    if(retVal == JFileChooser.APPROVE_OPTION ){
                        String file_path = fileChooser.getSelectedFile().getAbsolutePath();
                        sourceCode[i]=sourceCode[i].replaceAll("_PATHTOFILE", "\""+file_path.replaceAll("\\\\","/")+"\"");
                    }
                    
                    if(MainWindow.DEBUG) System.out.println(sourceCode[i] );
                }else if(i==3){
                    sourceCode[i] = sourceCode[i].replaceAll("FEATURES_", createFeatureString(1)  );
                    sourceCode[i] = sourceCode[i].replaceAll("PREDVARIABLE_", predVariable );
                    if(MainWindow.DEBUG) System.out.println(sourceCode[i] );
                }

                re.parseAndEval (sourceCode[i]); //run the code portion
            }

            //Two arrays  for  the actual and predicted output
            if(MainWindow.DEBUG){System.out.println("Path was set to : " + re.parseAndEval("PATH;").asString());}

            //             if(!re.parseAndEval("finish").asString().equals("Omar")){
            //                 System.err.println("Error in R code; Didn't execute fully");
            //             }

            REXP SVM = re.parseAndEval("SVM.Model;"); //store the ANN in an REXP object
            wasModelCreated = true;
            return SVM;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;

    }

    static  String testSVM(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(CLASS_PATH));
        int retVal = fileChooser.showOpenDialog(null);
        if(retVal == JFileChooser.APPROVE_OPTION ){
            String file_path = fileChooser.getSelectedFile().getAbsolutePath();
            try{
                if( re.parseAndEval("exists(\"SVM.Model\")" ).asInteger() != 1 ){
                    //System.err.println("ERROR SVM was not created");
                    return "ERROR  SVM was not created";
                }

                if(MainWindow.DEBUG){
                    System.out.println("Does ANN exist " + re.parseAndEval("exists(\"SVM.Model\")" ).asString() );
                    System.out.println("TESTING COMMANDS \n\n "+"test_data = read.csv (\""+file_path.replaceAll("\\\\","/")+"\");"
                        +"\n"+"testSet_Features = subset(test_data,select=c("+ createFeatureString(1) +")); write.csv(testSet_Features, file='testfile.csv')"+
                        "\n"+" SVM.Pred = predict(SVM.Model ,testSet_Features );" +
                        "\n"+"final_results = round(as.data.frame(SVM.Pred)$SVM.Pred  , 5) ;");
                }

                //read the file specified by the 
                re.parseAndEval("test_data = read.csv (\""+file_path.replaceAll("\\\\","/")+"\");");

                //extract  only the specified features from the test data set
                re.parseAndEval("testSet_Features = subset(test_data,select=c("+ createFeatureString(1) +")); " );

                // if(MainWindow.DEBUG) {System.out.print( Arrays.toString(re.parseAndEval("testSet_Features[\"Class\"]" ).asDoubles() ) );  }

                //compute the stuff
                re.parseAndEval("SVM.Pred = predict(SVM.Model ,testSet_Features );");

                re.parseAndEval("final_results = round(as.data.frame(SVM.Pred)$SVM.Pred  , 5) ;");

                if(saveOutput){//if we want to save the output as a csv
                    JFileChooser fileChooser2 = new JFileChooser();
                    fileChooser2.setCurrentDirectory(new File(CLASS_PATH));
                    int retVal2 = fileChooser2.showSaveDialog(null);
                    if(retVal2 == JFileChooser.APPROVE_OPTION ){
                         String file_path2 = fileChooser2.getSelectedFile().getAbsolutePath();
                        re.parseAndEval("testSet_Features[,'Predicted "+ predVariable+"'] = final_results;");
                        re.parseAndEval("write.csv(testSet_Features, file='" + file_path2.replaceAll("\\\\","/") +"')" );
                        if(MainWindow.DEBUG){System.out.println("write.csv(testSet_Features , file='" + file_path2.replaceAll("\\\\","/") +"')");}
                    }
                    else{
                        System.out.println("Didnt choose a place to save results");
                    }
                }

                double[] results = re.parseAndEval("final_results").asDoubles() ;
                if(MainWindow.DEBUG){System.out.println(Arrays.toString(results));}
                String ans  = "Predicted "+ predVariable+"\n----------------\n\n\t";
                for (int i=0 ; i<results.length ; i++){
                    ans+= (results[i]) + "\n\t";
                }

                return ans;
            }catch(Exception e){

            }
        }
        else{
            System.out.print("You didnt choose a test file");
            return "Error testing SVM!";
        }
        return null;
    }

    static REXP createBAY(){
        //checking if packages are installed
        checkPackages("e1071");

        //imports the R source Code and breaks it into parts for execution
        String [] sourceCode = splitCode(getSourceCode( CLASS_PATH+"/Rsource/BAY.R" ));

        //for debug purposes
        if(MainWindow.DEBUG){System.out.println(Arrays.toString(sourceCode));}

        try{
            //run all parts of source code

            //declaring parameters and variables in R using class variables
            //which were originally read from configuration file
            re.parseAndEval((String)("percentTrain="+String.valueOf(percentTrain)+";" ));
            re.parseAndEval("PATH = \""+ CLASS_PATH.replaceAll("\\\\","/")+"/Rsource\";");
            re.parseAndEval("saveWeights = " + Boolean.toString(saveWeights).toUpperCase() + ";" );
            re.parseAndEval("readWeights = " + Boolean.toString(readWeights).toUpperCase() + ";" );

            int retVal = -1;   
            JFileChooser fileChooser = null;
            for (int i = 0; i <sourceCode.length ;i++ ) {
                if(i==0){
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File(CLASS_PATH));
                     retVal = fileChooser.showOpenDialog(null);
                    if(retVal == JFileChooser.APPROVE_OPTION ){
                        String file_path = fileChooser.getSelectedFile().getAbsolutePath();
                        sourceCode[i]=sourceCode[i].replaceAll("_PATHTOFILE", "\""+file_path.replaceAll("\\\\","/")+"\"");
                    }   
                    else{
                        System.out.println("PLEASE CHOOSE A DATA FILE");
                    }

                }
                if(MainWindow.DEBUG){System.out.println("At code portion: "+ i+ "\n"+sourceCode[i]+"\n" );} //for debug

                //if(MainWindow.DEBUG && i==2){System.out.println("\n SVM SUMMARY :\n\n " + (Arrays.toString(re.parseAndEval("trainIndex;").asIntegers())));}
                if(i==2){
                    
                    sourceCode[i] = sourceCode[i].replaceAll("PREDVARIABLE_", predVariable );
                    sourceCode[i] = sourceCode[i].replaceAll("FEATURES_", createFeatureString(0)  );
                    String file_path = fileChooser.getSelectedFile().getAbsolutePath();
                    sourceCode[i]=sourceCode[i].replaceAll("_PATHTOFILE", "\""+file_path.replaceAll("\\\\","/")+"\"");
                    
                    
                    if(MainWindow.DEBUG) System.out.println(sourceCode[i] );
                }else if(i==3){
                    sourceCode[i] = sourceCode[i].replaceAll("FEATURES_", createFeatureString(1)  );
                    sourceCode[i] = sourceCode[i].replaceAll("PREDVARIABLE_", predVariable );
                    if(MainWindow.DEBUG) System.out.println(sourceCode[i] );
                }

                re.parseAndEval (sourceCode[i]); //run the code portion
            }

            //Two arrays  for  the actual and predicted output
            if(MainWindow.DEBUG){System.out.println("Path was set to : " + re.parseAndEval("PATH;").asString());}

            REXP BAY_NETWORK = re.parseAndEval("classifier;"); //store the ANN in an REXP object
            wasModelCreated = true;
            return BAY_NETWORK;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;

    }
    
    
    static  String testBAY(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(CLASS_PATH));
        int retVal = fileChooser.showOpenDialog(null);
        if(retVal == JFileChooser.APPROVE_OPTION ){
            String file_path = fileChooser.getSelectedFile().getAbsolutePath();
            try{
                if( re.parseAndEval("exists(\"classifier\")" ).asInteger() != 1 ){
                    //System.err.println("ERROR SVM was not created");
                    return "ERROR Bayesian network was not created";
                }

                if(MainWindow.DEBUG){
                    System.out.println("Does BAY exist " + re.parseAndEval("exists(\"classifier\")" ).asString() );
                    System.out.println("TESTING COMMANDS \n\n "+"test_data = read.csv (\""+file_path.replaceAll("\\\\","/")+"\");"
                        +"\n"+"testSet_Features = subset(test_data,select=c("+ createFeatureString(1) +")); "+
                        "\n"+"" +
                        "\n"+"result = as.character(predict(classifier ,testSet_Features ));");
                }

                //read the file specified by the 
                re.parseAndEval("test_data = read.csv (\""+file_path.replaceAll("\\\\","/")+"\");");

                //extract  only the specified features from the test data set
                re.parseAndEval("testSet_Features = subset(test_data,select=c("+ createFeatureString(1) +")); " );

                // if(MainWindow.DEBUG) {System.out.print( Arrays.toString(re.parseAndEval("testSet_Features[\"Class\"]" ).asDoubles() ) );  }

                //compute the stuff
                re.parseAndEval("result = as.character(predict(classifier ,testSet_Features ));");

                //re.parseAndEval("final_results = round(as.data.frame(SVM.Pred)$SVM.Pred  , 5) ;");

                if(saveOutput){//if we want to save the output as a csv
                    JFileChooser fileChooser2 = new JFileChooser();
                    fileChooser2.setCurrentDirectory(new File(CLASS_PATH));
                    int retVal2 = fileChooser2.showSaveDialog(null);
                    if(retVal2 == JFileChooser.APPROVE_OPTION ){
                          String file_path2 = fileChooser2.getSelectedFile().getAbsolutePath();
                        re.parseAndEval("testSet_Features[,'Predicted "+ predVariable+"'] = result;");
                        re.parseAndEval("write.csv(testSet_Features, file='" + file_path2.replaceAll("\\\\","/") +"')" );
                        if(MainWindow.DEBUG){System.out.println("write.csv(testSet_Features , file='" + file_path2.replaceAll("\\\\","/") +"')");}
                  }
                    else{
                        System.out.println("Didnt choose a place to save results");
                    }
                }

                String[] results = re.parseAndEval("result").asStrings() ;
                if(MainWindow.DEBUG){System.out.println(Arrays.toString(results));}
                String ans  = "Predicted "+ predVariable+"\n----------------\n\n\t";
                for (int i=0 ; i<results.length ; i++){
                    ans+= (results[i]) + "\n\t";
                }

                return ans;
            }catch(Exception e){

            }
        }
        else{
            System.out.print("You didnt choose a test file");
            return "Error testing SVM!";
        }
        return null;
    }
    
    
    static String saveModel(String modelType){
        if(!wasModelCreated){
            return "No model was created/trained !";
        }

        try{
            //reading the file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(CLASS_PATH));
            int retVal = fileChooser.showSaveDialog(null);
            if(retVal == JFileChooser.APPROVE_OPTION ){
                String file_path = fileChooser.getSelectedFile().getAbsolutePath();
                if(modelType.equals("ANN")){
                    if (MainWindow.DEBUG){ System.out.println("save( \"NNet\" ,file=\""+file_path.replaceAll("\\\\","/") + "\");");}
                    re.parseAndEval("save( \"NNet\" ,file=\""+file_path.replaceAll("\\\\","/") + "\");");
                }else if(modelType.equals("SVM")){
                    if (MainWindow.DEBUG){ System.out.println("save( \"SVM.Model\" ,file=\""+file_path.replaceAll("\\\\","/") + "\");");}
                    re.parseAndEval("save( \"SVM.Model\" ,file=\""+file_path.replaceAll("\\\\","/") + "\");");
                    
                }else if(modelType.equals("BAY")){
                    if (MainWindow.DEBUG){ System.out.println("save( \"classifier\" ,file=\""+file_path.replaceAll("\\\\","/") + "\");");}
                    re.parseAndEval("save( \"classifier\" ,file=\""+file_path.replaceAll("\\\\","/") + "\");");
                    
                }

            }
            return "Save Successful";     

        }catch(Exception e){

        }

        return "ERROR saving model!!!!";
    }

    /**
     * This method runs tests on an alrady created Machine learning structure.
     * You will specify a data set to be read from a file. features will have been supplied 
     * from a configuration file
     */
    static void runTest(String modelType ){
        if(!wasModelCreated){
            JOptionPane.showMessageDialog(null,"Model needs to be created to run tests","Error",JOptionPane.WARNING_MESSAGE);
        }else{
            try{
                //reading the file
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(CLASS_PATH));
                int retVal = fileChooser.showOpenDialog(null);
                if(retVal == JFileChooser.APPROVE_OPTION ){
                    String file_path = fileChooser.getSelectedFile().getAbsolutePath();
                    re.parseAndEval("testData = read.csv(\""+file_path + "\");");
                }
                else{
                    System.out.println("PLEASE CHOOSE A DATA FILE for testing ");
                }
                //creating subset of data using the features only
                re.parseAndEval("testData_features = subset(testData , select=c(" + createFeatureString(1) + "));");                
                //picking a model
                if(modelType.equals("ANN")){
                    re.parseAndEval("NN_out= compute(NNet , testData_features)");
                    re.parseAndEval("final_out = as.vector(NN_out$net.result);");
                }else if(modelType.equals("SVM")){
                }else if (modelType.equals("BAY")){
                }

                //storing the output as an array
                String [] results = re.parseAndEval("final_out;").asStrings();
                //the column which is also to be printed
                String [] idColumn  = null;
                if(!printColumn.equals(""))idColumn = re.parseAndEval("testSet$"+printColumn+ ";").asStrings();

                //output string generated
                String print_out = printOutput(idColumn , null , results);

            }
            catch(Exception e){

            }
        }

    }

    /**
     *   This method prints the output
     */
    static String printOutput(String [] idCol , double[] actual, double[] predicted){
        // Header for output
        String out = "\n\t--------OUTPUT------\n\n" ;
        out+= printColumn + " \t ACTUAL   PREDICTED \n\n" ;

        int count = 0;//counting how well the output matches
        for (int i = 0;i<predicted.length ; i++) {
            if(idCol!= null){
                out+= idCol[i] + " " ;
            }
            if(actual!=null){
                out+=" \t\t " + actual[i];
                if(Math.abs(actual[i]-predicted[i])<MATCH_THRESHOLD){
                    count++;
                }
            }
            out+= "       "+predicted[i]+ "\n" ;    //printing out table with predicted and actual
        }
        out+="\n--------SUMMARY-------\n Match Percent : " + (double)count*100.0/((double)predicted.length) ;
        System.out.println(out); 
        outputString = out;
        return out;
    }

    /**
     *   This method prints the output takes in two string arrays to compare their equality
     */
    static String printOutput(String[] idCol , String[] actual, String[] predicted){
        // Header for output
        String out = "\n\t--------OUTPUT------\n\n" ;
        out+=printColumn+ " \t ACTUAL \t PREDICTED \n\n" ;

        int count = 0;//counting how well the output matches
        for (int i = 0;i<predicted.length ; i++) {
            if(idCol!= null){
                out+= idCol[i] + " " ;
            }
            if(actual!=null){
                out+=" \t\t " + actual[i];
                if(actual[i].equalsIgnoreCase(predicted[i])){
                    count++;
                }
            }
            out+= "       "+predicted[i]+ "\n" ;    //printing out table with predicted and actual
        }
        out+="\n--------SUMMARY-------\n Match Percent : " + (double)count*100.0/((double)predicted.length) ;
        System.out.println(out); 
        outputString = out;
        return out;
    }

    /**
     * Creates a String that will be passed into R to specify the features.
     * There will be two types of these Strings. One seperated by plus signs and the other by commas
     * 
     * Type 0: seperated by +
     * Type 1 = seperated by ,
     */
    static String createFeatureString( int type){
        String features = "";
        if(type ==0){
            for( int i = 0;i<(featureVariables).length -1 ;i++ ){
                features += featureVariables[i] + "+" ;
            }
            features += featureVariables[featureVariables.length -1];
        }else if(type ==1){

            for( int i = 0;i<(featureVariables).length -1 ;i++ ){
                features += ""+ featureVariables[i] + "," ;
            }
            features += ""+ featureVariables[featureVariables.length -1] +"";
        }else{
            System.err.println("ERROR:Wrong type of feature string parsing");
        }        
        return features.trim();
    }

}
