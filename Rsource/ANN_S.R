 
  HLNodes= 4 ;
  
  #BREAK#

   if(nchar(PATH) >0 ){setwd(PATH);};
  #dataSet = read.csv( "creditset.csv" );
  dataSet = read.csv( file.choose() );
  
  #BREAK#

  head(dataSet);
  numTrain = percentTrain*nrow(dataSet);
  trainSet = dataSet[1:numTrain,];
  testSet = dataSet[(numTrain+1):nrow(dataSet),];
  
  #BREAK#
  
   NNet =  neuralnet( PREDVARIABLE_ ~ FEATURES_  , trainSet, hidden = HLNodes ,linear.output=F,lifesign="minimal",threshold = 0.2);  

  #BREAK#

  x=2;



  #BREAK#   

  testSet_Features = subset(testSet,select=c( FEATURES_ ));
  NNresults = compute(NNet , testSet_Features );
  final_results = as.vector(round(NNresults$net.result , 2 ));  
  y=3;

  #BREAK#

  z=4;

