/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trendanalisis.main.method;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections15.MapUtils;
import org.apache.commons.math.stat.StatUtils;
import trendanalisis.main.evaluateclustering.InternalMeasureOther;
import trendanalisis.main.evaluateclustering.SilhouetteCoefficient;
import trendanalisis.main.tools.weka.InitCoreWekaKmeans;
import weka.core.EuclideanDistance;
import weka.core.Instances;
import weka.core.Utils;

/**
 * * Hertigan Index algorithm.
 * @author asus
 */
public class HartiganEvaluation {

    private Instances instances_data;
    private Map<Integer, int[]> map_evaluation_centroid;
    private int numEvaluation = 0;
    private ArrayList< Map<Integer, ArrayList<Integer>>> listClusterEvaluation = null;
    private int bestHartgK = 0;
    private double bestHartgErrorMax = 0;
    private String outPrint = "";
    private ArrayList<String> listoutPrintKmeans = new ArrayList<>();
    private ArrayList<String> listoutPrintInternalMeasure = new ArrayList<>();

    public ArrayList<String> getListoutPrintInternalMeasure() {
        return listoutPrintInternalMeasure;
    }
    private ArrayList<Double> listValNewMethod = new ArrayList<>();
    
      private ArrayList<Double> errorSSE = new ArrayList<>();
    private double newMethod = 0;

    public double getNewMethod() {
        return newMethod;
    }
    private String outPrintBestHartgKOnly = "";


  
     public int getIndex( int K) {
        return (K-firstIndex);
    }


    public String getOutPrintBestHatgKOnly() {
        return outPrintBestHartgKOnly;
    }
    private int firstIndex = 0;

    public int getFirstIndex() {
        return firstIndex;
    }

    public void Evaluation() {

        listClusterEvaluation = new ArrayList<>();
        numEvaluation = map_evaluation_centroid.keySet().size();
        firstIndex = 0;

        MapUtils.orderedMap(map_evaluation_centroid);
        
        

        double[] H = new double[numEvaluation];
        /*
         * Hitung nilai error setiap hasil cluster
         */
        int i = 0;
        Object[] objs = map_evaluation_centroid.keySet().toArray();
        Arrays.sort(objs);
        for (Object k : objs) {
            
                firstIndex += (i == 0) ? (int) k : 0;
                H[i] = SSEKmeans(map_evaluation_centroid.get(k),(int) k);
                getErrorSSE().add(H[i]);

                i++;
            }
        

        /*
         * Li, X., R. Ramachandran, S. Movva 2008
         * Hertigan Index
         * H[k]= (n-k-1) (err(k)- err(k+1)) / err(k+1)
         * choise high error until min to max evaluation
         */
        double N = instances_data.numInstances();
        double[] HC = new double[numEvaluation];
        for (i = 0; i < H.length - 1; i++) {

            HC[i] = (((N - (firstIndex + i) - 1) * ((H[i] - H[i + 1]) / (H[i + 1]))));
            outPrint += (MessageFormat.format("\n\tHertigan  H[{0}]: Calculate({1} & {2}) ---> {3}", (firstIndex + i + 1), H[i], H[i + 1], (HC[i])));
        }


        int idx = Utils.maxIndex(HC);
        bestHartgK = (firstIndex + idx + 1);
        bestHartgErrorMax = HC[idx];
        outPrintBestHartgKOnly = listoutPrintKmeans.get(idx + 1);

        newMethod = listValNewMethod.get(idx + 1);


        outPrint += ("\n---->> Hertigan BEST K :" + bestHartgK + " || " + bestHartgErrorMax);

    }

