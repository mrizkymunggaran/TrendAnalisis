/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trendanalisis.main.evaluateclustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.ListUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.stat.StatUtils;
import trendanalisis.main.MainEventTrendingEvaluation;
import trendanalisis.main.util.FileImportUtils;
import weka.core.Utils;

/**
 *
 * @author asus
 */
public class PairWiseFmeasure {

    private String path = MainEventTrendingEvaluation.PATH_TARGET_EVALUATION;
    private double recall = 0;
    private double precision = 0;
    private double[] APP;
    private double[] Purity;

    public PairWiseFmeasure(String Path) {
        this.path = Path;

    }

    public PairWiseFmeasure() {
    }

    private Map<Integer, ArrayList<Integer>> GetClasification() {
        Map<Integer, ArrayList<Integer>> M = new HashMap<Integer, ArrayList<Integer>>();
        ArrayList<String> f = FileImportUtils.readFile(path);

        if (f == null) {
            return null;
        }

        int idx = 0;

        for (String s : f) {
            String[] tmpS = (s.split(" "));
            ArrayList<Integer> ltmp = new ArrayList<Integer>();
            // Collections.sort(ltmp);
            for (int i = 0; i < tmpS.length; i++) {

                ltmp.add(Integer.parseInt(tmpS[i]));
            }
            M.put(idx, ltmp);
            idx++;
        }

        return M;

    }

    public double ProccessEvaluation(Map<Integer, ArrayList<Integer>> mapCluster) {
        Map<Integer, ArrayList<Integer>> M = GetClasification();
        if (M == null) {
            return 0;
        }
        ArrayList<String> Mgold = GetLink(M);
        ArrayList<String> Ccluster = GetLink(new HashMap<>(mapCluster));
        double score = PairWisePRMeasure(Mgold, Ccluster);

        return score;
    }

    private ArrayList<String> GetLink(Map<Integer, ArrayList<Integer>> map) {
        // Map<Integer, ArrayList<String>> M = new HashMap<Integer, ArrayList<String>>();
        ArrayList<String> ltmpM = new ArrayList<String>();
        for (Integer key : map.keySet()) {
            //ArrayList<String> ltmpM = new ArrayList<String>();
            ArrayList<Integer> ltmp = map.get(key);
            Collections.sort(ltmp);
            if (ltmp.size() > 1) {
                for (int i = 0; i < ltmp.size() - 1; i++) {
                    String s = ltmp.get(i) + " " + ltmp.get(i + 1);
                    ltmpM.add(s);
                }
            } else {

                ltmpM.add(ltmp.get(0).toString());
            }


        }


        return ltmpM;
    }

    private double PairWisePRMeasure(ArrayList<String> A, ArrayList<String> B) {

        /*Pairwise Recall
         *          tp/fn+tp
         * 
         *Pairwise Precision
         *          tp/fp+tp
         */

        int tp = 0;
        int fn = 0;
        int fp = 0;
        for (String i : A) {

            tp += (ArrayUtils.indexOf(B.toArray(), i) != -1) ? 1 : 0;

        }

        fn = (A.size() - tp) + (B.size() - tp);
        fp = (B.size() - tp);

        recall = (double) tp / ((double) fn + (double) tp);
        precision = (double) tp / ((double) fp + (double) tp);

        return FMeasure(recall, precision);
    }

    private double FMeasure(double Recall, double Precision) {

        return (2 * ((Recall * Precision) / (Recall + Precision)));
    }

    public double ProccessEvaluationAdjustPWprecision(Map<Integer, ArrayList<Integer>> mapCluster) {



        Map<Integer, ArrayList<String>> clusters = new HashMap<>();

        Map<Integer, ArrayList<Integer>> M = GetClasification();
        if (M == null) {
            return 0;
        }
        ArrayList<String> gold = GetLink(M);

        for (int key : mapCluster.keySet()) {
            Map<Integer, ArrayList<Integer>> tmp = new HashMap<>();
            tmp.put(key, mapCluster.get(key));
            clusters.put(key, GetLink(tmp));
        }

        setAPP(new double[mapCluster.size()]);
        int[] numObject = new int[mapCluster.size()];

        for (Integer key : clusters.keySet()) {
            double count = 0;
            for (String linkOfC : clusters.get(key)) {
                count += (gold.indexOf(linkOfC) != -1) ? 1 : 0;
            }
            numObject[key] = (mapCluster.get(key).size());
            getAPP()[key] = (count == 0) ? 0 : count / (double) (numObject[key] + 1);

        }



        return APPFmeasure(getAPP(), numObject);
    }

    private double APPFmeasure(double APP[], int[] numObject) {

        double val = 0;
        for (int i = 0; i < APP.length; i++) {
            val += APP[i] / (double) numObject[i];
        }
        return (((double) 1 / (double) numObject.length) * val);
    }

    public double ProccessEvaluationPurity(Map<Integer, ArrayList<Integer>> mapCluster, int N) {


        Purity = new double[mapCluster.size()];
        double [] maxPurity = new double[mapCluster.size()];
        int[] tj = new int[mapCluster.size()];
       
        Map<Integer, ArrayList<Integer>> M = GetClasification();
        if (M == null) {
            return 0;
        }

        for (Integer key : mapCluster.keySet()) {
            double tmp[] = new double[M.size()];
            for (int obj : mapCluster.get(key)) {

                for (Integer keyg : M.keySet()) {
                    if ((M.get(keyg).indexOf(obj)) != -1) {
                        tmp[keyg] += 1;
                        break;
                    }
                }
            }
            int indexMax = Utils.maxIndex(tmp);
            Purity[key] = (double) tmp[indexMax]/(double) mapCluster.get(key).size();
            maxPurity[key]= (double) tmp[indexMax];
            tj[key] = M.get(indexMax).size();
        }
        
        
        
        
        double val = 0;
        for (int i = 0; i < Purity.length; i++) {
//            val += (double) maxPurity[i]
//                    * (Math.log((( double)maxPurity[i] * (double) N) / ((double)mapCluster.get(i).size() * (double)tj[i]))
//                    / (Math.log((mapCluster.size() * M.size()))));
            
            val += (double) maxPurity[i];
                    
        }


        return ((double) 1 / (double) N) * val;
        
        
    }

    /**
     * @return the recall
     */
    public double getRecall() {
        return recall;
    }

    /**
     * @return the precision
     */
    public double getPrecision() {
        return precision;
    }

    /**
     * @return the APP
     */
    public double[] getAPP() {
        return APP;
    }

    /**
     * @param APP the APP to set
     */
    public void setAPP(double[] APP) {
        this.APP = APP;
    }

    /**
     * @return the Purity
     */
    public double[] getPurity() {
        return Purity;
    }

    /**
     * @param Purity the Purity to set
     */
    public void setPurity(double[] Purity) {
        this.Purity = Purity;
    }
}
