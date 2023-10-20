package com.mynsgaii.app;

import java.util.Arrays;

public class ChromosomeZDT3 {
    Float[] genes;
    Float[] gaussValues;
    Inicialization inicialization;
    
    public ChromosomeZDT3(Float[] genes, Inicialization inicialization){
        this.genes = genes;
        this.inicialization = inicialization;
        this.gaussValues = new Float[genes.length];
        for (int i = 0; i < genes.length; i++){
            this.gaussValues[i] = 0.5f;
        }
    }


    public ChromosomeZDT3 copy(){
        return new ChromosomeZDT3(Arrays.copyOf(this.genes, this.genes.length), this.inicialization);
    }

    public Float[] fitness(Float[] weights){
        Float[] fitness = new Float[3];
        fitness[0] = f1();
        fitness[1] = f2();
        fitness[2] = weights[0] * f1() + weights[1] * f2();
        return fitness;
    }

    public Float fitnessSharing(Subproblema subproblema, ChromosomeZDT3 chromosomeJ){
        Float fSharing = 0f;
        for (int i = 0; i < subproblema.neighborhood.length; i++){
            ChromosomeZDT3 neighboringChromosome = inicialization.getChromosomeFromSubproblema(subproblema.neighborhood[i]);
            if (eucliedanDistance(neighboringChromosome) < this.inicialization.sigmaShare){
                fSharing += 1 - (eucliedanDistance(chromosomeJ)/this.inicialization.sigmaShare);
            }
        }
        return fitness(subproblema.weights)[2] + fitness(subproblema.weights)[2] / (1 + fSharing);
            
    }
    

    public Float f1(){
        return genes[0];
    }

    public Float f2(){
        return gFunction() * hFunction();
    }

    public float gFunction(){
        Float sum = 0f;
        for(int i = 1; i < genes.length; i++) sum+= genes[i];
        return 1 + (9*sum)/(genes.length-1); 
    }

    public Float hFunction(){
        Float element1 = (float) Math.sqrt(f1() / gFunction());
        Float element2 = (float) ((f1()/gFunction())*Math.sin(10 * Math.PI * f1()));
        return 1 - element1 - element2;
    }

    public Float eucliedanDistance(ChromosomeZDT3 chromosome){
        float euclideanDistance = 0;
        for (int i = 0; i < this.genes.length; i++){
            euclideanDistance += (this.genes[i] - chromosome.genes[i]) * (this.genes[i] - chromosome.genes[i]);
        }
        euclideanDistance = (float)Math.sqrt(euclideanDistance);
        return euclideanDistance;
    }

    public boolean isBetterThan(ChromosomeZDT3 chromosome, Subproblema subproblema, boolean debug){
        if (debug) {
            System.out.println("Comparing " + this.toString() + " = " + this.fitness(subproblema.weights)[2] + " with " + chromosome.toString() + " = " + chromosome.fitness(subproblema.weights)[2]);
            System.out.println("Euclidean distance: " + eucliedanDistance(chromosome));
        }
        Float fitnessShareI = fitnessSharing(subproblema, this);
        Float fitnessShareJ = fitnessSharing(subproblema, chromosome);
        // return this.fitness(subproblema.weights)[2] < chromosome.fitness(subproblema.weights)[2];
        return fitnessShareI < fitnessShareJ;
    }
    
    public String toString(){
        return Arrays.toString(genes);
    }
}
