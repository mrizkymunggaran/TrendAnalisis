/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trendanalisis.main.method;

import java.util.ArrayList;
import org.apache.commons.lang.ArrayUtils;
import weka.core.Utils;

/**
 *
 * @author asus
 */
public class FeatureSelection {
    
    
     public static double Entropy(double tf[]) {
        double sum = 0;
        for (int j = 0; j < tf.length; j++) {
            sum += (tf[j] / Utils.sum(tf)) * Math.log((tf[j] / Utils.sum(tf)));
        }

        return -sum;

    }

     public static double InformationGain(double df, double N) {

        return ((df / N) * (Math.log(df / N))) + (((N - df) / N) * (Math.log((N - df) / N)));

    }

     public static double IdfProbability(double N, double df) {

        return (double) Math.log((N - df) / df);

    }
     
       
       
        public  static double NormalizationTC(double tf, double df) {

          //return (double)(0.5 * Math.log(tf) );
            
            return (double)  (Math.log(tf)/df);

    }
        
    
    
}
