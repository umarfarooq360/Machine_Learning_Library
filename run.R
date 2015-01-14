
doesPredictionMatch = function( expected =NULL , predicted = NULL , threshold = 0.3){
  if(is.null(expected) || is.null(predicted)){
    print("Necessary arguments missing or null");
    stop();
  }
  
  results = rep(FALSE,length(expected));
  
  for(i in 1:length(expected)){
    if((!is.na(expected[i]))&&(!is.na(predicted[i]))){  
      if(abs(expected[i]-predicted[i])<threshold ){
        results[i] = TRUE;
      }
    }
      
  }
  return (results);
}
#BREAK#

countSuccessPercent = function(input = NULL){
  count= 0;
  for(i in 1:length(input)){
    tmp =as.logical(input[i]); 
    if(is.logical(tmp) && tmp ==TRUE ){
      count=count+1;
    }
  }
  return ((count/length(input))*100);  
}
#BREAK#

percentTrain= 0.5;
 roundOff =T ;
 outputClosenessThreshold = 0.3 ; 
 HLNodes = 4 ;

  dataSet = read.csv(file.choose());
  head(dataSet);
  numTrain = percentTrain*nrow(dataSet);
  trainSet = dataSet[1:numTrain,];
  testSet = dataSet[(numTrain+1):nrow(dataSet),];
 #BREAK#
  NNet =  neuralnet(default10yr~ LTI + age, trainSet, 
                    hidden = HLNodes ,linear.output=F,
                    lifesign="minimal",threshold = 0.1);
 
  
  testSet_Features = subset(testSet,select=c("LTI","age"));
  NNresults = compute(NNet , testSet_Features );
  