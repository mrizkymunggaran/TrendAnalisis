/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trendanalisis.main.util;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVSaver;
import weka.core.converters.ConverterUtils.DataSource;

/**
 *
 * @author asus
 */
public class WekaUtils {

    public static Instances TransformToDataWeka(RealMatrix matrix,
            String[] documentNames, String[] words) throws ParseException {

        FastVector attributes = new FastVector();
        for (int i = 0; i < words.length; i++) {
            Attribute x = new Attribute(words[i]);
            attributes.addElement(x);
        }
        //     attributes.addElement(new Attribute("Doc", (FastVector) null));

        Instances wekaPoints = new Instances("TFIDF", attributes, 0);
        //     wekaPoints.setClassIndex(words.length);
        for (int j = 0; j < documentNames.length; j++) {
            // Instance inst = new Instance(words.length + 1);
            Instance inst = new DenseInstance(words.length);

            for (int i = 0; i < words.length; i++) {
                inst.setValue((Attribute) attributes.elementAt(i), matrix.getEntry(j, i));
            }
            //   inst.setValue((Attribute) attributes.elementAt(words.length), wekaPoints.attribute(words.length).addStringValue(documentNames[j]));
            inst.setDataset(wekaPoints);

            wekaPoints.add(inst);


        }
        return wekaPoints;
    }

    public static Instances ReadfileArff(String path) {
        Instances data = null;
        try {
            DataSource source = new DataSource(path);
            data = source.getDataSet();
        } catch (Exception ex) {
            Logger.getLogger(FileImportUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return data;
    }

    public static RealMatrix TransformDataWekaToRealMatrix(Instances intans) {
        RealMatrix rm = MatrixUtils.createRealMatrix(intans.numAttributes(), intans.numInstances());
        for (int i = 0; i < intans.numAttributes(); i++) {
            rm.setRow(i, intans.attributeToDoubleArray(i));
        }
        return rm;
    }

    public static void SaveArff(File file, Instances data) {
        try {
            ArffSaver saver = new ArffSaver();
            saver.setInstances(data);

            if (FilenameUtils.getExtension(file.getName()).isEmpty()) {
                String name = file.getAbsolutePath() + ".arff";
                file = new File(name);

            }

            //  if (file.exists()) {

            saver.setFile(file);
            saver.writeBatch();
            //  }

        } catch (IOException ex) {
            Logger.getLogger(WekaUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void SaveCSV(File file, Instances data) {
        try {
            CSVSaver saver = new CSVSaver();
            saver.setInstances(data);
          
            
                String name = file.getAbsolutePath() + ".csv";
                file = new File(name);

           
            //  if (file.exists()) {

            saver.setFile(file);
            saver.writeBatch();
            //  }

        } catch (IOException ex) {
            Logger.getLogger(WekaUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Instances TransformToDataWeka(ArrayList<String> lText) throws ParseException {

        Attribute x = new Attribute("text", (FastVector) null);
        ArrayList<Attribute> atts = new ArrayList<Attribute>();
        atts.add(x);

        Instances wekaPoints = new Instances("document", atts, lText.size());
        System.out.println("\tWEKA-UTIL");
        for (int j = 0; j < lText.size(); j++) {
            
            //String a[] = "42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,83,90,91,92,93,96,97,98,99,100,101,102,105,106,107,108,109,110,112,113,114,115,116,117,118,119,120,121,122,123,192,223,242,252,287,302,307,331,339,362,366,392,460,466,495,496,498,501,502,503,504,505,506,508,510,511,512,513,514,515,516,517,518,519,520,521".split(",");
            //if (ArrayUtils.contains(a, j+""))
            //System.out.println(j + "|" + lText.get(j).split(",").length + ":" + lText.get(j));
            
            String content = lText.get(j);//   TextUtils.CleanArraysListChar(lText.get(j));
            Instance inst = new DenseInstance(1);
            inst.setValue(x, content);
            inst.setDataset(wekaPoints);
            wekaPoints.add(inst);


        }
        //   System.out.println(wekaPoints);
        return wekaPoints;
    }

    public static Instances TransformToDataWekaLocationAndTimeSubtittle(Map<Integer, String> Maplocation, Map<Integer, String> MapTime, Map<Integer, String> Mapsubtittle) throws ParseException {

        Attribute subtittle = new Attribute("subtittle", (FastVector) null);
        Attribute location = new Attribute("location", (FastVector) null);
        Attribute time = new Attribute("time", (FastVector) null);
        ArrayList<Attribute> atts = new ArrayList<Attribute>();
        atts.add(subtittle);
        atts.add(location);
        atts.add(time);

        Instances wekaPoints = new Instances("LocationAndTimeAndSubtittle", atts, Maplocation.keySet().size());
        // System.out.println("WEKA-UTIL");


        for (int j = 0; j < Maplocation.keySet().size(); j++) {


            Instance inst = new DenseInstance(3);

            String tmpSub = (Mapsubtittle.get(j));
            inst.setValue(subtittle, tmpSub);

            String tmpLoc = (Maplocation.get(j));
            inst.setValue(location, tmpLoc);

            String tmpTime = (MapTime.get(j));
            inst.setValue(time, tmpTime);

            inst.setDataset(wekaPoints);
            wekaPoints.add(inst);
            // System.out.println(j+"|" + tmpSub +" , "+tmpLoc +", "+ tmpTime);

        }
        //   System.out.println(wekaPoints);
        return wekaPoints;
    }
}
