package com.mynsgaii.app;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

public class InicializationCF6 {
    //Copy the implementation of Inicialization.java here but change the type of the chromosomes to ChromosomeCF6

    public int generations;
    public int population;
    public int dimensions;
    public Float neighborhoodSize;
    public Float crossoverRate;
    public Float mutationRate;
    public ChromosomeCF6[] chromosomes;//cromosomas de la poblacion
    public Subproblema[] subproblemas;
    public Float tau;
    public int increasedGauss = 0;
    public int decreasedGauss = 0;
    public Float[] referenceZ;
    public Float sigmaShare;
    public Random random;

    public InicializationCF6(int generations, int population,Long randomSeed , int dimensions, Float neighborhoodSize, Float crossoverRate) {
        this.generations = generations;
        this.population = population;
        this.dimensions =  dimensions;
        this.neighborhoodSize = neighborhoodSize;
        this.crossoverRate = crossoverRate;
        this.mutationRate = 1 / (float) dimensions;
        this.sigmaShare = 0.1f;
        this.tau = 1 / (float) Math.sqrt(dimensions);
        this.subproblemas = new Subproblema[population];
        this.random = new Random(randomSeed);
    }

    public ChromosomeCF6 getChromosomeFromSubproblema(Subproblema subproblema){
        return chromosomes[Arrays.asList(subproblemas).indexOf(subproblema)];
    }

    public Subproblema getSubproblemaFromChromosome(ChromosomeCF6 chromosome){
        return subproblemas[Arrays.asList(chromosomes).indexOf(chromosome)];
    }

    public void populate(float min, float max){//Crea tantos cromosomas como poblacion y los inicializa con valores aleatorios
        ChromosomeCF6[] newChromosomes = new ChromosomeCF6[population];
        for (int i = 0; i < population; i++){
            Float[] genes = new Float[dimensions];
            min = 0f;
            max = 1f;   
            for (int j = 0; j < dimensions; j++){
                if (j != 0) {
                    min = -2f;
                    max = 2f;   
                }
                genes[j] = random.nextFloat() * (max - min) + min;
            }
            newChromosomes[i] = new ChromosomeCF6(genes, this);
        }
        chromosomes = newChromosomes;
    }

    public static InicializationCF6 setup(int generations, int population,Long randomSeed , int dimensions, Float neighborhoodSize, Float crossoverRate){
        InicializationCF6 inicialization = new InicializationCF6(generations, population, randomSeed, dimensions, neighborhoodSize, crossoverRate);
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

    public void determineReferenceZ(ChromosomeCF6 extraChromosomeCF6){
        Float[] referenceZ = new Float[2];
        ChromosomeCF6[] allChromosomes = Stream.concat(Arrays.stream(chromosomes), Stream.of(extraChromosomeCF6)).toArray(ChromosomeCF6[]::new);
        Float minf1 = Stream.of(allChromosomes).map(chromosome -> chromosome.f1()).min(Float::compare).get();
        Float minf2 = Stream.of(allChromosomes).map(chromosome -> chromosome.f2()).min(Float::compare).get();
        referenceZ[0] = minf1;
        referenceZ[1] = minf2;
        this.referenceZ = referenceZ;
    }

    public ChromosomeCF6 differentialEvolution(ChromosomeCF6 chromosome, Subproblema subproblema, boolean debug){
        Subproblema[] neighborhood = subproblema.neighborhood;
        int[] threeRandomPicks = new int[3];
        for (int i = 0; i < 3; i++){
            threeRandomPicks[i] = (int) (random.nextFloat() * (neighborhood.length));
        }
        Float[] mutatedGenes = new Float[dimensions];
        Float min = 0f;
        Float max = 1f;
        for (int i = 0; i < dimensions; i++){
            if (i == 0) {
                min = 0f;
                max = 1f;
            } else {
                min = -2f;
                max = 2f;   
            }

            if (random.nextFloat() < crossoverRate){
                mutatedGenes[i] = bounce(chromosomes[threeRandomPicks[0]].genes[i] + 
                        0.5f * (
                            chromosomes[threeRandomPicks[1]].genes[i] -
                            chromosomes[threeRandomPicks[2]].genes[i]),
                            min, max);
            } else {
                mutatedGenes[i] = chromosome.genes[i];
            }
        }
        Float[] crossedGenes = new Float[dimensions];
        for (int i = 0; i < dimensions; i++){
            if (random.nextFloat() < crossoverRate){
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
        return new ChromosomeCF6(crossedGenes, this);
    }

    public ChromosomeCF6 gaussianMutation(ChromosomeCF6 chromosome, boolean debug){
        Float newGauss;
        Float newGene;
        ChromosomeCF6 newChromosomeCF6 = chromosome.copy();
        Float min = 0f;
        Float max = 1f;
        for (int i = 0; i < dimensions; i++){
            if (i == 0) {
                min = 0f;
                max = 1f;
            } else {
                min = -2f;
                max = 2f;   
            }
            if (random.nextFloat() < mutationRate){
                newGauss = chromosome.gaussValues[i] * (float)Math.exp(tau * random.nextGaussian());
                newGene = (float)(chromosome.genes[i] + newGauss * random.nextGaussian());
                newChromosomeCF6.gaussValues[i] = newGauss;
                newChromosomeCF6.genes[i] = bounce(newGene, min, max);
            }
        }
        return newChromosomeCF6;
    }


    public void evolveOnce(boolean debug){
        ChromosomeCF6 newChromosome;
        for (int i = 0; i < population; i++){
            newChromosome = chromosomes[i].copy();
            newChromosome = differentialEvolution(newChromosome, subproblemas[i], debug);
            newChromosome = gaussianMutation(newChromosome, debug);
            /*CHECKING GTE */
            for (int neighborIndex = 0; neighborIndex < subproblemas[i].neighborhood.length; neighborIndex++){
                Subproblema neighbor = subproblemas[i].neighborhood[neighborIndex];
                ChromosomeCF6 neighborChromosome = chromosomes[Arrays.asList(subproblemas).indexOf(neighbor)];
                if (newChromosome.getRestrictions()[2] == 0 && neighborChromosome.getRestrictions()[2] == 0){
                    newChromosome.getGTE(neighborChromosome);
                }
            }
            newChromosome.getGTE(chromosomes[i]);
            }
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

    public static float bounce(float val, float min, float max) {
        if (val < min) {
            return bounce(min + (min - val), min, max);
        }
        else if (val > max) {
            return bounce(max - (val - max), min, max);
        }
        else {
            return val;
        }
    }

    public static void main(String[] args){
        CF6ParetoOptimal pareto = new CF6ParetoOptimal(200);
        pareto.writeInFile();
        // Scanner sc= new Scanner(System.in);
        // System.out.print("Enter number of generations: ");
        // int gens = sc.nextInt(); 
        // System.out.print("Enter population size: ");
        // int population = sc.nextInt();
        // sc.close(); 
        // Inicialization inicialization = Inicialization.setup(gens, population, 30, 1/5f, 0.5f, 0.1f);
        // inicialization.evolve(false);
        // Float[] xValues = new Float[inicialization.population];
        // Float[] yValues = new Float[inicialization.population];
        // for (int i = 0; i < inicialization.population; i++){
        //     xValues[i] = inicialization.chromosomes[i].f1();
        //     yValues[i] = inicialization.chromosomes[i].f2();
        // }
        // System.out.println("Done");
        // DataSaving.saveXYValues("results-"+"p"+inicialization.population+"g"+inicialization.generations, xValues, yValues);

    }
}
