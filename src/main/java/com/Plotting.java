package com;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.Color;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class Plotting extends Application{
    Float[] xValues;
    Float[] yValues;
    String title;
    XYDataset dataset;
    JFreeChart chart;
    XYPlot plot;
    ChartPanel chartPanel;

    public Plotting(Float[] xValues, Float[] yValues, String title){
        this.xValues = xValues;
        this.yValues = yValues;
        this.dataset = createDataset(xValues, yValues);
        this.chart = ChartFactory.createScatterPlot(title, "X_Axis", "Y_Axis", dataset);
        this.plot = (XYPlot) chart.getPlot();
        this.plot.setBackgroundPaint(new Color(255,228,196));  
        this.chartPanel = new ChartPanel(chart);
    }

    public XYDataset createDataset(Float[] xValues, Float[] yValues){
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("Fitness");
        for (int i = 0; i < xValues.length; i++){
            series.add(xValues[i], yValues[i]);
        }
        dataset.addSeries(series);
        return dataset;
    }

    public static void show(Float[] xValues, Float[] yValues, String title) {  
        Plotting example = new Plotting(xValues, yValues, title);
        ChartViewer viewer = new ChartViewer(example.chart);  
        Stage stage = new Stage();
        stage.setScene(new Scene(viewer)); 
        stage.setTitle("JFreeChart: AreaChart"); 
        stage.setWidth(600);
        stage.setHeight(400);
        stage.show(); 
        // SwingUtilities.invokeLater(() -> {  
        //     Plotting example = new Plotting(xValues, yValues, title);
        //     example.setSize(800, 400);  
        //     example.setLocationRelativeTo(null);  
        //     example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);  
        //     example.setVisible(true);  
        // });  
  }

    @Override
    public void start(Stage stage) throws Exception {
        ChartViewer viewer = new ChartViewer(this.chart);  
        stage.setScene(new Scene(viewer)); 
        stage.setTitle("JFreeChart: AreaChart"); 
        stage.setWidth(600);
        stage.setHeight(400);
        stage.show(); 
    }


}
