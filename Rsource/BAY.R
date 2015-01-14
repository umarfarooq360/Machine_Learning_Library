
  library(e1071);
 
  if(!readWeights){dataSet=read.csv( _PATHTOFILE );
  head(dataSet);}
  #BREAK#

   

  #BREAK#

  if(readWeights){ load ( _PATHTOFILE );  }
  else{classifier = naiveBayes( as.factor( PREDVARIABLE_ ) ~ FEATURES_ , dataSet );}

  #BREAK#

 
  #BREAK#

