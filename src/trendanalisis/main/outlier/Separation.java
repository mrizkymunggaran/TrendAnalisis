/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trendanalisis.main.outlier;

import java.util.ArrayList;
import org.apache.commons.math.stat.StatUtils;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

/**
 *
 * @author asus
 */
public class Separation {

    private double stdv = 0;
    private double mean = 0;
    private double maxThreshold = 0;
    private double minThershold = 0;
    private boolean outlier = false;
    private ArrayList<Integer> lOutlierId;
    private double[] separations;

    public void Build(Instances instances) {
        separations = new double[instances.size()];
        for (int i = 0; i < instances.size(); i++) {
            separations[i] = Average(instances.get(i), instances);
        }

    }
    
    /*
     * koofisien variasi data  (coefficient of variation)
     * Koefisien Keragaman merupakan ukuran yang bebas satuan dan selalu dinyatakan dalam bentuk persentase.
     * Nilai KK yang kecil menunjukkan bahwa data tidak terlalu beragam dan di katakan lebih konsisten. 
     * KK tidak dapat diandalkan apabila nilai rata-rata hampir sama dengan 0 (nol). 
     * KK juga tidak stabil apabila skala pengukuran data yang digunakan bukan skala rasio.
     */
    
    public  double Varinces(Instances instances){
    
   separations = new double[instances.size()];
    for (int i = 0; i < instances.numInstances(); i++) {
                    double sum = 0;
                    for (int j = 0; j < instances.instance(i).toDoubleArray().length; j++) {
                        sum += Math.pow(instances.instance(i).toDoubleArray()[j], 2);
                    }

                    separations[i] = Math.sqrt(sum);
                }
   double mean= Utils.mean(separations);
    return (Math.sqrt(Utils.variance(separations))/mean)*100;
    }

    private double Average(Instance instance, Instances instances) {
        //System.out.println("WB-Index (Min)     \t:" + idInCluster.size());//min
        EuclideanDistance ecludiean = new EuclideanDistance(instances);
        double a = 0.0f;

        for (int i = 0; i < instances.size(); i++) {
            //  System.out.println(idInCluster.get(i));//min
           
            a += ecludiean.distance(instance, instances.get((i)));
        }

        return a/instances.size() ;
    }

    public void FindOutlier() {
        //System.out.println("WB-Index (Min)     \t:" + idInCluster.size());//min
        stdv = Math.sqrt(StatUtils.variance(separations));
        mean = StatUtils.mean(separations);
        maxThreshold = mean + (2 * stdv);
        minThershold = mean - (2 * stdv);
        
        double a = 0;
        lOutlierId = new ArrayList<Integer>();
        int []id= Utils.sort(separations);
       
        
        for (int i = 0; i < separations.length; i++) {
        //    System.out.println(id[i] +"--  "+separations[id[i]] );
            if (separations[id[i]] > maxThreshold || separations[id[i]] < minThershold) {
                lOutlierId.add(id[i]);
                
                outlier = true;

            }

        }

    }

    /**
     * @return the stdv
     */
    public double getStdv() {
        return stdv;
    }

    /**
     * @param stdv the stdv to set
     */
    public void setStdv(double stdv) {
        this.stdv = stdv;
    }

    /**
     * @return the mean
     */
    public double getMean() {
        return mean;
    }

    /**
     * @param mean the mean to set
     */
    public void setMean(double mean) {
        this.mean = mean;
    }

    /**
     * @return the maxThreshold
     */
    public double getMaxThreshold() {
        return maxThreshold;
    }

    /**
     * @param maxThreshold the maxThreshold to set
     */
    public void setMaxThreshold(double maxThreshold) {
        this.maxThreshold = maxThreshold;
    }

    /**
     * @return the minThershold
     */
    public double getMinThershold() {
        return minThershold;
    }

    /**
     * @param minThershold the minThershold to set
     */
    public void setMinThershold(double minThershold) {
        this.minThershold = minThershold;
    }

    /**
     * @return the outlier
     */
    public boolean isOutlier() {
        return outlier;
    }

    /**
     * @param outlier the outlier to set
     */
    public void setOutlier(boolean outlier) {
        this.outlier = outlier;
    }

    /**
     * @return the lOutlierId
     */
    public ArrayList<Integer> getlOutlierId() {
        return lOutlierId;
    }

    /**
     * @param lOutlierId the lOutlierId to set
     */
    public void setlOutlierId(ArrayList<Integer> lOutlierId) {
        this.lOutlierId = lOutlierId;
    }

    /**
     * @return the separations
     */
    public double[] getSeparations() {
        return separations;
    }

    /**
     * @param separations the separations to set
     */
    public void setSeparations(double[] separations) {
        this.separations = separations;
    }
    
    public String PrintOut(){
    
         String out= String.format("%f\t%f\t%f\t%f", mean,stdv,maxThreshold,minThershold);
         System.out.println(out);
    
         return out;
    }
            
            
}
