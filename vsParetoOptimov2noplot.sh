#!/bin/bash
if [ "$1" == 0 ]
then 
	java -jar /home/jaime/Desktop/MYNSGAIIcopy/my-nsgaii/target/my-nsgaii-1.0-SNAPSHOT.jar $1 $2 $3 $4 $5 $6
else
	java -jar /home/jaime/Desktop/MYNSGAIIcopy/my-nsgaii/target/my-nsgaii-1.0-SNAPSHOT.jar $1 $2 $3 $4 $5 $6
fi
##CAMBIAR ENTRADA DEL PLOT PARA TOMAR LAS VARIABLES DE GEN Y POP
##ABRIR TAMBIEN EL PARETO DE CF6 ./src/main/java/com/mynsgaii/app/results/CF6ParetoOptimal
##"results-p"+inicialization.population+"g"+inicialization.generations

#./vsParetoOptimov2.sh 1 100 100 4 30 2  -> ZDT3 bueno
# ./vsParetoOptimov2.sh 0 100 100 4 4 4 -> CF6 4 dim bueno
# ./vsParetoOptimov2.sh 0 100 100 4 16 5 -> CF6 16 dim bueno