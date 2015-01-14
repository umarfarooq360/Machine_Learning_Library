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
