 #!/bin/bash
java -jar /home/jaime/Desktop/MYNSGAIIcopy/my-nsgaii/target/my-nsgaii-1.0-SNAPSHOT.jar 
gnuplot -persist <<-EOFMarker
    plot 'PF.dat' using 1:2 with points pt 7 lt 2, './src/main/java/com/mynsgaii/app/results/results' w points pt 6 ps 1 lt -1
EOFMarker
##CAMBIAR ENTRADA DEL PLOT PARA TOMAR LAS VARIABLES DE GEN Y POP
##ABRIR TAMBIEN EL PARETO DE CF6 ./src/main/java/com/mynsgaii/app/results
