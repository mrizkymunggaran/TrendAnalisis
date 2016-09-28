/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trendanalisis.main.tools.weka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import weka.core.DistanceFunction;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Utils;

/**
 *
 * @author asus
 */
public class InitCoreWekaKmeans {

    /*
     * -Build Cluster K-means dengan Kakas WEKA 3.7
     * -Menambahkan CosineDistance untuk Modifikasi Weka 3.7
     * -Menambahkan Function Initial Centroid dari Hasil PCA untuk Modifikasi Weka 3.7-
     */
     private int iteration;
    private double errSquared;
    private double errSSBSquared;
    private double errSSTSquared;
    private int numClaster = 0;
    private Instances instances;
    private String printOut = "";
    private CoreWekaKmeans km;
    private Map<Integer, ArrayList<Integer>> mapCluster;
     SelectedTag tag = new SelectedTag(CoreWekaKmeans.CENTROID_MODIFICATION, CoreWekaKmeans.TAGS_SELECTION);

    public Map<Integer, ArrayList<Integer>> getMapCluster() {
        return mapCluster;
    }

    public String getPrintOut() {
        return printOut;
    }

    public Map<Integer, ArrayList<Integer>> KMeansWeka(Instances instances, int k, int[] initialCentroid, DistanceFunction df) {
        Map<Integer, ArrayList<Integer>> mapLabel = null;
        this.setInstances(new Instances(instances));
        try {
           
            mapLabel = Build(instances, k, initialCentroid, df, 100, tag);
            setErrSSBSquared(km.getSSBSquaredError());
            setErrSSTSquared(km.getSSTSquaredError());
            setErrSquared(km.getSquaredError());
            setNumClaster(km.numberOfClusters());
            iteration=km.getIteration();
//             ClusterEvaluation evaluation = new ClusterEvaluation();
//             evaluation.setClusterer(km);
//             evaluation.evaluateClusterer(instances);
//             System.out.println((evaluation.clusterResultsToString()));         
            //  System.out.println(km.toString());
            


            // System.out.println("Ukuran Cluster" + "\t: " + Utils.arrayToString(km.getClusterSizes()));


            // System.out.println("Hasil Cluster" + "\t: " + Utils.arrayToString(strText));

            /*
             * Mencari Doc terdekat dengan titik cluster
             * menggunakan CosineDistance Weka (Modification)
             */
            StringBuffer out = new StringBuffer();
            out.append("\n----Parameter Input K-Means-----");
          
            if (initialCentroid!=null)
            out.append("\nInitial Centroid\t: " + Utils.arrayToString(initialCentroid));
            out.append("\nJum Cluster Hartigan\t: " + km.getNumClusters());
           // out.append("\nJum Cluster Hartigan\t: " + k);
            out.append("\n----Hasil Cluster----");
            out.append("\nError\t\t: " + km.getSquaredError());
            out.append("\nIteration\t: " + km.getIteration());

            printOut = out.toString();



        } catch (Exception ex) {
            Logger.getLogger(InitCoreWekaKmeans.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mapLabel;
    }

    public Map<Integer, ArrayList<Integer>> Build(Instances instances, int k, int[] initialCentroid,
            DistanceFunction df, int maxIteration, SelectedTag tag) {
        Map<Integer, ArrayList<Integer>> mapLabel = null;
        this.setInstances(new Instances(instances));
        try {
            km = new CoreWekaKmeans();
            km.setPreserveInstancesOrder(true);
            // km.setSeed(50);
            km.setMaxIterations(maxIteration);
            km.setDistanceFunction(df);
            km.setNumClusters(k);
            // ArrayUtils.reverse(initialCentroid);
            // System.out.println(k+  " -- " + Arrays.toString(initialCentroid));
            km.setInitialCentroid(initialCentroid);

            // tag = new SelectedTag(CoreWekaKmeans.KMEANS_PLUS_PLUS, CoreWekaKmeans.TAGS_SELECTION);
            if (initialCentroid == null) {
                tag = new SelectedTag(CoreWekaKmeans.KMEANS_PLUS_PLUS, CoreWekaKmeans.TAGS_SELECTION);
            }

            km.setInitializationMethod(tag);
            km.setNumExecutionSlots(k);
            km.setDontReplaceMissingValues(false);
            km.setM_minSizeInCluster(1);
            
            //    km.setMaxIterations(1);
            km.buildClusterer(instances);
            setErrSSBSquared(km.getSSBSquaredError());
            setErrSSTSquared(km.getSSTSquaredError());
            setErrSquared(km.getSquaredError());
            setNumClaster(km.numberOfClusters());
            iteration=km.getIteration();
            
            String[] strText = new String[km.getNumClusters()];
            mapLabel = new HashMap<Integer, ArrayList<Integer>>();
            mapCluster = mapLabel;

            /*
             * -Identifikasi doc terhadap cluster terdekat
             * -Set Doc tercluster kedalam HashMap sebelum dilakukan doc labelling
             */
            for (int j = 0; j < instances.numInstances(); j++) {

                if (strText[km.clusterInstance(instances.instance(j))] == null) {
                    strText[km.clusterInstance(instances.instance(j))] = "";
                    mapLabel.put(km.clusterInstance(instances.instance(j)), new ArrayList<Integer>());
                }
                ArrayList<Integer> tmp = mapLabel.get(km.clusterInstance(instances.instance(j)));
                strText[km.clusterInstance(instances.instance(j))] += j + " ";
                tmp.add(j);
                mapLabel.put(km.clusterInstance(instances.instance(j)), tmp);
            }

           //  System.out.println("====K-MEAN WEKA===");
            // System.out.println(km.toString());
             


        } catch (Exception ex) {
            Logger.getLogger(InitCoreWekaKmeans.class.getName()).log(Level.SEVERE, null, ex);
        }

        return mapLabel;
    }

    /**
     * @return the errSquared
     */
    public double getErrSquared() {
        return errSquared;
    }

    /**
     * @param errSquared the errSquared to set
     */
    public void setErrSquared(double errSquared) {
        this.errSquared = errSquared;
    }

   
    /**
     * @return the errSSBSquared
     */
    public double getErrSSBSquared() {
        return errSSBSquared;
    }

    /**
     * @param errSSBSquared the errSSBSquared to set
     */
    public void setErrSSBSquared(double errSSBSquared) {
        this.errSSBSquared = errSSBSquared;
    }

    /**
     * @return the errSSTSquared
     */
    public double getErrSSTSquared() {
        return errSSTSquared;
    }

    /**
     * @param errSSTSquared the errSSTSquared to set
     */
    public void setErrSSTSquared(double errSSTSquared) {
        this.errSSTSquared = errSSTSquared;
    }

    /**
     * @return the numClaster
     */
    public int getNumClaster() {
        return numClaster;
    }

    /**
     * @param numClaster the numClaster to set
     */
    public void setNumClaster(int numClaster) {
        this.numClaster = numClaster;
    }

    /**
     * @return the instances
     */
    public Instances getInstances() {
        return instances;
    }

    /**
     * @param instances the instances to set
     */
    public void setInstances(Instances instances) {
        this.instances = instances;
    }

    /**
     * @return the iteration
     */
    public int getIteration() {
        return iteration;
    }

    /**
     * @param iteration the iteration to set
     */
    public void setIteration(int iteration) {
        this.iteration = iteration;
    }
}
