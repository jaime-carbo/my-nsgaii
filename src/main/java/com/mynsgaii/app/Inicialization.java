package com.mynsgaii.app;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

public class Inicialization {
    public int generations;
    public int population;
    public int dimensions;
    public Float neighborhoodSize;
    public Float crossoverRate;
    public Float mutationRate;
    public ChromosomeZDT3[] chromosomes;//cromosomas de la poblacion
    public Subproblema[] subproblemas;
    public Float tau;
    public int increasedGauss = 0;
    public int decreasedGauss = 0;

    public Float[] referenceZ;
    public Float sigmaShare;

    public Inicialization(int generations, int population, int dimensions, Float neighborhoodSize, Float crossoverRate, Float sigmaShare) {
        this.generations = generations;
        this.population = population;
        this.dimensions =  dimensions;
        this.neighborhoodSize = neighborhoodSize;
        this.crossoverRate = crossoverRate;
        this.mutationRate = 0.5f;
        this.sigmaShare = sigmaShare;
        this.tau = 1 / (float) Math.sqrt(dimensions);
        this.subproblemas = new Subproblema[population];
    }

    public ChromosomeZDT3 getChromosomeFromSubproblema(Subproblema subproblema){
        return chromosomes[Arrays.asList(subproblemas).indexOf(subproblema)];
    }

    public void populate(float min, float max){//Crea tantos cromosomas como poblacion y los inicializa con valores aleatorios
        ChromosomeZDT3[] newChromosomes = new ChromosomeZDT3[population];
        for (int i = 0; i < population; i++){
            Float[] genes = new Float[dimensions];
            for (int j = 0; j < dimensions; j++){
                genes[j] = (float) Math.random() * (max - min) + min;
            }
            newChromosomes[i] = new ChromosomeZDT3(genes, this);
        }
        chromosomes = newChromosomes;
    }

    public static Inicialization setup(int generations, int population, int dimensions, Float neighborhoodSize, Float crossoverRate, Float sigmaShare){
        Inicialization inicialization = new Inicialization(generations, population, dimensions, neighborhoodSize, crossoverRate, sigmaShare);
        Float jump = 1 / (((float)population) - 1);
        Float counter = 0f;
        for (int i = 0; i < population; i++){
            inicialization.subproblemas[i] = new Subproblema(new Float[] {
                clamp(counter, 0, 1), 
                clamp(1-counter, 0, 1)
            });
            counter += jump;
        }
        //Una vez estan todos los subproblemas creados, se les asignan sus vecinos
        for (int i = 0; i < population; i++){
            inicialization.subproblemas[i].setNeighbors(inicialization.subproblemas, neighborhoodSize);
        }
        inicialization.populate(0, 1);
        return inicialization;
    }

    public void determineReferenceZ(){
        Float[] referenceZ = new Float[2];
        Float minf1 = Stream.of(chromosomes).map(chromosome -> chromosome.f1()).min(Float::compare).get();
        Float minf2 = Stream.of(chromosomes).map(chromosome -> chromosome.f2()).min(Float::compare).get();
        referenceZ[0] = minf1;
        referenceZ[1] = minf2;
        this.referenceZ = referenceZ;
    }

    public ChromosomeZDT3 differentialEvolution(ChromosomeZDT3 chromosome, Subproblema subproblema, boolean debug){
        Subproblema[] neighborhood = subproblema.neighborhood;
        int[] threeRandomPicks = new int[3];
        for (int i = 0; i < 3; i++){
            threeRandomPicks[i] = (int) (Math.random() * (neighborhood.length));
        }
        Float[] mutatedGenes = new Float[dimensions];
        for (int i = 0; i < dimensions; i++){
            mutatedGenes[i] = clamp(chromosomes[threeRandomPicks[0]].genes[i] + 
                    0.5f * (
                        chromosomes[threeRandomPicks[1]].genes[i] -
                        chromosomes[threeRandomPicks[2]].genes[i]),
                        0, 1);
        }
        Float[] crossedGenes = new Float[dimensions];
        for (int i = 0; i < dimensions; i++){
            if (Math.random() < crossoverRate){
                crossedGenes[i] = mutatedGenes[i];
            } else {
                crossedGenes[i] = chromosome.genes[i];
            }
        }
        //DEBUG
        if (debug) System.out.println("Three random picks: " + Arrays.toString(threeRandomPicks));
        if (debug) System.out.println("Mutated genes: " + Arrays.toString(mutatedGenes));
        if (debug) System.out.println("Crossed genes: " + Arrays.toString(crossedGenes));
        if (debug) System.out.println("Origin  genes: " + Arrays.toString(chromosome.genes));
        return new ChromosomeZDT3(crossedGenes, this);
    }

