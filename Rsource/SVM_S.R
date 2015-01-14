

  library(e1071);
 
  if(!readWeights){dataSet=read.csv( _PATHTOFILE );
  head(dataSet);}
  #BREAK#

 

  
  #BREAK#
  if(readWeights){ load ( _PATHTOFILE )}
  else{
  SVM.Model  = svm(PREDVARIABLE_ ~ FEATURES_ , data = dataSet,scale = TRUE ,cost =1000 ,gamma = 0.1);
  }
  #BREAK#


  
  #BREAK#
  
  
  finish = "Omar";
 