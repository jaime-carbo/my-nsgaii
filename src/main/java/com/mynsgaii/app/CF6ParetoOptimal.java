package com.mynsgaii.app;

import javafx.util.Pair;

public class CF6ParetoOptimal {
    Float[] xValues;
    Float[] yValues;

    public CF6ParetoOptimal(int n){
        this.xValues = xValues(n);
        this.yValues = yValues(n);
    }

    private  Float from0to0point5(Float x){
        return (1 - x) * (1 - x);
    }

    private Float from0point5to0point75(Float x){
        return 0.5f * (1f - x);
    }

    private Float from0point75to1(Float x){
        return 0.25f * (float)Math.sqrt(1 - x);
    }

    private Float[] xValues(int n){
        Float h = 1f/n;
        Float[] xValues = new Float[n];
        for (int i = 0; i < n; i++){
            xValues[i] = h*i;
        }
        return xValues;
    }

    private Float[] yValues(int n){
        Float h = 1f/n;
        Float[] yValues = new Float[n];
        Float xValue;
        for (int i = 0; i < n; i++){
            xValue = h*i;
            if (xValue <= 0.5) yValues[i] = from0to0point5(xValue);
            else if (xValue > 0.5 && xValue <= 0.75) yValues[i] = from0point5to0point75(xValue);
            else yValues[i] = from0point75to1(xValue);
        }
        return yValues;
    }

    public void writeInFile(){
        DataSaving.saveXYValues("CF6ParetoOptimal", xValues, yValues);
    }

}
