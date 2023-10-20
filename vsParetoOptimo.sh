#!/bin/bash
gnuplot -persist <<-EOFMarker
    plot 'PF.dat' using 1:2 with points pt 7 lt 2, './src/main/java/com/mynsgaii/app/results/results' w points pt 6 ps 1 lt -1
EOFMarker
