/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trendanalisis.main;

import java.text.ParseException;
import trendanalisis.main.util.FileImportUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.collections15.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.stat.StatUtils;
import trendanalisis.main.document.nlp.PraproccesingText;
import trendanalisis.main.evaluateclustering.PairWiseFmeasure;
import trendanalisis.main.eventdetection.TrendEvent;
import trendanalisis.main.method.CosineMedianCentroid;
import trendanalisis.main.method.FeatureReduction;
import trendanalisis.main.method.HartiganEvaluation;
import trendanalisis.main.method.StatisticsNumberCluster;
import trendanalisis.main.tools.weka.InitCoreWekaPCA;
import trendanalisis.main.tools.weka.InitCoreWekaTFIDF;
import trendanalisis.main.util.WekaUtils;
import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.Instances;
import weka.core.Utils;

/**
 *
 * @author asus
 * memisahkan similarity postagger(Verb) dan NE
 */
public class MainEventTrendingEvaluation {

    /**
     * @param args the command line argumentsx
     */
    // private static String INDEX_DIR = "D:/databerita/ilyas/Data Tes Berita"; 
    // private static String INDEX_DIR = "D:/databerita/Hasil Filter/DoniTest"; 
    private static String OUTPUT_CLUSTER_DIR = "output_cluster";
    private static DistanceFunction DF_FUNCTION = null;
    private static final int kodeDir = 774;
    private static boolean SAVE_OUT_CLUSTER = false;
    private static boolean BASELINE = false;
    /*
     * setting menentukan jumlah k secara manual
     * gold standart !=-1
     */
    private static int k_gold_standart = -1;
    public static final String PATH_TARGET_EVALUATION = "data/" + kodeDir + ".txt";
    private static String INDEX_DIR = "data/berita/" + kodeDir;
    // private static String INDEX_DIR = "data/berita/q";
    //   private static String INDEX_DIR = "D:/databerita/Hasil Filter/Tanggal";
    /*
     * Setting TC (Reduksi Kata)
     * baseline---> TC_BASELINE
     * TC_MIN/TC MAX range 0-95, iterator 80 atau 95
     * new---> TC_NEW
     * TC_MIN/TC MAX range 0.0-0.5  
     * TC_iterator-->0.1    
     */
    private static final FeatureReduction.MethodFitur methodFitur = FeatureReduction.MethodFitur.TC_NEW_DF ;
    private static final double tc_min = 0.2;
    private static final double tc_max = 0.2;
    private static final double tc_iterator = 0.1;
    /*
     * Setting Bobot 
     * all false---> Baseline
     * new ----> false,false, true,true
     */
    private static boolean ig_1 = false;
    private static boolean title_1 = false;
    private static boolean ig_2 = false;
    private static boolean title_2 = true;
    /*
     * Setting PCA
     */
    private static final double pca_sumEigen_min = 0.03;
    private static final double pca_sumEigen_max = 0.03;//0.07
    private static final double pca_sumVar_max = 0.95;
    private static final boolean pca_statKaiser = false;
    private static final boolean pca_statVarimax = false;
    // private static final int pca_count_att_min = 7;
    /*
     * Setting Initial Centroid evaluation
     * baseline--> centroid_cosine_min =centroid_cosine_max>1.0;
     * new --> centroid_cosine_min =centroid_cosine_max=0.5;
     */
    private static double centroid_cosine_min = 0.5;
    private static double centroid_cosine_max = 0.5;
    //--------------------------------------------------------
    private static Map<String, String> documents = null;
    private static Instances instance_Preprocessing_Content;
    private static Instances instance_Preprocessing_Tittle;
    private static Map<Double, Instances> map_instance_output_tc;
    private static Map<Double, Instances> map_instance_output_pca;
    private static Map<Integer, int[]> map_evaluation_centroid;
    private static Map<String, Double> map_tf_attributname;
    private static ArrayList<String> outprint_resume_experiment = new ArrayList<>();
    private static ArrayList<String> out_print_trend_cluster;
    private static String out_print_tc = "";
    private static String out_print_pca = "";
    private static Map<String, Double> map_new_method;
    private static ArrayList<String> out_print_internal_measure;
    private static StringBuffer buffer_out_print;
    //private static double average_precision;
    //private static double average_recall;
    // private static double average_fmeasure;
    private static double[][] average_prec_rec_fmeasure;
    //  private static Map<String, String> map_out_print_tc;
    // private static Map<String, String> map_out_print_pca;

    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {

        /*
         * Set Parameter Uji
         */
        //double VARIANCE_PCA = pca_sumEigen_max; //Default PCA weka
        // double THRESHOLD_CLEAR = 2; // clear ambigus doc
        int PERSENT_LENGHT_DOC = PraproccesingText.PERSENT_LENGTH_DOC_100;
        DF_FUNCTION = new EuclideanDistance();

        File f = new File(INDEX_DIR);
        documents = FileImportUtils.getFileRecursivetoMapString(f);

        buffer_out_print = new StringBuffer();
        map_new_method = new HashMap<>();
        out_print_internal_measure = new ArrayList<>();
        out_print_trend_cluster = new ArrayList<>();


        System.out.println("\n=======.START PROCESS ======= " + methodFitur.name());
        System.out.println("========>>>>> PROSES PRE-PROCESSING <<<<======== ");

        PreprocessDocument(documents, PraproccesingText.COMBINE_PARSE_AND_VERB_NOUN, PERSENT_LENGHT_DOC);

        buffer_out_print.append("\n========>>>>> PROSES TC (Reduksi kata) <<<<======== ");

        SelectionTermContribution();

        //set array average for f-measure
        average_prec_rec_fmeasure = new double[4][map_instance_output_tc.keySet().size() * (int) ((pca_sumEigen_max - pca_sumEigen_min) + 1)];
        int averageCount = 0;

        Object[] objs = map_instance_output_tc.keySet().toArray();
        Arrays.sort(objs);

        for (Object tTC : objs) {// ulangi berdsrkan evaluasi term contribusi (0.0, 0.1..)

            buffer_out_print.append("\n\n----->> Evaluasi TC " + tTC);

            Instances instanceTermWeight = TermWeighting(map_instance_output_tc.get(tTC));

            buffer_out_print.append("\n========>>>>> PCA  <<<<========");

            PrincipalComponent(instanceTermWeight, Double.parseDouble(tTC.toString()));

            for (Double tPCA : map_instance_output_pca.keySet()) {// ulangi berdsrkan evaluasi sum eigen value ( 0.04, 0.05...)

                buffer_out_print.append("\n----->> Evaluasi PCA : " + tPCA);


                for (double tp = centroid_cosine_min; tp <= centroid_cosine_max; tp += 0.1) {// ulangi initial centroid berdsarkan evaluasi niali cosinus yang dipilih ( 20%, 50%...)


                    buffer_out_print.append("\n========>>>>> INITIAL CENTROID <<<<========");



                    if (BASELINE) {
                        map_evaluation_centroid = new HashMap<>();
                        map_evaluation_centroid.put(k_gold_standart, null);
                        map_evaluation_centroid.put(k_gold_standart - 1, null);
                        map_evaluation_centroid.put(k_gold_standart - 2, null);
                        map_evaluation_centroid.put(k_gold_standart - 3, null);

                    } else {

                        map_evaluation_centroid = InitialCentroid(map_instance_output_pca.get(tPCA), tp);
                    }



                    buffer_out_print.append("\n========>>>>> HARTIGAN INDEX <<<<========");
                    ArrayList< Map<Integer, ArrayList<Integer>>> listClusterHartigan = null;


                    HartiganEvaluation hartg = new HartiganEvaluation();
                    hartg.setInstances_data(map_instance_output_pca.get(tPCA));
                    hartg.setMap_evaluation_centroid(map_evaluation_centroid);
                    hartg.Evaluation();

                    listClusterHartigan =
                            hartg.getListClusterEvaluation();//return hartigan


                    int K = (k_gold_standart != -1) ? k_gold_standart : hartg.getBestK();
                    int idxBestK = hartg.getIndex(K);

                    if (idxBestK == -1 || listClusterHartigan.size() <= idxBestK) {
                        continue;
                    }

//                    if (BASELINE) {
//                        InitCoreWekaKmeans kmeans = new InitCoreWekaKmeans();
//                        Map<Integer, ArrayList<Integer>> mapClusters = kmeans.KMeansWeka(map_instance_output_pca.get(tPCA), k_gold_standart, null,
//                                new EuclideanDistance());
//                    }


                    /*  Trending event 
                     *  ektraksi kata kunci
                     *  pemilihan cluster Importance
                     */
                    TrendEvent trend = new TrendEvent();
                    trend.Build(listClusterHartigan.get(idxBestK),
                            map_instance_output_tc.get(tTC), map_tf_attributname);

                    String docLabel = trend.getOutPrint();

                    /*
                     * Save cluster event
                     */
                    if (SAVE_OUT_CLUSTER) {
                        FileImportUtils.ToFolderCluster(OUTPUT_CLUSTER_DIR, hartg.getListClusterEvaluation().get(idxBestK),
                                hartg.getErrorSSE().get(idxBestK), trend.getMapKeyWord(), String.format("TC(%.3f) SumEigen(%.3f)", tTC, 0.0), 0, documents);
                    }


                    buffer_out_print.append(hartg.getOutPrint());

                    // System.out.println(hartg.getOutPrint());
                    buffer_out_print.append("\n========>>>>> Evaluation Akurasi K-means at Hartigan (TC :" + tTC + " PCA :" + tPCA + ") <<<<========");

                    double[][] fmeasures = EvaluationFMeasureClusterHartigan(hartg.getFirstIndex(),
                            listClusterHartigan);

                    /*
                     * Set untuk resume experiment
                     * TC PCA PUSAT BEST %AKUR HARTG %AKUR 
                     * ------------------------------------------------------------------------------------------------
                     */


                    int idxMaxfmeasure = Utils.maxIndex(fmeasures[2]);//[2] f-measure

                    String outPrintresume = String.format("%.3f\t%.3f\t%.3f\t%d\t%.3f\t%d\t%.3f\t%.3f\t%.3f\t%.3f", tTC, tPCA, tp,
                            (hartg.getFirstIndex() + idxMaxfmeasure), fmeasures[2][idxMaxfmeasure], K, fmeasures[3][idxBestK],
                            fmeasures[0][idxBestK], fmeasures[1][idxBestK], fmeasures[2][idxBestK]);


                    average_prec_rec_fmeasure[0][averageCount] = fmeasures[0][idxBestK];
                    average_prec_rec_fmeasure[1][averageCount] = fmeasures[1][idxBestK];
                    average_prec_rec_fmeasure[2][averageCount] = fmeasures[2][idxBestK];
                    average_prec_rec_fmeasure[3][averageCount] = fmeasures[3][idxBestK];
                    averageCount++;

                    outprint_resume_experiment.add(outPrintresume);
                    map_new_method.put(outPrintresume, hartg.getNewMethod());

//                    outPrintresume = String.format("%.f\t%.3f\t%.3f\t%.3f\t%.3f", tTC, tPCA, tp, fmeasures[3][idxBestK], fmeasures[2][idxBestK]);
//                    out_print_internal_measure.add(outPrintresume + "\t" + hartg.getListoutPrintInternalMeasure().get(idxBestK));

                    outPrintresume = String.format("%.1f\t\t%.3f\t%.3f", tTC, fmeasures[3][idxBestK], fmeasures[2][idxBestK]);
                    out_print_internal_measure.add(outPrintresume);

                    
                    StringBuffer out = new StringBuffer();
                    out.append(hartg.getOutPrintBestHatgKOnly());
                    out.append(docLabel);
                    out_print_trend_cluster.add(outPrintresume + "\t" + out.toString());
                    /*
                     * --------------------------------------------------------------------------
                     */

                    // Logger.getLogger(MainEventTrendingEvaluation.class).log(Level.INFO, (buffer_out_print.toString()));
                    System.out.println(buffer_out_print.toString());
                    buffer_out_print = new StringBuffer();
                }

            }

        }


        OutPrintResumeExperiment();
    }

