/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trendanalisis.main.method;

import trendanalisis.main.method.Euclidean;
import trendanalisis.main.tools.weka.CosineDistance;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import weka.core.DistanceFunction;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import java.util.Arrays;
import org.apache.commons.lang.ArrayUtils;
import trendanalisis.main.tools.weka.CoreWekaKmeans;

/**
 *
 * @author asus
 * sim thrhold 0.95
 */
public class CosineMedianCentroid {

    private double thresholdSim = 0.5; //default
    private int numInstance = 0;
    private ArrayList<Integer> idCentroids = null;
    //  private Instances instancesCentroid = null;
    private int flagCalLength = 0;
    private Map<Integer, int[]> mapCentroids = null;
    private int KODE_PROSES = 1;

    public void setKODE_PROSES(int KODE_PROSES) {
        this.KODE_PROSES = KODE_PROSES;
    }
    private Instances instances_data = null;
    private String outPrint = "";

    public String getOutPrint() {
        return outPrint;
    }
    // private final int min_num_lenght  = 4;

    public CosineMedianCentroid(double threshold) {
        this.thresholdSim = threshold;
    }

    public void Build(Instances instances) {

        this.instances_data = instances;

        ArrayList<Integer> tmpidList = new ArrayList<Integer>();
        for (int i = 0; i < instances.numInstances(); i++) {
            tmpidList.add(i);
        }


        /*
         * 1 . kumupulkan data cosine similarity (estimasi 0.5 diangap mirip)
         * 2. kumpulan data >= Sturges Base 10 (jml data)--> jml data akan berkurang sesuai dengan iterasi pengecekan
         * 3.  hitung titik pusat : (core k-means) dari kumpulan data dengan cosine
         * 4. ambil data dengan kemiripan terdekat dengan titik pusat
         * 5. data yng sudah dipakai / terkelompok tidak akan di hitung cosine lagi dengan data lain
         */

        int iteration = instances.numInstances();
        //iteration= 2;
        for (int i = 0; i < iteration; i++) {
            /*
             * iterasi -0 --> menacri titik pusat
             * iterasi -1 s.d N-->(jika mungkin) menghilangkan kemungkinan redudansi titik pusat
             */
            ArrayList<Integer> idtmps = FindCandidateCentroid(tmpidList, i);

            if (idtmps.size() > 1 && idtmps.size() != tmpidList.size()) {
                tmpidList = idtmps;
            } else {
                break;
            }
            outPrint += ("\n\tCosine->iterasi-" + i + "(count : " + tmpidList.size() + ") : " + Utils.arrayToString(tmpidList.toArray()));
            // System.out.println("\n\tCosine->iterasi-" + i + "(" + tmpidList.size() + ") : " + Utils.arrayToString(tmpidList.toArray()));
        }


        idCentroids = tmpidList;
        /*
         * Renge Evaluation
         */
        int rangeEvaluationMin = (idCentroids.size() - 3);// batas min
        int rangeEvaluationMax = (idCentroids.size());//batas max

        if (idCentroids.size() == instances_data.numInstances()) {
            rangeEvaluationMin = (int) StatisticsNumberCluster.RuleOfSturgesBase10(idCentroids.size()) - 3;
            rangeEvaluationMax = (int) StatisticsNumberCluster.RuleOfSturgesBase10(idCentroids.size()) + 3;
        }

        rangeEvaluationMin = (rangeEvaluationMin <= 2) ? (rangeEvaluationMin + 1) : rangeEvaluationMin;//validasi batas evaluasi <2

        /*
         * Median for evaluation
         */
        mapCentroids = new HashMap<>();
        int centroidsOrdered[] = EuclideanLength();
        for (int r = rangeEvaluationMin; r <= rangeEvaluationMax; r++) {

            int orderwithK[] = Median(r, centroidsOrdered);
            mapCentroids.put(r, orderwithK);

            outPrint += ("\n\tmedian->" + (orderwithK.length) + ": " + Utils.arrayToString(orderwithK));

        }

    }

