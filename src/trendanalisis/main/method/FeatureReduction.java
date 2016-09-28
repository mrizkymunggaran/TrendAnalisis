/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trendanalisis.main.method;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.stat.StatUtils;
import trendanalisis.main.util.FileImportUtils;
import trendanalisis.main.util.WekaUtils;
import weka.core.Instances;
import weka.core.Utils;

/**
 *
 * @author asus
 */
public class FeatureReduction {

    private int countWord = 0;
    private double tcmin = 0;
    private int[] df;
    private double[] tf;
    private double[] df_prob;
    private String[] lblName;
    private double[] TC;
    private double[] TC_old;
    private Instances instances;
    private Map<String, Double> mapTC;
    private Map<String, Double> mapTF;
    private MethodFitur method;
    private String outPrint = "";

    // private Map<String, String> map_outPrintforResume;
    public String getOutPrint() {

        outPrint = String.format("%s", outPrint);
        return outPrint;

    }

    public String getHeadOutPrint() {

        return "Zscore\tWord\tAlWord \t %";

    }

    /**
     * @return the method
     */
    public MethodFitur getMethod() {
        return method;
    }

    /**
     * @param method the method to set
     */
    public void setMethod(MethodFitur method) {
        this.method = method;
    }

    public static enum MethodFitur implements Serializable {

        TC_BASELINE("TC Baseline"),// menggunakan persetnase
        TC_BASELINE_DF("TC_BASELINE_DF"),//menggunakan persetnse
        TC_BASELINE_NEW("TC_BASELINE_NEW"),//menggunakan persetnse
        TC_BASELINE_NEW_DF("TC_BASELINE_NEW_DF"),//menggunakan persetnse
        TC_NEW_DF("TC_NEW_DF"), // zscore
        TC_NEW("TermContributionNEW"),//zscore
        TC_NON_NEW_DF("TermContributionNonNEW"),// zscore
        TC_NON_NEW_NON_DF("TermContributionNonNEWNonDF");// zscore
        private String name;

        private MethodFitur(String name) {
            this.name = name;
        }

