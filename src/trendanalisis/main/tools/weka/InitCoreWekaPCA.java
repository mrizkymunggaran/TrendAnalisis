/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trendanalisis.main.tools.weka;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.stat.StatUtils;
import trendanalisis.main.method.FeatureSelection;
import weka.core.Instances;
import weka.core.Utils;

/**
 *
 * @author asus
 */
public class InitCoreWekaPCA {

    private double[][] rankAttribut = null;
    private String infoPCA;
    private double thresholdPCA = 0.1;
    private int attCountMin = 3;
    private int attCountMax = 20;
    private boolean statKaiserMayer = false;
    private boolean statVarimax = false;
    private Instances instance = null;
    private double[] eigenValue = null;
    private double variaceMax = 0;
    private double varianceMin = 0.4;
    private String headerInfoPCA;

    public Instances getInstance() {
        return instance;
    }

    public void setInstance(Instances instance) {
        this.instance = instance;
    }

    public Instances RunPCA(Instances data, double varianceMax) {
        this.variaceMax = varianceMax;

        try {

            CoreWekaPrincipalComponents pca = new CoreWekaPrincipalComponents();
            pca.setVarianceCovered(varianceMax);
            
            pca.setCenterData(true);
            pca.setBol_varimax(statVarimax);

            if (statKaiserMayer) {
                pca.setCenterData(false);
                pca.setKaiserMayer(statKaiserMayer);
                pca.setNum_kaiserMayer(thresholdPCA);

            }

            //  pca.setM_thresholdSumEigen(thresholdPCA);

            // Sets maximum number of attributes to include in transformed attribute
            // names.selector
            pca.setMaximumAttributeNames(5);
            pca.buildEvaluator(data);
           
            instance = pca.transformedData(data);//selector.reduceDimensionality(data);
             

           // System.out.println("=== PCA ORIGINAL ===");
        //   System.out.println(pca.toString());

            /*
             * Begin Algoritma
             * GET PCA which have SUM of Eigen value >0.1
             */

            eigenValue = pca.getEigenValues().clone();
            ArrayUtils.reverse(eigenValue);


        } catch (Exception ex) {
            Logger.getLogger(InitCoreWekaPCA.class.getName()).log(Level.SEVERE, null, ex);
        }

        return instance;
    }

    public Instances getTransformed(double threshold) {

        double cumulative = 0;

        Instances inst = new Instances(instance);
        cumulative = StatUtils.sum(eigenValue, 0, instance.numAttributes());
        int i = instance.numAttributes() - 1;

        while ((StatUtils.sum(eigenValue, 0, i) >= threshold)) {
            inst.deleteAttributeAt(i);
            i--;

        }

        cumulative = StatUtils.sum(eigenValue, 0, inst.numAttributes());
        /*
         * validasi attribut transformasi
         */
        double sumOfEigen = Utils.sum(eigenValue);

        infoPCA = (String.format("%.2f\t%d\t%.2f\t%.2f\t%.2f", threshold, inst.numAttributes(), sumOfEigen, cumulative, cumulative / sumOfEigen));
        // System.out.println(getHeaderInfoPCA() + "\n" + getInfoPCA());

        if (!statKaiserMayer) {
            if ((cumulative / sumOfEigen > variaceMax)
                    || ((cumulative / sumOfEigen) < varianceMin) //  ||   (transformedData.numAttributes()<attCountMin)
                    ) {
                return null;
            }
        }
        if (inst.numAttributes() < attCountMin) {
            return null;
        }


        return inst;
    }

