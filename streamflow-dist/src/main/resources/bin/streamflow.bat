@echo off

SETLOCAL

rem Change this value to modify any JAVA_OPTS provided to the Streamflow server
set STREAMFLOW_OPTS="-Xms256m -Xmx256g"

if NOT DEFINED JAVA_HOME goto err

set SCRIPT_DIR=%~dp0
for %%I in ("%SCRIPT_DIR%..") 
    do set STREAMFLOW_HOME=%%~dpfI

TITLE Streamflow Server ${project.version}

echo
echo "Streamflow Server ${project.version}"
echo
echo " JAVA_HOME:        %JAVA_HOME%"
echo " JAVA:             %JAVA_HOME%\bin\java"
echo " STREAMFLOW_HOME:  %STREAMFLOW_HOME%"
echo

"%JAVA_HOME%\bin\java" %JAVA_OPTS% %STREAMFLOW_OPTS% -cp %STREAMFLOW_HOME%\lib -jar^
     %STREAMFLOW_HOME%\lib\streamflow-app-jar-${project.version}.jar

goto finally

:err
echo JAVA_HOME environment variable must be set!
pause

:finally

ENDLOCAL