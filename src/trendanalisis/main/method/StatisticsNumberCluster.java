/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trendanalisis.main.method;

/**
 *
 * @author asus
 */
public class StatisticsNumberCluster {
    
     public static int RuleOfTumb(int numDoc) {

        return (int) Math.round(Math.sqrt(numDoc / 2));

    }

    public static double RuleOfSturges(int numDoc) {

        /*  H. A. Sturges pada tahun 1926, yaitu dengan rumus:
         * 
         */
        return (1 + (3.3 * Math.log(numDoc)));


    }    
    
     public static double RuleOfSturgesBase10(int numDoc) {

        /*  H. A. Sturges pada tahun 1926, yaitu dengan rumus:
         * 
         */
        return (1 + (3.3 * Math.log10(numDoc)));


    }

    public static double RuleOfScoots(int numDoc, double stdv) {

        /*  Scotts Rule, yaitu dengan rumus:
         * h=3.5σ/∛n
         */
        return ((3.5 * stdv) / Math.cbrt(numDoc));


    }
    
}