    private ArrayList<Integer> FindCandidateCentroid(ArrayList<Integer> id, int iteration) {

        DistanceFunction cosine = new CosineDistance();
        ArrayList<Integer> tmp = new ArrayList<>();
        ArrayList<Integer> idCandidateCentroids = new ArrayList<>();

        for (int i = 0; i < id.size(); i++) {

            Instances instancesCosineMember = new Instances(instances_data);//new
            instancesCosineMember.clear();//new
            ArrayList<Integer> idCosineMember = new ArrayList<>();//new

            if (!tmp.contains(id.get(i))) {

                instancesCosineMember.add(instances_data.get(id.get(i)));
                idCosineMember.add(id.get(i));

                for (int j = i + 1; j < id.size(); j++) {
                    if (!tmp.contains(id.get(j))) {

                        double sim = cosine.distance(instancesCosineMember.get(0), instances_data.get(id.get(j)));
                        
                       // String a[] = "42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,83,90,91,92,93,96,97,98,99,100,101,102,105,106,107,108,109,110,112,113,114,115,116,117,118,119,120,121,122,123,192,223,242,252,287,302,307,331,339,362,366,392,460,466,495,496,498,501,502,503,504,505,506,508,510,511,512,513,514,515,516,517,518,519,520,521".split(",");
                   //if (ArrayUtils.contains(a, id.get(i) +"") || (ArrayUtils.contains(a, id.get(j) +"")))
         
                       // System.out.println(id.get(i) + " dan " + id.get(j) + " = " + sim );

                              double t= (iteration==0)?getThresholdSim():0.3;
                        //double t = (getThresholdSim());
                        if (sim >= t) {
                            instancesCosineMember.add(instances_data.get(id.get(j)));
                            idCosineMember.add(id.get(j));

                        }
                    }
                }

                tmp.addAll(idCosineMember);
                // System.out.println("--> kandidat cosine : " + idCosineMember);
                /*
                 * Asumsi :
                 * cari batas min kumpulan drngan sturges/2
                 */
                int sturges = (int) Math.round(StatisticsNumberCluster.RuleOfSturgesBase10(instances_data.numInstances()));
                sturges = (int) Math.round(sturges/2);
                Euclidean euclidean = new Euclidean();
                //  System.out.println("CosineMedianCentorid Min-sturges :  " + sturges + "<" + idCosineMember.size());

                if (idCosineMember.size() >= sturges) {
                    double[] sortBest = new double[idCosineMember.size()];

                    double[] centroid = AverageCentroid(instancesCosineMember);// mencari rata2 titik pusat

                    for (int best = 0; best < idCosineMember.size(); best++) {
                        sortBest[best] = euclidean.calculate(instances_data.get(idCosineMember.get(best)).toDoubleArray(), centroid);
                    }

                    int idMin = Utils.minIndex(sortBest);
                    idCandidateCentroids.add(idCosineMember.get(idMin));

                } else {
                    if (iteration > 0) {
                        idCandidateCentroids.add(id.get(i));
                    } else {
                        System.out.println("pemetaan cluster (uncluster)\t"+(idCosineMember));
                    }
                    // System.out.println("Masuk  " + id.get(i));
                }
                //System.out.println("");
            }
        }


        return idCandidateCentroids;
    }

    private double[] AverageCentroid(Instances members) {

        double[] vals = new double[members.numAttributes()];
        double[][] nominalDists = new double[members.numAttributes()][];
        double[] weightMissing = new double[members.numAttributes()];
        double[] weightNonMissing = new double[members.numAttributes()];

        // Quickly calculate some relevant statistics 
        for (int j = 0; j < members.numAttributes(); j++) {
            if (members.attribute(j).isNominal()) {
                nominalDists[j] = new double[members.attribute(j).numValues()];
            }
        }
        for (Instance inst : members) {
            for (int j = 0; j < members.numAttributes(); j++) {
                if (inst.isMissing(j)) {
                    weightMissing[j] += inst.weight();
                } else {
                    weightNonMissing[j] += inst.weight();
                    if (members.attribute(j).isNumeric()) {
                        vals[j] += inst.weight() * inst.value(j); // Will be overwritten in Manhattan case
                    } else {
                        nominalDists[j][(int) inst.value(j)] += inst.weight();
                    }
                }
            }
        }
        for (int j = 0; j < members.numAttributes(); j++) {
            if (members.attribute(j).isNumeric()) {
                if (weightNonMissing[j] > 0) {
                    vals[j] /= weightNonMissing[j];
                } else {
                    vals[j] = Utils.missingValue();
                }
            } else {
                double max = -Double.MAX_VALUE;
                double maxIndex = -1;
                for (int i = 0; i < nominalDists[j].length; i++) {
                    if (nominalDists[j][i] > max) {
                        max = nominalDists[j][i];
                        maxIndex = i;
                    }
                    if (max < weightMissing[j]) {
                        vals[j] = Utils.missingValue();
                    } else {
                        vals[j] = maxIndex;
                    }
                }
            }
        }

        return vals;
    }

