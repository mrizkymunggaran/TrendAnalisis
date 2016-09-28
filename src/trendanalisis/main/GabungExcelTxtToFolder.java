/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trendanalisis.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import trendanalisis.main.util.FileImportUtils;

/**
 *
 * @author asus
 */
public class GabungExcelTxtToFolder {

    private static ArrayList<String> listDoc;

    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        /*
         * INPUT (.TXT)
         * Ambil Direktori isi kumpulan .txt dari berita
         */
        String INDEX_DIR = "D:/databerita/data uji";
       // File f = new File(INDEX_DIR);

       // List<File> filesList = FileImportUtils.getFileRecursive(f);

        /*
         * INPUT
         * Data excel hasil pelebelan
         */
        int colIndex = 1;
        int indContent = 2;
        int indCatagory = 6;
        String fileDir = "D:/databerita/data uji/datauji.xls";
        String DataExcel[][] = FileImportUtils.ReadExcel(fileDir,false);

        /*
         * OUTPUT
         * Hasil pengelomokan manual disimpan di folder ini.
         */

        String path = "D:/databerita/data uji";
        File setFile = new File(path);

        String regex = "[!@#$%^&*()_+<>?:{}/\"]";
        for (int j = 1; j < DataExcel[colIndex].length; j++) {
            listDoc = new ArrayList<String>();
            listDoc.add(DataExcel[colIndex][j]);
            String txt = DataExcel[indContent][j];
//            for (File key : filesList) {
//
//                if (key.getName().equals((DataExcel[colIndex][j]).replaceAll(regex, "") + ".txt")) {
//                    txt = FileUtils.readFileToString(key, "UTF-8");
//                    break;
//                }
//
//            }
            
            if (txt.length()<10) continue;
            String newFolderName = (DataExcel[indCatagory][j].charAt(0)==" ".charAt(0))?
                    DataExcel[indCatagory][j].substring(1, DataExcel[indCatagory][j].length()-1):
                    DataExcel[indCatagory][j];
            
             newFolderName = (newFolderName.charAt(newFolderName.length()-1) ==" ".charAt(0))?
                    newFolderName.substring(0, newFolderName.length()-2):
                    newFolderName;
            
           
            String outfolderName= FileImportUtils.CekFolderInSplitString(setFile, newFolderName);
             File newFolder = new File(path + File.separator + outfolderName);
            if (outfolderName.equals(newFolderName)){
             newFolder.mkdir();
             //System.out.println("--- buat folder nama : " + newFolder.getName());
            }
                
            
            
//            if (!FileImportUtils.CekFolderInSplitString(setFile, DataExcel[indCatagory][j])) {
//                newFolder.mkdir();
//            }
            
            
            
            String newFileName = outfolderName + "-" + (DataExcel[colIndex][j]).replaceAll(regex, "") + ".txt";

            if (!FileImportUtils.CekFile(newFolder, newFileName)) {
                System.out.println(j +".  "+newFolder + File.separator + newFileName);

                FileImportUtils.WriteFile(txt, newFolder + File.separator + newFileName);
            }
            
            
        }



    }
}