        private double getNewFitur(double val, double newFitur) {

            if (name.equals(MethodFitur.TC_NEW.toString())
                    || name.equals(MethodFitur.TC_BASELINE_NEW.toString())
                    || name.equals(MethodFitur.TC_BASELINE_NEW_DF.toString())
                    || name.equals(MethodFitur.TC_NEW_DF.toString())) {
                val *= newFitur;
            }

            return val;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public FeatureReduction(Instances inst, int df[],
            double tf[], double df_prob[], MethodFitur method) {

        this.df = df;
        this.tf = tf;
        this.df_prob = df_prob;
        this.instances = inst;
        this.method = method;
        //  this.map_outPrintforResume = new HashMap<>();

    }

    public void TCRank() {
        TC = new double[instances.numAttributes()];
        TC_old = new double[instances.numAttributes()];
        lblName = new String[instances.numAttributes()];

        for (int t = 0; t < instances.numAttributes(); t++) {
            lblName[t] = instances.attribute(t).name();

            double tc = 0;

            for (int i = 0; i < instances.numInstances() - 1; i++) {
                for (int j = i + 1; j < instances.numInstances(); j++) {

                    tc += instances.get(i).value(t) * instances.get(j).value(t);

                }

            }
            if (instances.numInstances() == 1) {
                tc = instances.get(0).value(t);
            }

            TC[t] = tc;
            double ntf = FeatureSelection.NormalizationTC(tf[t], df[t]);

            TC_old[t] = tc;
            TC[t] = method.getNewFitur(TC[t], ntf);

        }


    }

    public Instances GetNewDataset(double percentance, Instances instancesDocument) {
        Instances newAttwithTC = null;


        try {
            mapTC = new HashMap<String, Double>();
            mapTF = new HashMap<String, Double>();
            /*
             * DF prob dulu kemudian Zscore
             */
//            for (int i = TC.length - 1; i >= 0; i--) {
//                if (df_prob[i] < 1) {
//                  TC=  ArrayUtils.remove(TC, i);
//                   df_prob= ArrayUtils.remove(df_prob, i);
//                   lblName =(String[]) ArrayUtils.remove(lblName, i);
//                }
//            }
//              System.out.println("RANGK\tKATA\tNTF " + (TC.length));

            int[] sortTC = Utils.sort(TC);
            double meanTC = Utils.mean(TC);
            double stdvTC = Math.sqrt(StatUtils.variance(TC));

            Set<String> lAttNew = new HashSet<String>();
            System.out.println("STDV/means : " + stdvTC + "\t" + meanTC);
             //System.out.println("RANGK\tKATA\tG-TF\tG-DF\tTC\t\tNTF\t\tZ-SCORE\tDF-PROB");
            // System.out.println("RANGK\tKATA\tNTF");
            for (int k = sortTC.length - 1; k >= 0; k--) {

                double zcoreTC = ((TC[sortTC[k]] - meanTC) / stdvTC);//z-score

                mapTC.put(lblName[k], zcoreTC);
                mapTF.put(lblName[k], tf[k]);

//                  System.out.println("");
//                System.out.print(lAttNew.size() + "\t" + lblName[sortTC[k]] + "\t"
//                        + tf[sortTC[k]] + "\t "
//                        + df[sortTC[k]] + "\t"
//                        + String.format("%f", TC_old[sortTC[k]]) + "\t"
//                        + String.format("%f", TC[sortTC[k]]) + "\t"
//                        + String.format("%.2f", zcoreTC) + "\t"
//                        + String.format("%.2f", df_prob[sortTC[k]]));


                double t = 100 - ((double) ((sortTC.length - k - 1) / (double) TC.length) * 100.0);//persentase
                //double GlobalFrekuensiIDF = Math.log10(tf[sortTC[k]] / df[sortTC[k]]);   // GlobalFrekuensiIDF 

                double val = (method.equals(MethodFitur.TC_NEW)
                        || method.equals(MethodFitur.TC_NEW_DF)
                        || method.equals(MethodFitur.TC_NON_NEW_NON_DF)
                        || method.equals(MethodFitur.TC_NON_NEW_DF)) ? zcoreTC : t;


                if (method.equals(MethodFitur.TC_NEW_DF)
                        || (method.equals(MethodFitur.TC_BASELINE_DF)
                        || (method.equals(MethodFitur.TC_BASELINE_NEW_DF))
                        || method.equals(MethodFitur.TC_NON_NEW_DF))) {

                    if (val >= percentance) {

                        if (df_prob[sortTC[k]] >= 1) { // GlobalFrekuensiIDF >0
                            lAttNew.add(lblName[sortTC[k]]);


                        } else {
                              //System.out.print( lblName[sortTC[k]] +", ");
                             //System.out.print(" <-- clear idf prob (<1)");
                        }

                    } else {

                        break;
                    }
                    /*
                     *  perikasa df probability
                     */
                } else if (method.equals(MethodFitur.TC_NEW)
                        || method.equals(MethodFitur.TC_NON_NEW_NON_DF)
                        || method.equals(MethodFitur.TC_BASELINE_NEW)
                        || (method.equals(MethodFitur.TC_BASELINE))) {

                    if (val >= percentance) {
                        lAttNew.add(lblName[sortTC[k]]);


                    } else {
                        //System.out.print("<-- clear by threshold");
                        break;
                    }

                }
                // System.out.println("");

            }

            setCountWord(lAttNew.size());
            // System.out.println("-----------------------"+lAttNew.size());
            setTcmin(TC[sortTC[(int) percentance + 1]]);

            outPrint += String.format("\n\t%.2f\t%d\t%d\t%.2f", percentance, lAttNew.size(), TC.length, ((double) (lAttNew.size() / (double) TC.length) * 100.0));

            ArrayList<String> lString = new ArrayList<String>();
            for (int i = 0; i < instancesDocument.numInstances(); i++) {
                ArrayList<String> ltmp = null;
                for (int j = 0; j < instancesDocument.numAttributes(); j++) {

                    String s = instancesDocument.get(i).stringValue(j).replaceAll("[{}]", "");
                    ltmp = new ArrayList<String>(Arrays.asList(s.split(",")));
                    ltmp.retainAll((lAttNew));

                }

                if (ltmp.size() < 1) {
                    return newAttwithTC;

                }

//                 String simpan = ArrayUtils.toString(ltmp.toArray()).replaceAll("[,]", " ");
//                 simpan = simpan.replaceAll("[{}]", "");
//                 FileImportUtils.WriteFile(simpan, "data/berita/" + i + ".txt");

                lString.add(ArrayUtils.toString(ltmp.toArray()));
            }
            // System.out.println("");
            newAttwithTC = WekaUtils.TransformToDataWeka(lString);


        } catch (Exception ex) {
            Logger.getLogger(FeatureReduction.class.getName()).log(Level.SEVERE, null, ex);
        }


        return newAttwithTC;
    }

    /**
     * @return the countWord
     */
    public int getCountWord() {
        return countWord;
    }

    /**
     * @param countWord the countWord to set
     */
    public void setCountWord(int countWord) {
        this.countWord = countWord;
    }

    /**
     * @return the tcmin
     */
    public double getTcmin() {
        return tcmin;
    }

    /**
     * @param tcmin the tcmin to set
     */
    public void setTcmin(double tcmin) {
        this.tcmin = tcmin;
    }

    /**
     * @return the df
     */
    public int[] getDf() {
        return df;
    }

    /**
     * @param df the df to set
     */
    public void setDf(int[] df) {
        this.df = df;
    }

    /**
     * @return the tf
     */
    public double[] getTf() {
        return tf;
    }

    /**
     * @param tf the tf to set
     */
    public void setTf(double[] tf) {
        this.tf = tf;
    }

    /**
     * @return the df_prob
     */
    public double[] getDf_prob() {
        return df_prob;
    }

    /**
     * @param df_prob the df_prob to set
     */
    public void setDf_prob(double[] df_prob) {
        this.df_prob = df_prob;
    }

    /**
     * @return the lblName
     */
    public String[] getLblName() {
        return lblName;
    }

    /**
     * @param lblName the lblName to set
     */
    public void setLblName(String[] lblName) {
        this.lblName = lblName;
    }

    /**
     * @return the TC
     */
    public double[] getTC() {
        return TC;
    }

    /**
     * @param TC the TC to set
     */
    public void setTC(double[] TC) {
        this.TC = TC;
    }

    /**
     * @param instances the instances to set
     */
    public void setInstances(Instances instances) {
        this.instances = instances;
    }
}