    public ChromosomeZDT3 gaussianMutation(ChromosomeZDT3 chromosome, boolean debug){
        Float newGauss;
        Float newGene;
        Random random = new Random();
        ChromosomeZDT3 newChromosomeZDT3 = chromosome.copy();
        for (int i = 0; i < dimensions; i++){
            newGauss = chromosome.gaussValues[i] * (float)Math.exp(tau * random.nextGaussian());
            newGene = (float)(chromosome.genes[i] + newGauss * random.nextGaussian());
            newChromosomeZDT3.gaussValues[i] = newGauss;
            newChromosomeZDT3.genes[i] = clamp(newGene, 0, 1);
        }
        return newChromosomeZDT3;
    }

    public void checkNeighbors(ChromosomeZDT3 chromosome, Subproblema subproblema){
        Subproblema iSubproblema;
        int index;
        for (int i = 0; i < subproblema.neighborhood.length; i++){
            iSubproblema = subproblema.neighborhood[i];
            index = Arrays.asList(subproblemas).indexOf(subproblema.neighborhood[i]);
            if (chromosome.isBetterThan(chromosomes[index], iSubproblema, false)){
                chromosomes[index] = chromosome;
            }
        }
    }

    public void evolveOnce(boolean debug){
        ChromosomeZDT3 newChromosome;
        ChromosomeZDT3[] children = new ChromosomeZDT3[population];
        int totalMutations = 0;
        int positiveMutations = 0;
        for (int i = 0; i < population; i++){
            newChromosome = chromosomes[i].copy();
            if (Math.random() > this.crossoverRate){
                newChromosome = differentialEvolution(newChromosome, subproblemas[i], debug);
                if (newChromosome.isBetterThan(chromosomes[i], subproblemas[i], debug)){
                    if (debug) System.out.println("New chromosome:" + newChromosome.toString() + " is better than " + chromosomes[i].toString());
                    chromosomes[i] = newChromosome;
                }
            } 
            if (Math.random() <= this.mutationRate){
                newChromosome = gaussianMutation(newChromosome, debug);
                totalMutations++;
                if (newChromosome.isBetterThan(chromosomes[i], subproblemas[i], debug)){
                    positiveMutations++;
                }

            }
            children[i] = newChromosome;
            ChromosomeZDT3[] completeGeneration = Stream.concat(Arrays.stream(chromosomes), Arrays.stream(children)).toArray(ChromosomeZDT3[]::new);
            //  checkNeighbors(newChromosome, subproblemas[i]);
        }
        if (totalMutations > 0){
            if (positiveMutations/totalMutations < 0.2){
                for (int i = 0; i < population; i++){
                    for (int j = 0; j < dimensions; j++){
                        chromosomes[i].gaussValues[j] = chromosomes[i].gaussValues[j] * 0.8f;
                    }
                    decreasedGauss++;
                }
            } else {
                for (int i = 0; i < population; i++){
                    for (int j = 0; j < dimensions; j++){
                        chromosomes[i].gaussValues[j] = chromosomes[i].gaussValues[j] * 1.2f;
                    }
                    increasedGauss++;
                }
            }
        }

        if (debug) System.out.println("Total mutations: " + totalMutations + " Positive mutations: " + positiveMutations);
    }

    public void evolve(boolean debug){
        for (int i = 0; i < generations; i++){
            evolveOnce(debug);
        }
    }

    public String toString(){
        String str = "";
        for (int i = 0; i < population; i++){
            str += "[ " + subproblemas[i].weights[0] + "," + subproblemas[i].weights[1] + " ]\n";
        }
        return str;
    }

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    public static void main(String[] args){
        Inicialization inicialization = Inicialization.setup(100, 100, 30, 0.3f, 0.5f, 0.1f);
        inicialization.determineReferenceZ();
        inicialization.evolve(false);
        Float[] xValues = new Float[inicialization.population];
        Float[] yValues = new Float[inicialization.population];
        for (int i = 0; i < inicialization.population; i++){
            xValues[i] = inicialization.chromosomes[i].f1();
            yValues[i] = inicialization.chromosomes[i].f2();
        }

        DataSaving.saveXYValues("results", xValues, yValues);

    }
}

