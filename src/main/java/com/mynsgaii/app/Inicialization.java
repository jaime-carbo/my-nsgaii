package com.mynsgaii.app;

import java.util.stream.Stream;
import java.util.*;

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
        this.mutationRate = 2 / (float) dimensions;
        this.sigmaShare = sigmaShare;
        this.tau = 1 / (float) Math.sqrt(dimensions);
        this.subproblemas = new Subproblema[population];
    }

    public ChromosomeZDT3 getChromosomeFromSubproblema(Subproblema subproblema){
        return chromosomes[Arrays.asList(subproblemas).indexOf(subproblema)];
    }

    public Subproblema getSubproblemaFromChromosome(ChromosomeZDT3 chromosome){
        return subproblemas[Arrays.asList(chromosomes).indexOf(chromosome)];
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

    public void determineReferenceZ(ChromosomeZDT3 extraChromosomeZDT3){
        Float[] referenceZ = new Float[2];
        ChromosomeZDT3[] allChromosomes = Stream.concat(Arrays.stream(chromosomes), Stream.of(extraChromosomeZDT3)).toArray(ChromosomeZDT3[]::new);
        Float minf1 = Stream.of(allChromosomes).map(chromosome -> chromosome.f1()).min(Float::compare).get();
        Float minf2 = Stream.of(allChromosomes).map(chromosome -> chromosome.f2()).min(Float::compare).get();
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
            if (Math.random() < crossoverRate){
            mutatedGenes[i] = bounce(chromosomes[threeRandomPicks[0]].genes[i] + 
                    0.5f * (
                        chromosomes[threeRandomPicks[1]].genes[i] -
                        chromosomes[threeRandomPicks[2]].genes[i]),
                        0, 1);
            } else {
                mutatedGenes[i] = chromosome.genes[i];
            }
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
            if (Math.random() < mutationRate){
                newGauss = chromosome.gaussValues[i] * (float)Math.exp(tau * random.nextGaussian());
                newGene = (float)(chromosome.genes[i] + newGauss * random.nextGaussian());
                newChromosomeZDT3.gaussValues[i] = newGauss;
                newChromosomeZDT3.genes[i] = bounce(newGene, 0, 1);
            }
        }
        return newChromosomeZDT3;
    }

    // public void checkNeighbors(ChromosomeZDT3 chromosome, Subproblema subproblema){
    //     Subproblema iSubproblema;
    //     int index;
    //     for (int i = 0; i < subproblema.neighborhood.length; i++){
    //         iSubproblema = subproblema.neighborhood[i];
    //         index = Arrays.asList(subproblemas).indexOf(subproblema.neighborhood[i]);
    //         if (chromosome.isBetterThan(chromosomes[index], iSubproblema, false)){
    //             chromosomes[index] = chromosome;
    //         }
    //     }
    // }

    // public void tournamentSelection(ChromosomeZDT3[] completeGeneration){
    //     ChromosomeZDT3 chromosomeI;
    //     ChromosomeZDT3 chromosomeJ;
    //     for (int i = 0; i < population; i++){
    //         chromosomeI = completeGeneration[(int)(Math.random() * (completeGeneration.length))];
    //         chromosomeJ = completeGeneration[(int)(Math.random() * (completeGeneration.length))];
    //         if (chromosomeI.isBetterThan(chromosomeJ, subproblemas[i], false)){
    //             chromosomes[i] = chromosomeI;
    //         } else {
    //             chromosomes[i] = chromosomeJ;
    //         }
    //     }
    // }

    public void evolveOnce(boolean debug){
        ChromosomeZDT3 newChromosome;
        for (int i = 0; i < population; i++){
            newChromosome = chromosomes[i].copy();

            /*DIFFERENTIAL CROSSOVER */
            // if (Math.random() > this.crossoverRate){
            //     newChromosome = differentialEvolution(newChromosome, subproblemas[i], debug);
            // } 
            newChromosome = differentialEvolution(newChromosome, subproblemas[i], debug);

            /*GAUSS MUTATION */
            // if (Math.random() <= this.mutationRate){
            //     newChromosome = gaussianMutation(newChromosome, debug);
            // }
            newChromosome = gaussianMutation(newChromosome, debug);

            /*CHECKING GTE */
            for (int neighborIndex = 0; neighborIndex < subproblemas[i].neighborhood.length; neighborIndex++){
                Subproblema neighbor = subproblemas[i].neighborhood[neighborIndex];
                ChromosomeZDT3 neighborChromosome = chromosomes[Arrays.asList(subproblemas).indexOf(neighbor)];
                newChromosome.getGTE(neighborChromosome);
            }
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
        int whichAlg = Integer.parseInt(args[0]);
        int population = Integer.parseInt(args[1]);
        int gens = Integer.parseInt(args[2]);
        // int whichAlg = 0;
        // int population = 100;
        // int gens = 100;
        if (whichAlg == 0){
            System.out.println("CF6");
            InicializationCF6 inicialization = InicializationCF6.setup(gens, population, 4, 1/3f, 0.5f, 0.1f);
            inicialization.evolve(false);   
            String fileName = "CF6results-p"+inicialization.population+"g"+inicialization.generations;
            Float[] xValues = new Float[inicialization.population];
            Float[] yValues = new Float[inicialization.population];
            Float[] restriction1 = new Float[inicialization.population];
            Float[] restriction2 = new Float[inicialization.population];
            for (int i = 0; i < inicialization.population; i++){
                xValues[i] = inicialization.chromosomes[i].f1();
                yValues[i] = inicialization.chromosomes[i].f2();
                restriction1[i] = inicialization.chromosomes[i].getRestrictions()[0];
                restriction2[i] = inicialization.chromosomes[i].getRestrictions()[1];
            }
            System.out.println("Done");
            DataSaving.saveXYValues(fileName, xValues, yValues);
            DataSaving.saveWXYZ(fileName+"withRestrictions", xValues, yValues, restriction1, restriction2);
        } else {
            System.out.println("ZDT3");
            Inicialization inicialization = Inicialization.setup(gens, population, 30, 1/3f, 0.5f, 0.1f);
            inicialization.evolve(false);
            String fileName = "ZDT3results-p"+inicialization.population+"g"+inicialization.generations;
            Float[] xValues = new Float[inicialization.population];
            Float[] yValues = new Float[inicialization.population];
            for (int i = 0; i < inicialization.population; i++){
                xValues[i] = inicialization.chromosomes[i].f1();
                yValues[i] = inicialization.chromosomes[i].f2();
            }
            System.out.println("Done");
            DataSaving.saveXYValues(fileName, xValues, yValues);
        
        }
        
    }
}

