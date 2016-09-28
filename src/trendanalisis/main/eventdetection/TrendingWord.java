/*
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
import nz.ac.waikato.cs.weka.Utils;
import weka.core.Instances;

/**
 *
 * @author asus
 */
public class TrendingWord {

    /*
     * CTF = jumlah kata dalam satu cluster
     *  CF=jumlah cluster yang mempunyai kata ke i
     * kata yg digunakan adalah kata kunci yg sudah dipilih
     * lamda*log(1+CTF)+ lamda *log(1+CF)
     */
    private static int max_count_word = 10;
    private static final double lambda = 0.5;

    public static Map<Integer, ArrayList<String>> Calculate(ArrayList<Map<String, Double>> ArrTFandAttributCluster) {

        Map<Integer, ArrayList<String>> mapnewKeyWord = new HashMap<>();

        for (int i = 0; i < ArrTFandAttributCluster.size(); i++) {

            Map<String, Double> mapTFofCluster = ArrTFandAttributCluster.get(i);
            double[] val = new double[mapTFofCluster.keySet().size()];
            ArrayList<String> keyWords = new ArrayList<>();
            int j = 0;
            Iterator<String> it = mapTFofCluster.keySet().iterator();
            for (; it.hasNext();) {
                String word = it.next();
                keyWords.add(word);

                double CTF = mapTFofCluster.get(word);
                double CF = FindCF(ArrTFandAttributCluster, word, i);
                val[j] = (lambda * Math.log(1 + CTF)) + (lambda * Math.log(1 + CF));
                j++;
            }


            int[] sort = Utils.sort(val);
            ArrayList<String> newkeyWords = new ArrayList<>();

            j = 0;
            while (j < max_count_word) {
                newkeyWords.add(keyWords.get(sort[ sort.length - 1 - j]));
                j++;
            }

            mapnewKeyWord.put(i, newkeyWords);

        }




        return mapnewKeyWord;
    }

    private static int FindCF(ArrayList<Map<String, Double>> ArrTFandAttributCluster, String keyWord, int idCluster) {

        int val = 0;
        for (int i = 0; i < ArrTFandAttributCluster.size(); i++) {

            if (i == idCluster) {
                continue;
            }

            Map<String, Double> mapTFofCluster = ArrTFandAttributCluster.get(i);
            if (mapTFofCluster.containsKey(keyWord)) {
                val++;
            }
          


        }

        return val;
    }
}
