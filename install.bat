@ECHO OFF
::Setting up R paths
set PATH=%PATH%;"C:\Program Files\R\R-3.1.0\bin\x64\"
set PATH=%PATH%;"C:\Program Files\R\R-3.1.0\bin\"

set CLASSPATH=.
set CLASSPATH=%CLASSPATH%;"C:\Program Files\R\R-*\library\rJava\jri\REngine.jar"
set CLASSPATH=%CLASSPATH%;"C:\Program Files\R\R-*\library\rJava\jri\JRI.jar"
set CLASSPATH=%CLASSPATH%;"C:\Program Files\R\R-*\library\rJava\jri\JRIEngine.jar"
set CLASSPATH=%CLASSPATH%;"C:\Program Files\R\R-*\library\rJava\jri\x64\*"
set CLASSPATH=%CLASSPATH%;"C:\Program Files\R\R-*\library\rJava\jri\i386\*"
set CLASSPATH=%CLASSPATH%;"C:\Program Files\R\R-3.1.0\library\rJava\jri\*"
set CLASSPATH=%CLASSPATH%;"C:\Program Files\R\R-*\library\rJava\jri\*"


::Compiling the java Program
javac -cp %CLASSPATH% Main.java MainWindow.java Config.java ImagePanel.java

::Running the Program
java -cp %CLASSPATH% Main "ANN"