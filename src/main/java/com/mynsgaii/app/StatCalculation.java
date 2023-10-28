package com.mynsgaii.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.scene.shape.Path;


public class StatCalculation {

    public static List<Float[]> readLines(String filename) throws IOException{
        FileInputStream fis=new FileInputStream(filename);       
        Scanner sc=new Scanner(fis);    //file to be scanned  
        List<Float> lines = new ArrayList<>();
        int index = 1;
        while(sc.hasNextLine())  
        {   
            if (index==5 || index== 6 ) lines.add(Float.parseFloat(sc.nextLine().trim().split("=")[1]));
            else lines.add(Float.parseFloat(sc.nextLine().trim().split(":")[1]));
            
            index += index == 6 ? -5 : 1;
        }  
        sc.close();     //closes the scanner 
        //I want to divide every 6 lines and put those in their own array
        List<Float[]> data = new ArrayList<>();
        for (int i = 0; i < lines.size(); i+=6){
            Float[] temp = new Float[6];
            for (int j = 0; j < 6; j++){
                temp[j] = lines.get(i+j);
            }
            data.add(temp);
        }
        // String[][] data = new String[lines.size()/6][6];
        // for (int i = 0; i < 6; i++){
        //     for (int j = 0; j < lines.size()/6; j++){
        //         data[j][i] = lines.get(i + j*6);
        //     }
        // }
        return data;
    }

    public static String[] getStats(List<Float[]> data){
        float[] means = new float[6];
        float[] stds = new float[6];
        for (int i = 0; i < data.size(); i++){
            for (int j = 0; j < 6; j++){
                means[j] += data.get(i)[j];
            }
        }
        for (int i = 0; i < 6; i++){
            means[i] /= data.size();
        }
        for (int i = 0; i < data.size(); i++){
            for (int j = 0; j < 6; j++){
                stds[j] += Math.pow(data.get(i)[j] - means[j], 2);
            }
        }
        for (int i = 0; i < 6; i++){
            stds[i] = (float) Math.sqrt(stds[i]/data.size());
        }
        String[] stats = new String[3];
        stats[0] = "media-HV-mio = " + means[0]  + " | media-HV-NSGAII = " + means[2] + " // desv-estandar-mio = " + stds[0] + " | desv-estandar-NSGAII = " + stds[2];
        stats[1] = "media-spacing-mio = " + means[1]  + " | media-spacing-NSGAII = " + means[3] + " // desv-estandar-mio = " + stds[1] + " | desv-estandar-NSGAII = " + stds[3];
        stats[2] = "media C(NSGAII,mio) " + means[4] + " | media-C(mio,NSGAII) " + means[5] + " // desv-estandar C(NSGAII,mio) " + stds[4] + " | desv-estandar C(mio,NSGAII) " + stds[5];
        return stats;
    }

    public static void main(String[] args) throws IOException{
        List<Float[]> data = readLines("my-nsgaii/metricas/comparaciones/10000zdt3metricas200.txt" );
        //print out the data
        String[] res = getStats(data);
        for (int i = 0; i < res.length; i++){
            System.out.println(res[i]);
        }
    }
}



