package com.mynsgaii.app;

import java.io.File;
import java.io.FileWriter;

public class DataSaving {

    public static void saveXYValues(String fileName, Float[] xValues, Float[] yValues){
        try {
            File directory = new File("/home/jaime/Desktop/MYNSGAIIcopy/my-nsgaii/src/main/java/com/mynsgaii/app/results");
            File file = new File(directory, fileName);
            if (file.createNewFile()) System.out.println("created");;
            FileWriter writer = new FileWriter(file);
            for (int i = 0; i < xValues.length; i++){
                writer.write(xValues[i] + "\t" + yValues[i] + "\n");
            }
            writer.close();
        } catch (Exception e){
            System.out.println("Error saving data");
            e.printStackTrace();
        }
    }

    
}
