package com.mynsgaii.app;

import java.util.Arrays;
import java.util.Random;

public class ChromosomeZDT3 {
    Float[] genes;
    Float[] gaussValues;
    
    public ChromosomeZDT3(Float[] genes){
        this.genes = genes;
        this.gaussValues = new Float[genes.length];
        for (int i = 0; i < genes.length; i++){
            this.gaussValues[i] = 0.5f;
        }
    }

    public Float[] fitness(Float[] weights){
        Float[] fitness = new Float[3];
        fitness[0] = f1();
        fitness[1] = f2();
        fitness[2] = weights[0] * f1() + weights[1] * f2();
        return fitness;
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

    public boolean isBetterThan(ChromosomeZDT3 chromosome, Float[] weights, boolean debug){
        if (debug) System.out.println("Comparing " + this.toString() + " = " + this.fitness(weights)[2] + " with " + chromosome.toString() + " = " + chromosome.fitness(weights)[2]);
        return this.fitness(weights)[2] < chromosome.fitness(weights)[2];
    }
    
    public String toString(){
        return Arrays.toString(genes);
    }
}
