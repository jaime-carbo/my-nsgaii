package com.mynsgaii.app;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Subproblema {
    public Float[] weights = new Float[2];
    public Subproblema[] neighborhood;
    public Float[] euclideans;

    public Subproblema(Float[] weight){
        this.weights = weight;
    }

    public Map<Subproblema, Float> setNeighbors(Subproblema[] neighbors, float neighborhoodSize){
        int nNeighbors = ((int)Math.ceil(neighbors.length*neighborhoodSize));
        Float[] euclideans = new Float[neighbors.length];

        for (int i = 0; i < neighbors.length; i++){
            Subproblema neighbor = neighbors[i];
            euclideans[i] = ((float)Math.sqrt(
                (neighbor.weights[0] - this.weights[0]) * (neighbor.weights[0] - this.weights[0]) + 
                (neighbor.weights[1] - this.weights[1]) * (neighbor.weights[1] - this.weights[1])
                ));
        }

        Map<Subproblema, Float> euclideansMap = Arrays.asList(neighbors).stream().collect(Collectors.toMap(
        neighbor -> neighbor,        
        neighbor -> euclideans[Arrays.asList(neighbors).indexOf(neighbor)]              
        ));

        Subproblema[] neighborhood = euclideansMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .toArray(Subproblema[]::new);
        
        this.neighborhood = Arrays.copyOfRange(neighborhood,0, nNeighbors);
        return euclideansMap;
    }

    public void setNeighbors(Subproblema[] neighbors, float neighborhoodSize, boolean debug){
        Float[] euclideans = setNeighbors(neighbors, neighborhoodSize).entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getValue)
                .toArray(Float[]::new);
        this.euclideans = Arrays.copyOfRange(euclideans,0, ((int)Math.ceil(neighbors.length*neighborhoodSize)));
    }

    public String toStringSimple(){
        return Arrays.toString(weights);
    }

    public String toString(){
        String neighborhoodString = "";
        for (Subproblema neighbor : this.neighborhood){
            neighborhoodString += neighbor.toStringSimple() + ": ";
            neighborhoodString +=  euclideans != null? euclideans[Arrays.asList(this.neighborhood).indexOf(neighbor)] + ", " : "null, ";
        }
        return "Subproblema: " + Arrays.toString(weights) + " Neighborhood { " + neighborhoodString + " }";
    }
}
