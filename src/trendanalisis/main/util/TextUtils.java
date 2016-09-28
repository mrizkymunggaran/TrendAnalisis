/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trendanalisis.main.util;
import java.util.ArrayList;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author asus
 */
public class TextUtils {

    public static String cleanChar(String str) {
        return str.replaceAll("[^A-Za-z0-9 ()!@#$%^&*{}<>_+-=/:?.,\']", "");
        //  return str.replaceAll("[^A-Za-z0-9 /&()-,]", "");
    }

    public static String cleanAlphaNominal(String str) {
        return str.replaceAll("[^A-Za-z0-9 ]", "");

    }
    
    public static String CleanArraysChar(String str) {
        
        String text = (str).replaceAll("[,]", " ");
               text = text.replaceAll("[{}]", "");
        return text;

    }
    
    
    public static int ParsingMonthDateIndonesia(String str) {

        String[] intMonth = new String[]{"januari", "februari", "maret", "april", "mei", "juni", "juli", "agustus", "september", "oktober", "november", "desember"};
        return ArrayUtils.indexOf(intMonth, str) + 1;

    }
}
