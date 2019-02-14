#!/bin/sh
########################
# deploy Semaphore Classifier to local Nifi environment 
########################
echo "Classifier Nifi Build and  Deploy"

sudo service nifi stop
sudo rm -f /opt/nifi-1.7.0/logs/*

cd /home/steve/projects/nificlassifier
mvn -DskipTests clean package
sudo cp /home/steve/projects/nificlassifier/target/semaphore-1.2.0-SNAPSHOT.nar /opt/nifi-1.7.0/lib

sudo service nifi start

