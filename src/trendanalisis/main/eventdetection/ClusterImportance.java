/*
 * Adopsi method dari
 * TECHNIA – International Journal of Computing Science and Communication Technologies, VOL. 2, NO. 1, July 2009. 
Sentence Clustering-based Summarization of  Multiple Text Documents Kamal Sarkar 
Computer Science & Engineering Department, Jadavpur University, Kolkata – 700 032, INDIA,
[jukamal2001@yahoo.com]
 */
package trendanalisis.main.eventdetection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.lang.ArrayUtils;
import weka.core.Instances;
import weka.core.Utils;

/**
 *
 * @author asus
 */
public class ClusterImportance {

    private static int max_count_word = 15;
    private static double lambda = 0.5;

    public static double[] Calculate(Map<Integer, ArrayList<String>> globalKeyWords, Map<String, Double> globalTF) {


        double[] val = new double[globalKeyWords.keySet().size()];


        Iterator<Integer> it = globalKeyWords.keySet().iterator();
        for (; it.hasNext();) {
            Integer key = it.next();

            ArrayList<String> keyWords = globalKeyWords.get(key);

            if (max_count_word > keyWords.size()) {
                max_count_word = keyWords.size();
            }


            for (int i = 0; i < max_count_word; i++) {

                double countW = globalTF.get(keyWords.get(i));

                val[key] += Math.log(1 + countW);

                //   System.out.println(keyWords.get(i)+ " "+   (lambda * Math.log(1 + countW)) +" * "+ (CF[i]) +"="+    val[i] );

            }

        }
        return val;
    }

    public static double Calculate(ArrayList<String> keyWords, Instances data) {

        double val = 0;

        if (max_count_word > keyWords.size()) {
            max_count_word = keyWords.size();
        }

        for (int i = 0; i < max_count_word; i++) {
            double countW = 0;
            int idx = data.attribute(keyWords.get(i)).index();
            for (int j = 0; j < data.numInstances(); j++) {

                countW += (data.get(j).value(idx) > 0) ? 1 : 0;


            }

            val += Math.log(1 + countW);

        }

        return val;
    }

//    public static double[] Calculate(Map<Integer, Instances> globalTFIDF, Map<Integer, ArrayList<String>> globalKeyWords) {
//
//        Map<Integer, ArrayList<String>> mapnewKeyWord = new HashMap<>();
//        double[] val = new double[globalTFIDF.size()];
//
//
//
//        for (Integer key : globalTFIDF.keySet()) {
//
//            Instances data = globalTFIDF.get(key);
//
//            ArrayList<String> keyWords = globalKeyWords.get(key);
//
//            if (max_count_word > keyWords.size()) {
//                max_count_word = keyWords.size();
//            }
//
//            int countWordOtherCluster[] = FindWordOtherCluster(globalTFIDF, keyWords, key);
//
//
//            for (int i = 0; i < max_count_word; i++) {
//
//                int countW = 0;
//                int idx = data.attribute(keyWords.get(i)).index();
//
//                for (int j = 0; j < data.numInstances(); j++) {
//
//                    countW += (data.get(j).value(idx) > 0) ? 1 : 0;
//
//                }
//
//                val[key] += Math.log(1 + (countW + countWordOtherCluster[i]));
//
//                //   System.out.println(keyWords.get(i)+ " "+   (lambda * Math.log(1 + countW)) +" * "+ (CF[i]) +"="+    val[i] );
//
//            }
//
//        }
//        return val;
//    }
//
   /*
     * jumlah cluster yang mempunyai kata ke i
     * kata yg digunakan adalah kata kunci yg sudah dipilih
     */
    public static int[] FindWordOtherCluster(Map<Integer, Instances> globalTFIDF, ArrayList<String> keyWords, int idCluster) {

        int[] val = new int[max_count_word];
        for (Integer key : globalTFIDF.keySet()) {

            if (key == idCluster) {
                continue;
            }

            Instances data = globalTFIDF.get(key);

            for (int i = 0; i < max_count_word; i++) {

                int countW = 0;

                if (data.attribute(keyWords.get(i)) == null) {
                    continue;
                }

                int idx = data.attribute(keyWords.get(i)).index();

                for (int j = 0; j < data.numInstances(); j++) {

                    countW += (data.get(j).value(idx) > 0) ? 1 : 0;

                }

                val[i] += countW;

                // val[i] += (data.attribute(keyWords.get(i)) == null) ? 0 : 1;

            }

        }

        return val;
    }
}