    private int[] EuclideanLength() {
        /*
         * AKumulasi data menjadi 1 dimensi
         * kode 1--> ambil attribut ke 0
         * kode 2--> hitung forbenius
         */

        //  System.out.println("=== SPLIT INITIAL CENTROID ==== ");         

        double[] dataPcaAxis = instances_data.attributeToDoubleArray(0).clone();
        double[] dataCentroidOrdered = new double[idCentroids.size()];//test2;
        switch (KODE_PROSES) {
            case 0:

                for (int i = 0; i < idCentroids.size(); i++) {
                    dataCentroidOrdered[i] = instances_data.instance(idCentroids.get(i)).value(0);
                }

                break;
            case 1:

                dataPcaAxis = new double[instances_data.numInstances()];
                for (int i = 0; i < instances_data.numInstances(); i++) {
                    double sum = 0;
                    for (int j = 0; j < instances_data.instance(i).toDoubleArray().length; j++) {
                        sum += Math.pow(instances_data.instance(i).toDoubleArray()[j], 2);
                    }

                    dataPcaAxis[i] = Math.sqrt(sum);
                }

                dataCentroidOrdered = new double[idCentroids.size()];
                for (int i = 0; i < idCentroids.size(); i++) {
                    double sum = 0;
                    for (int j = 0; j < instances_data.instance(idCentroids.get(i)).toDoubleArray().length; j++) {
                        sum += Math.pow(instances_data.instance(idCentroids.get(i)).toDoubleArray()[j], 2);
                    }

                    dataCentroidOrdered[i] = Math.sqrt(sum);
                }
                break;

        }

        return Utils.sort(dataCentroidOrdered.clone());

    }

    private int[] Median(int k, int[] idCentOrds) {

        int[] indexCentroid = new int[k];

        flagCalLength = k;

        int lenght = CalculateLenghtMedian(idCentOrds.length, k);
        int flagBegin = 0;

        for (int i = 0; i < k; i++) {

            int idnew = IndexMedian(Arrays.copyOfRange(idCentOrds, flagBegin, (flagBegin + (lenght))));

            flagBegin = flagBegin + lenght;
            // System.out.println("index flasg " + flagCalLength +"  begin-  "+ indexBegin+"  length "+ lenght); 
            if ((i == (k - flagCalLength) - 1)) {
                lenght = CalculateLenghtMedian(idCentOrds.length - (flagBegin), flagCalLength);
            }


            indexCentroid[i] = this.idCentroids.get(idnew);
        }

        return indexCentroid;

    }

    private int IndexMedian(int[] m) {

        int middle = m.length / 2;
        if (m.length % 2 == 1) {
            return m[middle];
        } else {
            /* 
             * given return index which have value max between other
             */
            return m[middle];
        }
    }

    private int CalculateLenghtMedian(int S, int k) {

        int mod = S % k;
        int lenght = S / k;
        if (mod != 0) {
            lenght = ((S - mod) / k) + 1;
            flagCalLength = k - mod;

        }
        return lenght;
    }

    /**
     * @return the numInstance
     */
    public int getNumInstance() {
        return numInstance;
    }

    /**
     * @return the idClear
     */
    public ArrayList<Integer> getIDCentroids() {
        return idCentroids;
    }

    /**
     * @return the thresholdSim
     */
    public double getThresholdSim() {
        return thresholdSim;
    }

    public Map<Integer, int[]> getMapCentroids() {
        return mapCentroids;
    }
}
