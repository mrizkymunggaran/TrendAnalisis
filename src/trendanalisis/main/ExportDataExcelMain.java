/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trendanalisis.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import trendanalisis.main.util.FileImportUtils;

/**
 *
 * @author asus
 */
public class ExportDataExcelMain {

    private static ArrayList<String> listDoc;

    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        
        ConvertContentFolderToOneFolder();
        
       // ExportExcelToFolder();
    }

    private static void ExportExcelToFolder() {

        String fileDir = "D:/databerita/data uji/datauji.xls";
        String path = "D:/databerita/data uji satu folder";

        int colIndex = 1;
        int indContent = 2;
        int indCatagory = 6;
        String DataExcel[][] = FileImportUtils.ReadExcel(fileDir, false);
        String regex = "[!@#$%^&*()_+<>?:{}/\"]";
        for (int j = 1; j < DataExcel[colIndex].length - 1; j++) {

            String newFolderName = (DataExcel[indCatagory][j].charAt(0) == " ".charAt(0))
                    ? DataExcel[indCatagory][j].substring(1, DataExcel[indCatagory][j].length() - 1)
                    : DataExcel[indCatagory][j];

            newFolderName = (newFolderName.charAt(newFolderName.length() - 1) == " ".charAt(0))
                    ? newFolderName.substring(0, newFolderName.length() - 2)
                    : newFolderName;
            listDoc = new ArrayList<String>();
            listDoc.add(DataExcel[colIndex][j]);
            // System.out.println(DataExcel[colIndex][j]);
            System.out.println(path + File.separator + (DataExcel[colIndex][j]).replaceAll(regex, "") + ".txt");
            FileImportUtils.WriteFile(DataExcel[indContent][j], path + File.separator + newFolderName + "-" + (DataExcel[colIndex][j]).replaceAll(regex, "") + ".txt");
        }


    }

    private static void ConvertContentFolderToOneFolder() {


        String INDEX_DIR = "data/berita/GabungDoniPandu";
        Map<String, String> documents = null;
        File f = new File(INDEX_DIR);


        try {
            documents = FileImportUtils.getFileRecursivetoMapString(f);
            for (String s : documents.keySet()) {
                File fl = new File(s);

                FileImportUtils.WriteFile(documents.get(s), "data/berita/data uji doni/" + fl.getName() + ".txt");
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
