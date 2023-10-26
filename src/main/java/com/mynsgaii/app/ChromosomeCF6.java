package com.mynsgaii.app;

import java.util.Arrays;

public class ChromosomeCF6 {
    Float[] genes;
    Float[] gaussValues;
    InicializationCF6 inicialization;
    Float[] unweightedFitness;
    Float[] restrictions;

    public ChromosomeCF6(Float[] genes, InicializationCF6 inicialization) {
        this.genes = genes;
        for (int i = 1; i < genes.length; i++) {
            this.genes[i] = this.genes[i] * 4 - 2;
        }
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

    public ChromosomeCF6 copy(){
        return new ChromosomeCF6(Arrays.copyOf(this.genes, this.genes.length), this.inicialization);
    }

    public Float[] fitness(Float[] weights){
        Float[] allFitness = new Float[3];
        allFitness[0] = unweightedFitness[0];
        allFitness[1] = unweightedFitness[1];
        allFitness[2] = unweightedFitness[0] * weights[0] + unweightedFitness[1] * weights[1];
        return allFitness;
    }

    public Float y(int j){
        int jAjustada = j + 1;
        if (jAjustada % 2 == 0 && jAjustada>= 2 && jAjustada <= this.genes.length){
            return (float)(this.genes[j] - 0.8 * this.genes[0] * Math.cos(6 * Math.PI * this.genes[0] + jAjustada * Math.PI / this.genes.length)); 
        }
        else if (jAjustada % 2 != 0 && jAjustada>= 2 && jAjustada <= this.genes.length){
            return (float)(this.genes[j] - 0.8 * this.genes[0] * Math.sin(6 * Math.PI * this.genes[0] + jAjustada * Math.PI / this.genes.length)); 
        }
        else{
            throw new IllegalArgumentException("j must be greater than 0 and less than the number of genes");
        }
    }

    public Float f1(){
        Float sum = 0f;
        for (int j = 1; j < this.genes.length; j++){
            sum += y(j) * y(j);
        }
        return this.genes[0] + sum;
    }

    public Float f2(){
        Float sum = 0f;
        for (int j = 1; j < this.genes.length; j++){
            sum += y(j) * y(j);
        }
        return (1 - this.genes[0]) * (1 - this.genes[0]) + sum;
    }

    public Float eucliedanDistance(ChromosomeCF6 chromosome){
        float euclideanDistance = 0;
        for (int i = 0; i < this.genes.length; i++){
            euclideanDistance += (this.genes[i] - chromosome.genes[i]) * (this.genes[i] - chromosome.genes[i]);
        }
        euclideanDistance = (float)Math.sqrt(euclideanDistance);
        return euclideanDistance;
    }

    public int sign(Float number){
        if (number > 0) return 1;
        else if (number < 0) return -1;
        else return 0;
    }

    public Float[] getRestrictions(){
        if (this.restrictions != null) return this.restrictions;
        Float restriction1 = (float) (genes[1] - 0.8f *
                genes[0] * Math.sin(6 * Math.PI * genes[0] + 2 * Math.PI / genes.length) -
                sign(0.5f * (1-genes[0]) - (1-genes[0]) * (1-genes[0])) *
                Math.sqrt(Math.abs(0.5f * (1-genes[0]) - (1-genes[0]) * (1-genes[0]))));
        Float restriction2 = (float) (genes[3] - 0.8f *
                genes[0] * Math.sin(6 * Math.PI * genes[0] + 4 * Math.PI / genes.length) -
                sign(0.25f * (float)Math.sqrt(1-genes[0]) - 0.5f * (1-genes[0])) *
                Math.sqrt(Math.abs(0.25*Math.sqrt(1 - genes[0]) - 0.5 * (1 - genes[0]))));
        this.restrictions = new Float[] {restriction1, restriction2};
        return new Float[] {restriction1, restriction2};
    }

    public String toString(){
        return Arrays.toString(genes);
    }

    void getGTE(ChromosomeCF6 neighbor){
        Subproblema subproblema = this.inicialization.getSubproblemaFromChromosome(neighbor);
        ChromosomeCF6 child = this;
        Float childRestrictions = child.getRestrictions()[0] + child.getRestrictions()[1];
        Float neighborRestrictions = neighbor.getRestrictions()[0] + neighbor.getRestrictions()[1];
        int neighborIndex = Arrays.asList(this.inicialization.chromosomes).indexOf(neighbor);
        if (childRestrictions > neighborRestrictions){
            this.inicialization.chromosomes[neighborIndex] = child;
            return;
        }
        else if (childRestrictions < neighborRestrictions){
            return;
        } else {
            Float referenceZI = this.inicialization.referenceZ[0];
            Float referenceZJ = this.inicialization.referenceZ[1];
            Float neighborGTE = Math.max(subproblema.weights[0] * (neighbor.f1() - referenceZI), subproblema.weights[1] * (neighbor.f2() - referenceZJ));
            Float childGTE = Math.max(subproblema.weights[0] * (child.f1() - referenceZI), subproblema.weights[1] * (child.f2() - referenceZJ));
            if (childGTE <= neighborGTE){
                this.inicialization.chromosomes[neighborIndex] = child;
            }
        }

            
    }
    

    //Los chromosomas tienen el primer gen entre 0 y 1 y los demas entre -2 y 2
    //Tengo que crear una funcion de restriccione
    //Antes de calcular tchebichef compriuebo si el nuevo individuo tiene un mayor valor de restricciones que el viejo, en cuyo caso lo sustituye sin hacer tchebichef
 
    

}
