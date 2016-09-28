/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trendanalisis.main.tools.weka;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Utils;
import weka.core.stemmers.Stemmer;
import weka.core.tokenizers.Tokenizer;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.Filter;

/**
 *
 * @author asus
 */
public class InitCoreWekaTFIDF {

    private int[] numdocdf;
    private double[] global_tf;
    private double[] df_prob;
    private double[] NormalLenght;
    private int KODE_PROSES = 0;
    private double[] IG;
    private boolean pos_word = false;
    private boolean fitur_ig = false;
    private boolean fitur_subtitle = false;
    private Instances ins_fitur_subtitle;
    private String[] attributName;
    private Map<String, Double> mapTFAndAttribut;

    public Map<String, Double> getMapTFAndAttribut() {
        
        mapTFAndAttribut= new HashMap<>();
        
         attributName = new String[global_tf.length];
        for (int j = 0; j < data.numAttributes(); j++) {
          mapTFAndAttribut.put(data.attribute(j).name(),global_tf[j]);
        }
       
        
        return mapTFAndAttribut;
    }

 
    Instances data;

    public Instances getIns_fitur_subtitle() {
        return ins_fitur_subtitle;
    }

    public void setIns_fitur_subtitle(Instances ins_fitur_subtitle) {
        this.ins_fitur_subtitle = ins_fitur_subtitle;
    }

    public boolean isFitur_ig() {
        return fitur_ig;
    }

    public void setFitur_ig(boolean fitur_ig) {
        this.fitur_ig = fitur_ig;
    }

    public boolean isFitur_subtitle() {
        return fitur_subtitle;
    }

    public void setFitur_subtitle(boolean fitur_subtitle) {
        this.fitur_subtitle = fitur_subtitle;
    }

    public boolean isPos_word() {
        return pos_word;
    }

    public void setPos_word(boolean pos_word) {
        this.pos_word = pos_word;
    }

    /*
     * 0=TF normilize
     * 1=tfidf Normlize
     * 2=tfidf binary
     * 3=tf binary normalize
     * 4=tfidf binary normalize
     */
    public Instances TFIDFProccess(Instances text, int KODE) throws IOException, Exception {
        KODE_PROSES = KODE;
        Instances t = text;
        //    StringToWordVector filter = new StringToWordVector();
        CoreWekaTFIDF filter = new CoreWekaTFIDF();

        switch (KODE_PROSES) {
            /*
             * TF Normalize
             */
            case 0:
                filter.setTFTransform(true);
                filter.setIDFTransform(false);
                filter.setBinary_transform(true);
                filter.setNormalizeDocLength(new SelectedTag(CoreWekaTFIDF.FILTER_NONE, CoreWekaTFIDF.TAGS_FILTER));

                break;
            case 1:
                /*
                 * TF*IDF Normalize
                 */
                filter.setTFTransform(false);
                filter.setBinary_transform(false);
                filter.setIDFTransform(true);
                filter.setNormalizeDocLength(new SelectedTag(CoreWekaTFIDF.FILTER_NORMALIZE_COSTUMIZE, CoreWekaTFIDF.TAGS_FILTER));

                break;

            case 2:
                /*
                 * TF*IDF Binary
                 */
                filter.setTFTransform(false);
                filter.setIDFTransform(true);
                filter.setBinary_transform(true);
                filter.setNormalizeDocLength(new SelectedTag(CoreWekaTFIDF.FILTER_NONE, CoreWekaTFIDF.TAGS_FILTER));

                break;
            case 3:
                /*
                 * TF Binary + Normalize
                 */
                filter.setTFTransform(true);
                filter.setIDFTransform(false);
                filter.setBinary_transform(true);
                filter.setNormalizeDocLength(new SelectedTag(CoreWekaTFIDF.FILTER_NORMALIZE_COSTUMIZE, CoreWekaTFIDF.TAGS_FILTER));


                break;
            case 4:
                /*
                 * TF*IDF Binary + Normalize
                 */
                filter.setTFTransform(false);
                filter.setIDFTransform(true);
                filter.setBinary_transform(true);
                filter.setNormalizeDocLength(new SelectedTag(CoreWekaTFIDF.FILTER_NORMALIZE_COSTUMIZE, CoreWekaTFIDF.TAGS_FILTER));


                break;
            default:

                break;


        }

        filter.setDoNotOperateOnPerClassBasis(true); 
        filter.setStat_pos_word(pos_word);
        filter.setFitur_ig(fitur_ig);
        filter.setFitur_subtitle(fitur_subtitle);
        filter.setIns_fitur_subtitle(ins_fitur_subtitle);

        filter.setOutputWordCounts(true);
        filter.setLowerCaseTokens(true);
        filter.setMinTermFreq(1);

        filter.setStopwordsHandler(new StopwordRecognizer());
        Tokenizer token = new WordTokenizer();
        String[] delimiter = new String[]{"-delimiters", "\"\\r\\n\\t.,;:\\\'\\\"()?!1234567890&#/[]{}"};

        token.setOptions(delimiter);
        filter.setTokenizer(token);

        filter.setInputFormat(t);



        data = Filter.useFilter(t, filter);
        setNumdocdf(filter.GetNumDocAttribut());

        //  double TF[] = new double[data.numAttributes()];
        // TF = filter.GetLabelAttribut();


        // setglobal_tf(filter.GetLabelAttribut());
        setglobal_tf(filter.GetLabelAttribut());
        // System.out.println(data);
        setdf_prob(filter.Getdf_prob());

        setNormalLenght(filter.GetNomalLenght());
        IG = filter.GetIG();

        return data;

    }

    /**
     * @return the numdocdf
     */
    public int[] getNumdocdf() {
        return numdocdf;
    }

    /**
     * @param numdocdf the numdocdf to set
     */
    public void setNumdocdf(int[] numdocdf) {
        this.numdocdf = numdocdf;
    }

    /**
     * @return the global_tf
     */
    public double[] getglobal_tf() {
        return global_tf;
    }

    /**
     * @param global_tf the global_tf to set
     */
    public void setglobal_tf(double[] global_tf) {
        this.global_tf = global_tf;
    }

    /**
     * @return the df_prob
     */
    public double[] getdf_prob() {
        return df_prob;
    }

    /**
     * @param df_prob the df_prob to set
     */
    public void setdf_prob(double[] df_prob) {
        this.df_prob = df_prob;
    }

    /**
     * @return the NormalLenght
     */
    public double[] getNormalLenght() {
        return NormalLenght;
    }

    /**
     * @param NormalLenght the NormalLenght to set
     */
    public void setNormalLenght(double[] NormalLenght) {
        this.NormalLenght = NormalLenght;
    }

    /**
     * @return the IG
     */
    public double[] getIG() {
        return IG;
    }

    /**
     * @param IG the IG to set
     */
    public void setIG(double[] IG) {
        this.IG = IG;
    }

    /**
     * @return the attributName
     */
    public String[] getAttributName() {

        attributName = new String[global_tf.length];
        for (int j = 0; j < data.numAttributes(); j++) {
            attributName[j] = data.attribute(j).name();
        }
        return attributName;
    }

}
