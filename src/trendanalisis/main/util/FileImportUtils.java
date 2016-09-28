/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trendanalisis.main.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author asus
 */
public class FileImportUtils {

    private static String INDEX_DIR = "data/berita";
    private static int id = 0;

    public static ArrayList<String> readFile(String file_name) {
        /* kamus */
        ArrayList<String> list = null;
        Scanner scanner;

        /* algoritma */

        try {
            // inisialisasi varibel
            list = new ArrayList<String>();
            scanner = new Scanner(new FileInputStream(file_name));

            // proses baca tiap baris
            while (scanner.hasNext()) {
                list.add(scanner.nextLine());
            }

            // close stream
            scanner.close();
        } catch (FileNotFoundException ex) {
           // JOptionPane.showMessageDialog(null, "Utils->readFile() ERROR! " + file_name, "Error", JOptionPane.ERROR_MESSAGE);
           //Logger.getLogger(FileImportUtils.class.getName()).log(Level.SEVERE, null, ex);
           return null;
        
        }

        return list;
    }

    public static String[][] ReadExcel(String directory, boolean lowerCase) {
        String fileData[][] = null;

        try {

            File file = new File(directory);

            Workbook workbook = Workbook.getWorkbook(file);
            Sheet[] sheet = workbook.getSheets();

            int x = sheet[0].getRows();
            int y = sheet[0].getColumns();
            fileData = new String[y][x];

            for (int i = 0; i < x; ++i) {
                for (int j = 0; j < y; ++j) {
                    String ret = sheet[0].getCell(j, i).getContents();
                    if (lowerCase) {
                        ret = ret.toLowerCase();
                    }
                    fileData[j][i] = ret;

                }
            }

        } catch (IOException ex) {
            Logger.getLogger(FileImportUtils.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex + "  ERROR");

        } catch (BiffException ex) {
            Logger.getLogger(FileImportUtils.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex + "  ERROR BIFF");
        }
        return fileData;
    }

    /**
     * Load files recursively from a directory
     *
     * @param dir
     * @return
     * @throws FileNotFoundException
     */
    public static List<File> getFileRecursive(File dir) throws FileNotFoundException {
        List<File> result = new ArrayList<File>();

        File[] filesAndDirs = dir.listFiles();
        List filesDirs = Arrays.asList(filesAndDirs);
        Iterator filesIter = filesDirs.iterator();
        File file;
        while (filesIter.hasNext()) {
            file = (File) filesIter.next();
            if (file.isFile()) {
                result.add(file); //always add, even if directory
            } else {
                List<File> deeperList = getFileRecursive(file);
                result.addAll(deeperList);


            }

        }
        return result;
    }

    /**
     * Load directories recursively from a directory
     *
     * @param dir
     * @return
     * @throws FileNotFoundException
     */
    public static List<File> getFolderRecursive(File dir) throws FileNotFoundException {
        Set<File> result = new HashSet<File>();

        File[] filesAndDirs = dir.listFiles();
        List filesDirs = Arrays.asList(filesAndDirs);
        Iterator filesIter = filesDirs.iterator();
        File file;
        while (filesIter.hasNext()) {
            file = (File) filesIter.next();
            if (file.isDirectory()) {
                result.add(file); //always add, even if directory
                List<File> deeperList = getFolderRecursive(file);
                result.addAll(deeperList);

            }

        }
        result.add(dir);

        List<File> toreturn = new ArrayList<File>(result);
        Collections.sort(toreturn);
        return toreturn;
    }

    public static Map<String, Reader> getFileRecursivetoMap(File dir) throws FileNotFoundException, IOException, Exception {
        Map<String, Reader> result = new LinkedHashMap<String, Reader>();

        File[] filesAndDirs = dir.listFiles();
        List filesDirs = Arrays.asList(filesAndDirs);
        Iterator filesIter = filesDirs.iterator();
        File file;
        while (filesIter.hasNext()) {
            file = (File) filesIter.next();
            if (file.isFile()) {
                result.put(file.getParent() + File.separator + file.getName(), new StringReader(FileUtils.readFileToString(file, "UTF-8"))); //always add, even if directory
                // System.out.println(FileUtils.readFileToString(file, "UTF-8") +"  ERROR BIFF");
                //System.out.println("--ISFILE- " + file.getName()+" SIZE " + result.size());


            } else {
                Map<String, Reader> deeperList = getFileRecursivetoMap(file);
                result.putAll(deeperList);
                //System.out.println("--PUT- " + file.getName() +" SIZE " + result.size());

            }
//            StringReader s = new StringReader(FileUtils.readFileToString(file, "UTF-8"));
//            Logger.getLogger(FileImportUtils.class.getName()).log(Level.INFO,"" ,getText(s));
//// System.out.println(FileUtils.readFileToString(file, "UTF-8") +"  ERROR BIFF");
        }
        //Logger.getLogger(FileImportUtils.class.getName()).log(Level.INFO, "document size : {0}", result.size());
        return result;
    }

