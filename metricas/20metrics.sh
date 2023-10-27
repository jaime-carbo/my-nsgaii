#!/bin/bash

teacherFolder=/home/jaime/Downloads/suyas/ZDT3TEACHEREVALS/EVAL10000/P200G50

miFolder=/home/jaime/Downloads/mias/ZDT3MISEVALS/EVAL10000/P200G50

previousDirectory=$(dirname "$miFolder")
# Use an array to store the file names
teacherFiles=("$teacherFolder"/*)
miFiles=("$miFolder"/*)

# Loop through the arrays
index=0

# Check if the arrays are of the same length
if [ ${#teacherFiles[@]} -ne ${#miFiles[@]} ]; then
    echo ${#teacherFiles[@]} ${#miFiles[@]} 
    exit 1
fi

while [ $index -lt ${#teacherFiles[@]} ]; do
    #echo "Teacher File: ${teacherFiles[$index]}"
    #echo "MI File: ${miFiles[$index]}"
    if [ $index -eq 0 ]
    then
        echo $previousDirectory/metricas$1.txt
        echo "2 0 2 ${miFiles[$index]} $1 1 ${teacherFiles[$index]} $1 $2 0 2 2" | ./1metricas  | tail -n 6 > $previousDirectory/metricas$1.txt
    else
        echo $previousDirectory/metricas$1.txt
        echo "2 0 2 ${miFiles[$index]} $1 1 ${teacherFiles[$index]} $1 $2 0 2 2" | ./1metricas | tail -n 6 >> $previousDirectory/metricas$1.txt
    fi
    index=$(($index+1))
done

# 2
# 0
# 2
# /home/jaime/Desktop/MYNSGAIIcopy/my-nsgaii/src/main/java/com/mynsgaii/app/results/CF6results-p200g50seed1
# 200
# 1  
# /home/jaime/Desktop/MYNSGAIIcopy/my-nsgaii/src/main/java/com/mynsgaii/app/results/CF6results-p200g50seed5
# 200
# 1
# 0
# 2
# 2