    public Instances getVaribelPCAByZScore() {


        double cumulative = 0;
        double threshold = 1.0;
        Instances inst = new Instances(instance);
        cumulative = StatUtils.sum(eigenValue, 0, instance.numAttributes());
        //    eigenValue= ArrayUtils.subarray(eigenValue, 0,instance.numAttributes()+1);
        double mean = Utils.mean(eigenValue);
        double stdv = Math.sqrt(Utils.variance(eigenValue));
         double sumOfEigen = Utils.sum(eigenValue);

        /*
         * Z-score >1
         */
//        int i=1;
//         System.out.println("========>>>>> ILUSTRASI PCA  <<<<========");
//          System.out.println("Rank\teigenvalue\tZscore\tcumulative(%)");
//        for (int j = 0; j <= instance.numAttributes() - 1; j++) {
//            double z = (double) ((eigenValue[j] - mean) / stdv);
//            cumulative = StatUtils.sum(eigenValue, 0, i)/sumOfEigen;
//            
//            System.out.print((String.format("%d\t%.5f\t\t%.2f\t%.2f", i, (eigenValue[j]), z,cumulative)));
//            if (z < threshold) {
//                 System.out.print("<-- clear");
//            }
//             System.out.println("");
//             i++;
//        }
//        
        
        for (int j = instance.numAttributes() - 1; j >= 0; j--) {
            double z = (double) ((eigenValue[j] - mean) / stdv);
            
            if (z < threshold) {
                inst.deleteAttributeAt(j);
            }

        }
       inst.deleteAttributeAt(inst.numAttributes() - 1);
       
//        for (int j = instance.numAttributes() - 1; j >= 0; j--) {
//            double z = (double) ((eigenValue[j] - mean) / stdv);
//            
//            if (inst.numAttributes() >10) {
//                inst.deleteAttributeAt(j);
//            }else{break;}
//
//        }
//     
       
       
        cumulative = StatUtils.sum(eigenValue, 0, inst.numAttributes());
        /*
         * validasi attribut transformasi
         */
   
        infoPCA = (String.format("%.2f\t%d\t%.2f\t\t%.2f\t\t%.2f", threshold, inst.numAttributes(), sumOfEigen, cumulative, cumulative / sumOfEigen));

//        System.out.println("=== PC REDUCTION ===");
//        System.out.println(pca.toString());

        return inst;
    }

    /**
     * @return the outPrint
     */
    public String getHeaderInfoPCA() {
        headerInfoPCA = "Tresh\tN-att\tS_Alleig\tS_Selecteig\t%Cumulative ";
        return headerInfoPCA;
    }

    public double[][] getRankAttribut() {

        return rankAttribut;
    }

    /**
     * @return the infoPCA
     */
    public String getInfoPCA() {
        return infoPCA;
    }

    /**
     * @param infoPCA the infoPCA to set
     */
    public void setInfoPCA(String infoPCA) {
        this.infoPCA = infoPCA;
    }

    /**
     * @return the thresholdPCA
     */
    public double getThresholdPCA() {
        return thresholdPCA;
    }

    /**
     * @param thresholdPCA the thresholdPCA to set
     */
    public void setThresholdPCA(double thresholdPCA) {
        this.thresholdPCA = thresholdPCA;
    }

    /**
     * @return the attCountMin
     */
    public int getAttCountMin() {
        return attCountMin;
    }

    /**
     * @return the attCountMax
     */
    public int getAttCountMax() {
        return attCountMax;
    }

    /**
     * @return the statVarimax
     */
    public boolean isStatVarimax() {
        return statVarimax;
    }

    /**
     * @param statVarimax the statVarimax to set
     */
    public void setStatVarimax(boolean statVarimax) {
        this.statVarimax = statVarimax;
    }

    /**
     * @return the statKaiserMayer
     */
    public boolean isStatKaiserMayer() {
        return statKaiserMayer;
    }

    /**
     * @param statKaiserMayer the statKaiserMayer to set
     */
    public void setStatKaiserMayer(boolean statKaiserMayer) {
        this.statKaiserMayer = statKaiserMayer;
    }

    /**
     * @param attCountMin the attCountMin to set
     */
    public void setAttCountMin(int attCountMin) {
        this.attCountMin = attCountMin;
    }

    /**
     * @param attCountMax the attCountMax to set
     */
    public void setAttCountMax(int attCountMax) {
        this.attCountMax = attCountMax;
    }
}