    public static void WriteFile(String content, String path) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(path));
            pw.print(content);
            pw.close();
        } catch (IOException ex) {
            Logger.getLogger(FileImportUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static Map<String, String> getFileRecursivetoMapString(File dir) throws FileNotFoundException, IOException, Exception {
        Map<String, String> result = new LinkedHashMap<String, String>();

       // System.out.println();
        File[] filesAndDirs = dir.listFiles();
        List filesDirs = Arrays.asList(filesAndDirs);
        Iterator filesIter = filesDirs.iterator();
        File file;
        while (filesIter.hasNext()) {
            
            file = (File) filesIter.next();
            
            // System.out.println("--ISFILE- " + file.getName()+" SIZE " + result.size());
            if (file.isFile()) {
                if ("txt".equals(FilenameUtils.getExtension(file.getName()))) {
                    result.put(file.getParent() + File.separator + file.getName(), (FileUtils.readFileToString(file, "UTF-8"))); //always add, even if directory
                  //  System.out.print(id + " ");
                    id++;
                }

            } else {
                Map<String, String> deeperList = getFileRecursivetoMapString(file);
                result.putAll(deeperList);
                System.out.println(file.getName() +"\t" + deeperList.size());

            }
//            StringReader s = new StringReader(FileUtils.readFileToString(file, "UTF-8"));
//            Logger.getLogger(FileImportUtils.class.getName()).log(Level.INFO,"" ,getText(s));
//// System.out.println(FileUtils.readFileToString(file, "UTF-8") +"  ERROR BIFF");
        }
        //Logger.getLogger(FileImportUtils.class.getName()).log(Level.INFO, "document size : {0}", result.size());
        return result;
    }

    public static boolean CekFolder(File dir, String folderName) throws FileNotFoundException {
        boolean stat = false;

        File[] filesAndDirs = dir.listFiles();
        List filesDirs = Arrays.asList(filesAndDirs);
        Iterator filesIter = filesDirs.iterator();
        File file;
        while (filesIter.hasNext()) {
            file = (File) filesIter.next();
            if (file.isDirectory()) {
                if (file.getName().equals(folderName)) {
                    stat = true;
                }

            }

        }

        return stat;
    }

    public static String CekFolderInSplitString(File dir, String folderName) throws FileNotFoundException {
      
        String outNameFoleder = folderName;
        File[] filesAndDirs = dir.listFiles();
        List filesDirs = Arrays.asList(filesAndDirs);
        Iterator filesIter = filesDirs.iterator();
        File file;
        while (filesIter.hasNext()) {
            file = (File) filesIter.next();
            if (file.isDirectory()) {



                String[] folderNames = folderName.split(" ");
                String[] folderInDir = file.getName().split(" ");
                for (String name : folderInDir) {
                    if (ArrayUtils.contains(folderNames, name)) {
                        outNameFoleder = file.getName();
                    }

                }

            }

        }

        return outNameFoleder;
    }

    public static boolean CekFile(File dir, String fileName) throws FileNotFoundException {
        boolean stat = false;

        File[] filesAndDirs = dir.listFiles();

      
        List filesDirs = Arrays.asList(filesAndDirs);
        Iterator filesIter = filesDirs.iterator();
        File file;
        while (filesIter.hasNext()) {
            file = (File) filesIter.next();
            if (file.isFile()) {

                if (fileName.equals(file.getName())) {

                    stat = true;
                }
            }
        }

        return stat;
    }

    public static void ToFolderCluster(String pathOutputDir, Map<Integer, ArrayList<Integer>> mapCluster, double err,
            Map<Integer, ArrayList<String>> docLabelCluster, String label,
            double lengtPersenDoc, Map<String, String> documents)
            throws FileNotFoundException, IOException, Exception {


        File setFile = new File(pathOutputDir);
        String newFolderName = documents.size() + " Doc, " + label + ", " + mapCluster.size()
                + " C, " + String.format("%.4f", err) + " E, " + String.format("%.2f", lengtPersenDoc) + " %";
        File newFolder = new File(pathOutputDir + File.separator + newFolderName);

        if (!FileImportUtils.CekFolder(setFile, newFolderName)) {
            newFolder.mkdir();
        }

        for (int j : mapCluster.keySet()) {

            String lblCluster = docLabelCluster.get(j).toString();
            // File f= new File(documents.keySet().toArray()[lblCluster].toString());
            String newFolderClusterName = j + "," + lblCluster;
            File newFolderCluster = new File(pathOutputDir + File.separator + newFolderName + File.separator + newFolderClusterName);

            //  System.out.println (pathOutputDir + File.separator + newFolderName + File.separator + newFolderClusterName);
            for (int i : mapCluster.get(j)) {

                if (!FileImportUtils.CekFolder(newFolder, newFolderClusterName)) {
                    //  System.out.println ("ksosong");
                    newFolderCluster.mkdir();
                }
                //  System.out.println ("ada");
                File f = new File(documents.keySet().toArray()[i].toString());
                String txt = documents.get(documents.keySet().toArray()[i].toString());
                String namFile = f.getName();
                // System.out.println(newFolderCluster + File.separator + namFile);
                if (!FileImportUtils.CekFile(newFolderCluster, namFile)) {
                    //  System.out.println(newFolderCluster + File.separator + namFile);

                    FileImportUtils.WriteFile(txt, newFolderCluster + File.separator + namFile);
                }

            }


        }




    }
}
