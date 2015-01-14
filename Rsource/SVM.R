
  #import the required libraries
  library(e1071);
  library(rpart);

  #Import the dataset 
  dataSet=read.csv(file.choose());
  
  #BREAK#

  #Delete first column, named a in data which just has indices
  #dataSet = subset(dataSet , select= -c(a) );
  
  #Seperate indices for test and training data
  index = 1:nrow(dataSet);  #The whole dataset index
  
  #Sample randomly from the indices to get test and training indices
  trainIndex = sample(index, trunc(length(index)* percentTrain ));
  #Using indices to get sets
  testSet = dataSet[ -trainIndex ,  ];
  trainSet = dataSet[ trainIndex , ];
  
  #BREAK#

  #Creating the SVM. default10yr is the name of column with dataSet type
 
  SVM.Model  = svm(default10yr ~ age + LTI , data =trainSet,scale = TRUE ,cost =1000 ,gamma =1);
  
  #-----------------
  #Optional : Tune the SVM
  #   tobj =  tune.svm( default10yr ~ LTI + age , data = trainSet
  #   ,gamma = 10^(-4:-1) ,cost =10^(1:2) )
  #----------------
  
  #Try printing and plotting the model 
  print(summary(SVM.Model));
  #plot(SVM.Model , trainSet  , LTI~age );
  
  #Using the SVM to predict.We are taking away the results in the test set
  Answers =  subset(testSet , select= c(default10yr));
  testSet = subset(testSet ,  select= c(age,LTI)); 
  SVM.Pred = predict(SVM.Model ,testSet );
  
  #BREAK#
  
  final_results = round(as.data.frame(SVM.Pred)$SVM.Pred  , 2) ;
  
  finish = "Omar";
 