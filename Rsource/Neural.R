 HLNodes= 4 ;
 trainingWeights = NULL; 
 
  #BREAK#

  
  dataSet = read.csv(file.choose());
  if(nchar(PATH) >0 ){setwd(PATH);}
  
  #BREAK#

  head(dataSet);
  numTrain = percentTrain*nrow(dataSet);
  trainSet = dataSet[1:numTrain,];
  testSet = dataSet[(numTrain+1):nrow(dataSet),];
  
  #BREAK#
  
  if(readWeights){
    load('weights.Rdata');
    NNet =  neuralnet( default10yr~ LTI + age, trainSet, hidden = HLNodes, startweights = finalWeights ,linear.output=F,lifesign="minimal",threshold = 0.1);
  }else{
    NNet =  neuralnet(default10yr~ LTI + age, trainSet, hidden = HLNodes ,linear.output=F,lifesign="minimal",threshold = 0.2);
  }

  #BREAK#
  finalWeights = NNet$weights[[1]];
  if(saveWeights){save(finalWeights , file='weights.Rdata'); }
  #source('plotANN.R');  

  #BREAK#
  
  #library(JavaGD);
  #JavaGD();
  plot(NNet , dimension =10 );
  #dev.off();

  #BREAK#   library(JavaGD);  JavaGD();  
 
  testSet_Features = subset(testSet,select=c('LTI','age'));
  NNresults = compute(NNet , testSet_Features );
  final_results = round(NNresults$net.result , 2 );  

