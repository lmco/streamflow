#!/bin/bash

# Change this value to modify any JAVA_OPTS provided to the Streamflow server
STREAMFLOW_OPTS="-Xms256m -Xmx256m"

SCRIPT="$0"

# Loop through symlinks until the absolute path is determined
while [ -h "$SCRIPT" ] ; do
    ls=`ls -ld "$SCRIPT"`
    # Drop everything prior to ->
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        SCRIPT="$link"
    else
        SCRIPT=`dirname "$SCRIPT"`/"$link"
    fi
done

# Determine STREAMFLOW_HOME based on the location of this script
STREAMFLOW_HOME=`dirname "$SCRIPT"`/..

# Make absolute path to STREAMFLOW_HOME the current directory
STREAMFLOW_HOME=`cd "$STREAMFLOW_HOME"; pwd`

export STREAMFLOW_HOME=$STREAMFLOW_HOME

# Configure Java executable
if [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA="$JAVA_HOME/bin/java"
else
    JAVA=`which java`
fi

if [ ! -x "$JAVA" ]; then
    echo "Could not find any executable java binary. Please install java in your PATH or set JAVA_HOME"
    exit 1
fi

# Print out the environment information for information purposes
echo
echo "Streamflow Server ${project.version}"
echo
echo " JAVA_HOME:        $JAVA_HOME"
echo " JAVA:             $JAVA"
echo " STREAMFLOW_HOME:  $STREAMFLOW_HOME"
echo

# Start the Streamflow application server
$JAVA $JAVA_OPTS $STREAMFLOW_OPTS -cp $STREAMFLOW_HOME/lib -jar \
    $STREAMFLOW_HOME/lib/streamflow-app-jar-${project.version}.jar