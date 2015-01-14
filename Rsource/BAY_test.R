
  library(e1071);
 
  dataSet=read.csv(file.choose());
  
  #BREAK#

 
  index = 1:nrow(dataSet);  
  
  
  trainIndex = sample(index, trunc(length(index)* percentTrain ));
 
  testSet = dataSet[ -trainIndex ,  ];
  trainSet = dataSet[ trainIndex , ];

  #BREAK#

  
  classifier = naiveBayes( as.factor( default10yr ) ~ age,LTI , trainSet );

  #BREAK#

  Answers =  subset(testSet , select= c(  default10yr  ));
  testSet = subset(testSet ,  select= c(  age,LTI  )); 

  #BREAK#

  result = as.character(predict (classifier , testSet ));

  finish = "Omar";