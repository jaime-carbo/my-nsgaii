package com.mynsgaii.app;

import java.util.Arrays;

public class ChromosomeZDT3 {
    Float[] genes;
    Float[] gaussValues;
    Inicialization inicialization;
    Float[] unweightedFitness;
    
    public ChromosomeZDT3(Float[] genes, Inicialization inicialization){
        this.genes = genes;
        this.inicialization = inicialization;
        this.gaussValues = new Float[genes.length];
        for (int i = 0; i < genes.length; i++){
            this.gaussValues[i] = 0.5f;
        }
        Float[] fitness = new Float[2];
        fitness[0] = f1();
        fitness[1] = f2();
        this.unweightedFitness = fitness;
    }

    void getGTE(ChromosomeZDT3 neighbor){

        Subproblema subproblema = this.inicialization.getSubproblemaFromChromosome(neighbor);
        ChromosomeZDT3 child = this;

        inicialization.determineReferenceZ(child);

        Float referenceZI = inicialization.referenceZ[0];
        Float referenceZJ = inicialization.referenceZ[1];

        Float neighborGTE = Math.max(subproblema.weights[0] * (neighbor.f1() - referenceZI), subproblema.weights[1] * (neighbor.f2() - referenceZJ));
        //neighborGTE -= neighbor.fitnessSharing(subproblema);
        Float childGTE = Math.max(subproblema.weights[0] * (child.f1() - referenceZI), subproblema.weights[1] * (child.f2() - referenceZJ));
        //childGTE -= child.fitnessSharing(subproblema);
        Float beta = 0.005f;
        Float childWithFS = child.gteWithFitnessShare(childGTE, subproblema, beta);
        Float neighborWithFS = neighbor.gteWithFitnessShare(neighborGTE, subproblema, beta);
        
        if (childWithFS <= neighborWithFS){
            int neighborIndex = Arrays.asList(this.inicialization.chromosomes).indexOf(neighbor);
            this.inicialization.chromosomes[neighborIndex] = child;
        }
        
    }


    public ChromosomeZDT3 copy(){
        return new ChromosomeZDT3(Arrays.copyOf(this.genes, this.genes.length), this.inicialization);
    }

    public Float[] fitness(Float[] weights){
        Float[] allFitness = new Float[3];
        allFitness[0] = unweightedFitness[0];
        allFitness[1] = unweightedFitness[1];
        allFitness[2] = unweightedFitness[0] * weights[0] + unweightedFitness[1] * weights[1];
        return allFitness;
    }

    public Float fitnessSharing(Subproblema subproblema){
        Float fSharing = 0f;
        for (int i = 0; i < subproblema.neighborhood.length; i++){
            ChromosomeZDT3 neighboringChromosome = inicialization.getChromosomeFromSubproblema(subproblema.neighborhood[i]);
            if (eucliedanDistance(neighboringChromosome) < this.inicialization.sigmaShare){
                fSharing += (eucliedanDistance(neighboringChromosome));
            }
        }
        return fSharing;
            
    }

    public Float gteWithFitnessShare(Float tchebycheff,Subproblema subproblema,  Float beta){
        return  beta * this.fitnessSharing(subproblema) + (1 - beta) * tchebycheff;
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
        Float fitnessShareI = fitnessSharing(subproblema);
        Float fitnessShareJ = fitnessSharing(subproblema);
        // return this.fitness(subproblema.weights)[2] < chromosome.fitness(subproblema.weights)[2];
        return fitnessShareI < fitnessShareJ;
    }
    
    public String toString(){
        return Arrays.toString(genes);
    }
}
