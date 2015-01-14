#Check if packages exist otherwise install em
if(!exists('rJava') ){install.packages('rJava', repos="http://cran.r-project.org");}
if(!exists('e1071')){install.packages('e1071', repos="http://cran.r-project.org");}
if(!exists('neuralnet')){install.packages('neuralnet', repos="http://cran.r-project.org");}

quit("yes");