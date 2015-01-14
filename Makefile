#!/bin/sh

#launching R and installing all the required packages
R CMD BATCH packages.R

#printing out
cat packages.Rout

#setting the path to all the required jar files
CLASSPATH="/Library/Frameworks/R.framework/Resources/library/rJava/jri/JRI.jar"
CLASSPATH=$CLASSPATH:"/Library/Frameworks/R.framework/Resources/library/rJava/jri/REngine.jar"
CLASSPATH=$CLASSPATH:"/Library/Frameworks/R.framework/Resources/library/rJava/jri/JRIEngine.jar"
CLASSPATH=$CLASSPATH:"/Library/Frameworks/R.framework/Resources/library/rJava/jri/"
CLASSPATH=$CLASSPATH:"/Library/Frameworks/R.framework/Resources/bin/"

export CLASSPATH
#!/bin/sh

R_HOME=/Library/Frameworks/R.framework/Resources

export R_HOME
R_SHARE_DIR=/Library/Frameworks/R.framework/Resources/share
export R_SHARE_DIR
R_INCLUDE_DIR=/Library/Frameworks/R.framework/Resources/include
export R_INCLUDE_DIR
R_DOC_DIR=/Library/Frameworks/R.framework/Resources/doc
export R_DOC_DIR

JRI_LD_PATH=${R_HOME}/lib:${R_HOME}/bin:
if test -z "$LD_LIBRARY_PATH"; then
  LD_LIBRARY_PATH=$JRI_LD_PATH
else
  LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$JRI_LD_PATH
fi
JAVA=/usr/bin/java


#compiling the program
javac -Djava.library.path=.:${CLASSPATH}  -cp .:$CLASSPATH Main.java MainWindow.java ImagePanel.java Config.java

#running the program GUI
java -Djava.library.path=.:${CLASSPATH}  -cp .:$CLASSPATH MainWindow