    private static void PreprocessDocument(Map<String, String> documents, int tipe, int lenghtDoc) {

        /*
         * proses pre processing
         * nilai yang diambil dari ini adalah 2 jenis :
         * 1. instance content dokumen
         * 2. instance judul(judul,lokasi dan waktu)
         */

        try {

            PraproccesingText vectorGenerator = null;

            String namafilePraproccess = "Berita_PRA" + documents.keySet().size() + "_" + ".arff";
            String namafilePraproccessLoctime = "Berita_PRA_LOCTIME" + documents.keySet().size() + "_" + ".arff";

            if (!FileImportUtils.CekFile(new File("data/iris"), namafilePraproccess)
                    || !FileImportUtils.CekFile(new File("data/iris"), namafilePraproccessLoctime)) {
                try {
                    vectorGenerator = new PraproccesingText(true, 0);
                    try {
                        vectorGenerator.BuildWordWithDocument(documents, tipe, lenghtDoc);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    instance_Preprocessing_Content = vectorGenerator.GetContentInsatences();
                    instance_Preprocessing_Tittle = vectorGenerator.GetTitleLocTimeInsatences();

                    /*
                     * simpan hasil preprocessing
                     */
                    WekaUtils.SaveArff(new File("data/iris/" + namafilePraproccess), instance_Preprocessing_Content);
                    WekaUtils.SaveArff(new File("data/iris/" + namafilePraproccessLoctime), instance_Preprocessing_Tittle);

                } catch (ParseException ex) {
                    ex.printStackTrace();
                }


            } else {

                instance_Preprocessing_Content = WekaUtils.ReadfileArff("data/iris/" + namafilePraproccess);
                instance_Preprocessing_Tittle = WekaUtils.ReadfileArff("data/iris/" + namafilePraproccessLoctime);

                //System.out.println("WEKA CONVERT-CONTENT");
                //  System.out.println(instance_Preprocessing_Content);

                //System.out.println("WEKA CONVERT-TITTLE");
                //instance_Preprocessing_Tittle.deleteAttributeAt(1);
                // instance_Preprocessing_Tittle.deleteAttributeAt(1);
                //  System.out.println(instance_Preprocessing_Tittle);

            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }


    }

    private static void SelectionTermContribution() {

        /*
         * Term Contribution * new method (log(TF)/df)
         * seleksi berdsarkan nilai df probablity >=1
         * 
         */
        String outPrint = "";
        map_instance_output_tc = new HashMap<>();
        FeatureReduction tc = null;

        try {

            InitCoreWekaTFIDF wekaTfidf = new InitCoreWekaTFIDF();

            wekaTfidf.setFitur_ig(ig_1);
            wekaTfidf.setFitur_subtitle(title_1);
            wekaTfidf.setIns_fitur_subtitle(instance_Preprocessing_Tittle);

            Instances instfidf = wekaTfidf.TFIDFProccess(instance_Preprocessing_Content, 1);

            String namafileTfidf = "Berita_TFIDF_" + instance_Preprocessing_Content.numInstances()
                    + "_" + ".arff";
//            WekaUtils.SaveCSV(new File("data/iris/" + namafileTfidf), new Instances(instfidf));

            tc = new FeatureReduction(instfidf, wekaTfidf.getNumdocdf(),
                    wekaTfidf.getglobal_tf(),
                    wekaTfidf.getdf_prob(), methodFitur);

            tc.TCRank();

            /*
             * Ambil kemungkinan terbaik dari hasil selekci fitur
             * 
             */

            for (double t = tc_min; t <= tc_max; t += tc_iterator) {

                Instances newTCInstance = tc.GetNewDataset(t, instance_Preprocessing_Content);

                if (newTCInstance == null) {
                    outPrint += (t + " ---> TERM CONTRIBUTION KOSONG !!! -> Reduksi Kata Terlalu besar dan terdapat dokumen hanya mewakili kata <=1\n");

                    continue;
                }

                map_instance_output_tc.put(t, newTCInstance);


            }



            outPrint += "\n----->Info Reduksi Kata";
            outPrint += "\n\t" + tc.getHeadOutPrint();
            outPrint += tc.getOutPrint();

            out_print_tc = (tc.getOutPrint());
            // map_out_print_tc=tc.getMap_outPrintforResume();

            buffer_out_print.append(outPrint);
            //System.out.println(outPrint);
            //System.out.println(out_print_tc);


        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }




    }

    private static Instances TermWeighting(Instances instancesFiturSelection) throws IOException, Exception {

        /*
         * pembobotan kata dengan TFIDF + IG + Judul
         */

        String outPrint = "";
        InitCoreWekaTFIDF wekaTfidf = new InitCoreWekaTFIDF();

        outPrint += ("\n----->> Pembobotan Kata (TFIDF + Resembalance to the tittle)");
        String namafileTfidf = "Berita_TFIDF_" + instance_Preprocessing_Content.numInstances()
                + "_" + ".arff";

        wekaTfidf = new InitCoreWekaTFIDF();
        wekaTfidf.setFitur_ig(ig_2);// jgn di matikan
        wekaTfidf.setFitur_subtitle(title_2);
        wekaTfidf.setIns_fitur_subtitle(instance_Preprocessing_Tittle);

        // WekaUtils.SaveArff(new File("data/iris/" + namafileTfidf), new Instances(wekaTfidf));

        Instances tfidf = wekaTfidf.TFIDFProccess(instancesFiturSelection, 1);
        map_tf_attributname = wekaTfidf.getMapTFAndAttribut();

        buffer_out_print.append(outPrint);
        //System.out.println(outPrint);
        return tfidf;

    }

    private static void PrincipalComponent(Instances instancesTfidf, double tc) {

        /*
         * return dari PCA berupa  'map_instance_output_pca 'Instances yang akan di periksa satu2
         * PCA dengan nilai eigen value mana yang terbaik akurasinya
         * MIN_THRESHOLD_PCA dan MAX_THRESHOLD_PCA batasan eigen value
         */
        StringBuffer outPrint = new StringBuffer();

        map_instance_output_pca = new HashMap<>();
        int numSturges = (int) (Math.round(StatisticsNumberCluster.RuleOfSturgesBase10(instancesTfidf.numInstances())));

        outPrint.append("\n\tBatas min jumlah attribut PCA (numSturges10) :" + numSturges);

        double MIN_THRESHOLD_PCA = pca_sumEigen_min;
        double MAX_THRESHOLD_PCA = pca_sumEigen_max;

        if (pca_statKaiser) {
            MIN_THRESHOLD_PCA = 1;
            MAX_THRESHOLD_PCA = 1;
        }

        InitCoreWekaPCA wekaPca = new InitCoreWekaPCA();
        wekaPca.setAttCountMin(numSturges);
        wekaPca.setStatVarimax(pca_statVarimax);
        wekaPca.setStatKaiserMayer(pca_statKaiser);
        wekaPca.setThresholdPCA(MAX_THRESHOLD_PCA);
        Instances instancesPCA = wekaPca.RunPCA(instancesTfidf, pca_sumVar_max);

        outPrint.append("\n\t" + wekaPca.getHeaderInfoPCA());

        if (pca_statKaiser) {
            map_instance_output_pca.put(MIN_THRESHOLD_PCA, instancesPCA);
            outPrint.append("\n\t Kaiser Mayer");
        } else {

            for (double t = MIN_THRESHOLD_PCA; t <= MAX_THRESHOLD_PCA; t += 0.01) {

                instancesPCA = null;

                // instancesPCA = wekaPca.getTransformed(t);
                instancesPCA = wekaPca.getVaribelPCAByZScore();

                outPrint.append("\n\t" + wekaPca.getInfoPCA());


                if (instancesPCA == null) {
                    outPrint.append(" --->> PCA TIDAK TERDEFINISI EIGEN !!! Jumlah attribut < "
                            + numSturges + " atau  cumulative(%) > " + (pca_sumVar_max - 1));


                } else {

                    map_instance_output_pca.put(t, instancesPCA);

                    // String kode=tc+","+t;                  
                    // map_out_print_pca.put(kode, wekaPca.getInfoPCA());

                    out_print_pca += "\n\t" + tc + "\t" + wekaPca.getInfoPCA();

                }

            }

        }


        buffer_out_print.append(outPrint);
        //System.out.println(outPrint);

        MapUtils.orderedMap(map_instance_output_pca);
    }

    private static Map<Integer, int[]> InitialCentroid(Instances instancesPCA, double threshold) {

        /*
         * menggunakan cosine similarity--> titik pusat ditentukan dari jarak terdeket dari rata2 kelompok cosine
         * median--> unutk evaluasi jumlah K (cluster)
         */

        String outPrint = "";
        outPrint = ("\n----->> Initial Centroid With Cosine and Median");


        CosineMedianCentroid centroid = new CosineMedianCentroid(threshold);
        centroid.setKODE_PROSES(1);
        centroid.Build(instancesPCA);

        outPrint += centroid.getOutPrint();

        buffer_out_print.append(outPrint);
        //System.out.println(outPrint);
        return centroid.getMapCentroids();

    }

    private static double[][] EvaluationFMeasureClusterHartigan(int firstIndexK,
            ArrayList< Map<Integer, ArrayList<Integer>>> lEvaluationClusterHartigan) {
        /*
         * Ukuran akurasi mengguna kan precision, recall dan f-measure
         * [0] precision
         * [1] recall
         * [2] fmeasure
         * [2] purity
         */
        String outPrint = "";

        double[][] fmeasures = new double[4][lEvaluationClusterHartigan.size()];
        outPrint = (("\n\tK\tpurity\tPreci\tRecall\t f-masure"));

        PairWiseFmeasure measure = new PairWiseFmeasure(PATH_TARGET_EVALUATION);

        for (int k = 0; k < lEvaluationClusterHartigan.size(); k++) {

            double fMeasure = measure.ProccessEvaluation(lEvaluationClusterHartigan.get(k));
            double purity = measure.ProccessEvaluationPurity(lEvaluationClusterHartigan.get(k), documents.size());
            // System.out.println("aa " +purity +" " + documents.size());
            fmeasures[0][k] = measure.getPrecision();
            fmeasures[1][k] = measure.getRecall();
            fmeasures[2][k] = fMeasure;
            fmeasures[3][k] = purity;

            outPrint += (String.format("\n\t%d\t%.3f\t%.3f\t%.3f\t%.3f", firstIndexK + k, purity, measure.getPrecision(), measure.getRecall(), fMeasure));

        }

        buffer_out_print.append(outPrint);
        //System.out.println(outPrint);

        return fmeasures;
    }

    private static void OutPrintResumeExperiment() {
        /*
         * Resume Experimen
         */
        StringBuffer out = new StringBuffer();
        Map<String, Double> mapRekomendasi = RekomendasiHasilExperiment();

        for (int i = average_prec_rec_fmeasure[2].length - 1; i >= 0; i--) {

            if (average_prec_rec_fmeasure[2][i] <= 0) {
                ArrayUtils.remove(average_prec_rec_fmeasure[0], i);
                ArrayUtils.remove(average_prec_rec_fmeasure[1], i);
                ArrayUtils.remove(average_prec_rec_fmeasure[2], i);
                ArrayUtils.remove(average_prec_rec_fmeasure[3], i);
            }
        }

        out.append("\n======= >>>>> EKTRAKSI KATA KUNCI SEBAGAI LABEL TREND EVENT <<<<<========");
        out.append("\nJumlah Experiment yang sudah terseleksi sejumlah : " + out_print_trend_cluster.size());
        out.append("\n");

        Iterator<String> it = out_print_trend_cluster.iterator();
        for (; it.hasNext();) {
            String name = it.next();
            out.append("\nTC\tPCA\tPUSAT\t%AKUR");
            out.append("\n" + name);

            out.append("\n==========================================================================");
        }

        out.append("\n\n======= >>>>> RESUME EXPERIMENT <<<<<========");
        out.append("\n-->Info Reduksi Kata");
        out.append("\n\tTC\tWord\tAlWord \t %");
        out.append(out_print_tc);
        out.append("\n----------------------------------------------");
        out.append("\n-->Info PCA");
        out.append("\n\tTC\tTresh\tN_att\tS_Alleig\tS_Selecteig\t%Cumulative ");
        out.append(out_print_pca);
        out.append("\n----------------------------------------------");
        out.append("\n-->Info Pengukuran Internal Cluster");
        it = out_print_internal_measure.iterator();
        //out.append("\n\tTC\tPCA\tPUSAT\t%AKUR\tK\tHART\tFRAT\tWB-IND\tSSW\tSSB\tSST\tSST+SSB-Ratio\tsilhouet");
        //out.append("\n\tTC\tPCA\tPUSAT\tPURITY\t%FMEA\tK\tSSE\tIteration");
         out.append("\nthreshold\t\tPurity\tF-measure");
        for (; it.hasNext();) {
            String name = it.next();
            out.append("\n\t" + name + "\t");
        }
        out.append("\n----------------------------------------------");
        out.append("\n-->Info K-means");

        out.append("\n\tTC\tPCA\tPUSAT\tBEST(K)\t%f-mea\tHART(K)\tPURITY\t%Prec\t%Rec\t%f-mea");
        int count = 0;

        double ZscoreTot[][] = new double[5][outprint_resume_experiment.size()];
        for (String outPrint : outprint_resume_experiment) {
            out.append("\n\t" + outPrint);
            if (mapRekomendasi.get(outPrint) != null) {
                out.append(" <<-- Rekomendasi dari fungsi ((F-Ratio-(SST+SSE))/N )*(K+1) = " + mapRekomendasi.get(outPrint).doubleValue());
            }
            ZscoreTot[0][count] = (average_prec_rec_fmeasure[0][count] - Utils.mean(average_prec_rec_fmeasure[0])) / Math.sqrt(StatUtils.variance(average_prec_rec_fmeasure[0]));
            ZscoreTot[1][count] = (average_prec_rec_fmeasure[1][count] - Utils.mean(average_prec_rec_fmeasure[1])) / Math.sqrt(StatUtils.variance(average_prec_rec_fmeasure[1]));
            ZscoreTot[2][count] = (average_prec_rec_fmeasure[2][count] - Utils.mean(average_prec_rec_fmeasure[2])) / Math.sqrt(StatUtils.variance(average_prec_rec_fmeasure[2]));
            ZscoreTot[3][count] = (average_prec_rec_fmeasure[3][count] - Utils.mean(average_prec_rec_fmeasure[3])) / Math.sqrt(StatUtils.variance(average_prec_rec_fmeasure[3]));
            ZscoreTot[4][count] = ZscoreTot[0][count] + ZscoreTot[1][count] + ZscoreTot[2][count] + ZscoreTot[3][count];

            count++;
        }
        out.append("\n\t\t\t\t\t------------------------------------------");
        out.append("\n\t\t\t\t\tAverage ==>>");
        out.append(String.format("\t%.3f\t%.3f\t%.3f\t%.3f",
                Utils.mean(average_prec_rec_fmeasure[3]),
                Utils.mean(average_prec_rec_fmeasure[0]),
                Utils.mean(average_prec_rec_fmeasure[1]),
                Utils.mean(average_prec_rec_fmeasure[2])));
        out.append("\n\t\t\t\t\tMin ==>>");
        out.append(String.format("\t%.3f\t%.3f\t%.3f\t%.3f",
                StatUtils.min(average_prec_rec_fmeasure[3]),
                StatUtils.min(average_prec_rec_fmeasure[0]),
                StatUtils.min(average_prec_rec_fmeasure[1]),
                StatUtils.min(average_prec_rec_fmeasure[2])));
        out.append("\n\t\t\t\t\tMax ==>>");
        out.append(String.format("\t%.3f\t%.3f\t%.3f\t%.3f",
                StatUtils.max(average_prec_rec_fmeasure[3]),
                StatUtils.max(average_prec_rec_fmeasure[0]),
                StatUtils.max(average_prec_rec_fmeasure[1]),
                StatUtils.max(average_prec_rec_fmeasure[2])));
        out.append("\n\t\t\t\t\tstdv ==>>");
        out.append(String.format("\t%.3f\t%.3f\t%.3f\t%.3f",
                Math.sqrt(StatUtils.variance(average_prec_rec_fmeasure[3])),
                Math.sqrt(StatUtils.variance(average_prec_rec_fmeasure[0])),
                Math.sqrt(StatUtils.variance(average_prec_rec_fmeasure[1])),
                Math.sqrt(StatUtils.variance(average_prec_rec_fmeasure[2]))));
        out.append("\n\t\t\t\t\t------------------------------------------");

        out.append("\n\tTC\tPCA\tPUSAT\tBEST(K)\t%f-mea\tHART(K)\tPURITY\t%Prec\t%Rec\t%f-mea\tz-purt\tz-Prec\tz-Rec\tz-fmea\tz-tot");
        count = 0;
        for (String outPrint : outprint_resume_experiment) {


            if (Utils.maxIndex(ZscoreTot[4]) == count) {
                out.append("\n---------------------------------------------------------------------------------------------------------------------------------------------------------");
                out.append("\n\t" + outPrint);
                out.append(String.format("\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f",
                        ZscoreTot[3][count], ZscoreTot[0][count], ZscoreTot[1][count], ZscoreTot[2][count], ZscoreTot[4][count]));
                out.append(" <<-- max value");
                out.append("\n---------------------------------------------------------------------------------------------------------------------------------------------------------");
            } else {
                out.append("\n\t" + outPrint);
                out.append(String.format("\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f",
                        ZscoreTot[3][count], ZscoreTot[0][count], ZscoreTot[1][count], ZscoreTot[2][count], ZscoreTot[4][count]));
            }
            count++;

        }



        //System.out.println(out.toString());

        // Logger.getLogger(MainEventTrendingEvaluation.class).log(Level.INFO, (out.toString()));;
        System.out.println(out.toString());
    }

    private static Map<String, Double> RekomendasiHasilExperiment() {
        /*
         * fungsi tambaan untuk memilih kelompok optimal dari setiap evaluasi
         * menggunakan  fungsi ((F-Ratio-(SST+SSE))/N )*(K+1)) 
         */

        double probTCMax = Collections.max((map_new_method.values()));
        double probTCMin = Collections.min((map_new_method.values()));
        String idTCOPMin = "", idTcOPMax = "";

        for (String tcOP : map_new_method.keySet()) {

            if (map_new_method.get(tcOP) <= 0) {

                if (map_new_method.get(tcOP) >= probTCMin) {
                    probTCMin = map_new_method.get(tcOP);
                    idTCOPMin = tcOP;
                }

            } else {
                if (map_new_method.get(tcOP) <= probTCMax) {
                    probTCMax = map_new_method.get(tcOP);
                    idTcOPMax = tcOP;
                }

            }
        }

        Map<String, Double> mapRekomedasi = new HashMap<>();
        mapRekomedasi.put(idTcOPMax, probTCMax);
        mapRekomedasi.put(idTCOPMin, probTCMin);
        return mapRekomedasi;


    }
}
