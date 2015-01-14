 
 HLNodes= 4 ;
  
  #BREAK#

   if(nchar(PATH) >0 ){setwd(PATH);};
  #dataSet = read.csv( "creditset.csv" );
 if( !readWeights ){ dataSet = read.csv( _PATHTOFILE ); }
  
  #BREAK#


  head(dataSet);

  
  #BREAK#
  
  if(readWeights){
    load( _PATHTOFILE );
  }else{   NNet =  neuralnet( PREDVARIABLE_ ~ FEATURES_  , dataSet, hidden = HLNodes ,linear.output=TRUE,lifesign="minimal",threshold = 0.01);  }

  #BREAK#

  
  #BREAK#
  

  #BREAK#   
 