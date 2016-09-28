/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trendanalisis.main.eventdetection;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import trendanalisis.main.evaluateclustering.PairWiseFmeasure;
import trendanalisis.main.method.FeatureReduction;
import trendanalisis.main.tools.weka.InitCoreWekaTFIDF;
import weka.core.Instances;
import weka.core.Utils;

/**
 *
 * @author asus
 */
public class TrendEvent {

    private String outPrint = "";
    private String trendEvet = "";
    private Map<Integer, ArrayList<String>> mapKeyWord;
    private final int NUM_WORD = 15;

    public void Build(Map<Integer, ArrayList<Integer>> mapCluster, Instances instances, Map<String, Double> mapGlobalTFandAttribut) throws FileNotFoundException, IOException, Exception {

        //ArrayList<String> keyWords = new ArrayList<String>(); //keyWORDS
        StringBuffer keyWordBuffer = new StringBuffer(); // Keywords buffer for outprit
        Map<Integer, ArrayList<Integer>> mapNewCluster = new HashMap<>();
        Map<Integer, ArrayList<String>> mapKeyWordTC = new HashMap<>();
        // ArrayList<Map<String, Double>> ArrTFandAttributCluster = new ArrayList<>(); //keyWORDS

        /* Ekstrak Kata Kunci
         * Untuk setip cluter dengan kombinasi TC + Normalization+df_prob b+global_tf/global_df)
         */

        String outPrintCusterLabel = "";
        PairWiseFmeasure evaluation = new PairWiseFmeasure();
        //double scoreAPP = evaluation.ProccessEvaluationAdjustPWprecision(mapCluster);
        double scorePurity = evaluation.ProccessEvaluationPurity(mapCluster, instances.numInstances());
        //System.out.println("dd " +scorePurity +" " + instances.numInstances());
        for (int j : mapCluster.keySet()) {
            try {
                Instances tmp = new Instances(instances);
                tmp.clear();
                //  MutualInformation.calculateMutualInformation(data[k], data[j]);
                for (int i = 0; i < mapCluster.get(j).size(); i++) {
                    tmp.add(instances.get(mapCluster.get(j).get(i)));
                }

                InitCoreWekaTFIDF tfidf = new InitCoreWekaTFIDF();
                Instances tfidfCluster = tfidf.TFIDFProccess(tmp, 1);

                // ArrTFandAttributCluster.add(tfidf.getMapTFAndAttribut());// set extraksi versi cluster impotan

                FeatureReduction tc = new FeatureReduction(tfidfCluster, tfidf.getNumdocdf(), tfidf.getglobal_tf(), tfidf.getdf_prob(), FeatureReduction.MethodFitur.TC_NEW_DF);
                tc.TCRank();

                double[] dfd = tc.getTC();
                int[] sortd = Utils.sort(dfd);
                ArrayList<String> keyWordsOfC = new ArrayList<>();//kata kuci

                for (int k = sortd.length - 1; k >= 0; k--) {
                    keyWordsOfC.add(tfidfCluster.attribute(sortd[k]).name());

                    if (keyWordsOfC.size() == NUM_WORD) {
                        break;
                    }
                }

                mapKeyWordTC.put(j, new ArrayList<>(keyWordsOfC));

                outPrintCusterLabel += ("\nC-" + (j) + "(" + mapCluster.get(j).size() + ")("
                        + String.format("%.2f",  evaluation.getPurity()[j]) + ")\t("
                        + Arrays.toString(keyWordsOfC.subList(0, (keyWordsOfC.size() > 5) ? 5
                        : keyWordsOfC.size()).toArray()).toUpperCase() + "): \t\t\t"
                        + Utils.arrayToString(mapCluster.get(j).toArray()));





//                for (int i = 1; i < sortd.length; i++) {
//                    keyWordsOfC.add(tfidfCluster.attribute(sortd[sortd.length - i]).name());
//
//                }
//                int lenght = (keyWordsOfC.size() >= 15) ? 15 : keyWordsOfC.size();
//                mapKeyWordTC.put(j, new ArrayList<>(keyWordsOfC.subList(0, lenght)));
//                outPrintCusterLabel += ("\nC-" + (j) + "(" + mapCluster.get(j).size() + ")\t (" + Arrays.toString(keyWordsOfC.subList(0, (lenght > 5) ? 5 : lenght).toArray()).toUpperCase() + "): \t\t\t" + Utils.arrayToString(mapCluster.get(j).toArray()));
//
//                //   outPrintCusterLabel +=("\nC-" + (j) + "(" + mapCluster.get(j).size() + ")\t (" + Arrays.toString(keyWordsOfC.subList(0, 15).toArray()).toUpperCase() + ")");// \t\t\t" + Utils.arrayToString(mapCluster.get(j).toArray()));
//                String s = keyWordsOfC.toString();
//
//                if (keyWordsOfC.size() > 10) {
//                    s = Utils.arrayToString(ArrayUtils.subarray(keyWordsOfC.toArray(), 0, 8));
//                }

                //  keyWords.add(s);

            } catch (IOException ex) {
                Logger.getLogger(TrendEvent.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(TrendEvent.class.getName()).log(Level.SEVERE, null, ex);
            }

        }


        mapNewCluster.putAll(mapCluster);

        // PairWiseFmeasure measure = new PairWiseFmeasure(PATH_TARGET_EVALUATION);
        // double score = measure.ProccessEvaluation(mapNewCluster);

        /*
         * cluster mportance adaptasi
         * trending event          */

        Map<Integer, ArrayList<String>> mapnewKeyWord = mapKeyWordTC;//TrendingWord.Calculate(ArrTFandAttributCluster);
        //   Map<Integer, ArrayList<String>> mapnewKeyWord = TrendingWord.Calculate(ArrTFandAttributCluster);
        int[] rankCluster2 = Utils.sort(ClusterImportance.Calculate(mapnewKeyWord, mapGlobalTFandAttribut));

        keyWordBuffer.append(outPrintCusterLabel);
        keyWordBuffer.append("\n---------------------------------------------------");
       // keyWordBuffer.append("\n Adjust Pair wise (%) cluster : " + scoreAPP);
        keyWordBuffer.append("\n PURITY cluster : " + scorePurity);//StatUtils.sum(evaluation.getPurity())/evaluation.getPurity().length);
        keyWordBuffer.append("\n Ranking cluster : " + Utils.arrayToString(rankCluster2));
        keyWordBuffer.append("\n Best cluster : " + (rankCluster2[rankCluster2.length - 1]));
        keyWordBuffer.append("\n Event trend : " + mapnewKeyWord.get(rankCluster2[rankCluster2.length - 1]).toString().toUpperCase());

        keyWordBuffer.append("\n---------------------------------------------------");


//        for (int idc : mapNewCluster.keySet()) {
//
//            keyWordBuffer.append("\n" + idc + " " + mapnewKeyWord.get(idc).toString() + "\t" + Arrays.toString(mapNewCluster.get(idc).toArray()));
//
//        }

        trendEvet = mapnewKeyWord.get(rankCluster2.length - 1).toString();
        outPrint = keyWordBuffer.toString();
        mapKeyWord = mapnewKeyWord;

    }

    public String getOutPrint() {
        return outPrint;
    }

    public void setOutPrint(String outPrint) {
        this.outPrint = outPrint;
    }

    public String getTrendEvet() {
        return trendEvet;
    }

    public void setTrendEvet(String trendEvet) {
        this.trendEvet = trendEvet;
    }

    /**
     * @return the mapKeyWord
     */
    public Map<Integer, ArrayList<String>> getMapKeyWord() {
        return mapKeyWord;
    }

    /**
     * @param mapKeyWord the mapKeyWord to set
     */
    public void setMapKeyWord(Map<Integer, ArrayList<String>> mapKeyWord) {
        this.mapKeyWord = mapKeyWord;
    }
}
