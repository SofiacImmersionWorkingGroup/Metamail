skipTests=
execJava=false

if [ $# -eq 1 ]; then
  if [ $1 -eq "-skip" ];then
    skipTests="-Dmaven.test.skip=true"
  elif [ $1 -eq "-x" ];then
    execJava=true
  fi
elif [ $# -eq 2 ]; then
  skipTests="-Dmaven.test.skip=true"
  execJava=true
fi

mvn clean install $skipTests

if [ $execJava ];then
  mvn exec:java -Dexec.mainClass="com.igalia.enron_importer.thread.Start"
fi