    private double SSEKmeans(int[] centroids, int k) {
        double err = 0;
        try {

            /*
             * k-means evaluasi setiap centroid
             */
            InitCoreWekaKmeans kmeans = new InitCoreWekaKmeans();
            Map<Integer, ArrayList<Integer>> mapClusters = kmeans.KMeansWeka(instances_data, k, centroids,
                    new EuclideanDistance());

            String internalMeasure = ClusterInternalMeasurePrintOut(kmeans.getNumClaster(), instances_data.numInstances(), kmeans);

            listoutPrintInternalMeasure.add(internalMeasure);
            listoutPrintKmeans.add(kmeans.getPrintOut());
            listClusterEvaluation.add(mapClusters);

            err = kmeans.getErrSquared();

        } catch (Exception ex) {
            Logger.getLogger(HartiganEvaluation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (err);
    }

    public void setInstances_data(Instances instances_data) {
        this.instances_data = instances_data;
    }

    public void setMap_evaluation_centroid(Map<Integer, int[]> map_evaluation_centroid) {

        this.map_evaluation_centroid = map_evaluation_centroid;
    }

    public double getBestErrorMax() {
        return bestHartgErrorMax;
    }

    public int getBestK() {
        return bestHartgK;
    }

    public ArrayList<Map<Integer, ArrayList<Integer>>> getListClusterEvaluation() {
        return listClusterEvaluation;
    }

    /**
     * @return the outPrint
     */
    public String getOutPrint() {
        String title = "K\tSSE\tIteration";
        outPrint += String.format("\n\tInfo Internal Measure K-means\n\t%s", title);
        for (String s : listoutPrintInternalMeasure) {
            outPrint += "\n\t" + s;
        }
        return outPrint;
    }
    
//       public String getOutPrint() {
//        String title = "K\tHAR\tFRAT\tWB-IND\tSSW\tSSB\tSST\tSST+SSB-Ratio\tsilhouet";
//        outPrint += String.format("\n\tInfo Internal Measure K-means\n\t%s", title);
//        for (String s : listoutPrintInternalMeasure) {
//            outPrint += "\n\t" + s;
//        }
//        return outPrint;
//    }

    private String ClusterInternalMeasurePrintOut(int k, int N, InitCoreWekaKmeans kmeans) {

        double derajatSSB = k + 1;
        double derajatSSE = N;//N-k;

        //log(SSB/SSW)        ----    Hartigan (1975) 
        double Hart = InternalMeasureOther.Hartigan(kmeans.getErrSSBSquared(), kmeans.getErrSquared());

        //Calinski & Harabasz (1974)
        //(SSB/k-1) / (SSE/N-k)
        //F-Ration
        double FRatio = kmeans.getErrSSBSquared() / derajatSSB;
        FRatio = FRatio / (kmeans.getErrSquared() / derajatSSE);

        //WB-Index
        //  k * SSE/SSB        
        double WBIndex = InternalMeasureOther.WBIndex(k, kmeans.getErrSquared(), kmeans.getErrSSBSquared());


        SilhouetteCoefficient silhouett = new SilhouetteCoefficient();
        double silhouettValue = 0;
        try {
            silhouettValue = silhouett.evaluateClustering(kmeans.getMapCluster(), kmeans.getInstances());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        double newFunc = ((FRatio - (kmeans.getErrSSTSquared() + kmeans.getErrSquared())) / N) * (k + 1);
       // String report = String.format("%d\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%f\t%.2f", k, Hart, FRatio, WBIndex, kmeans.getErrSquared(), kmeans.getErrSSBSquared(), kmeans.getErrSSTSquared(), newFunc, silhouettValue);
 String report = String.format("%d\t%.2f\t%d", k,  kmeans.getErrSquared(),kmeans.getIteration());

        listValNewMethod.add(newFunc);

        return report;

    }

    /**
     * @return the errorSSE
     */
    public ArrayList<Double> getErrorSSE() {
        return errorSSE;
    }

    /**
     * @param errorSSE the errorSSE to set
     */
    public void setErrorSSE(ArrayList<Double> errorSSE) {
        this.errorSSE = errorSSE;
    }
}
