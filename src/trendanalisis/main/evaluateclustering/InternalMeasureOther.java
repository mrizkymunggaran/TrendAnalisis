/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trendanalisis.main.evaluateclustering;

import java.io.IOException;
import trendanalisis.main.tools.weka.InitCoreWekaKmeans;

/**
 *
 * @author asus
 */
public class InternalMeasureOther {

    /*
     * Hartigan (1975) 
     */
    public static double Hartigan(double SSB, double SSE) {

        return Math.log(SSB / SSE);

    }

    public static double WBIndex(int k, double SSB, double SSE) {

        return k * (SSE / SSB);

    }

    public static double FRatio(int k, int N, double SSB, double SSE) {
        //Calinski & Harabasz (1974)
        //(SSB/k-1) / (SSE/N-k)
        //F-Ration
        double derajatSSB = k - 1;//k-1
        double derajatSSE = N - k;//N-k

        return (SSB / derajatSSB) / (SSE / derajatSSE);

    }
    
    


}
