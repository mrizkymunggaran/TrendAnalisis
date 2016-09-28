package trendanalisis.main.document.nlp;

import IndonesianNLP.IndonesianNETagger;
import IndonesianNLP.IndonesianSentenceDetector;
import IndonesianNLP.IndonesianStemmer;
import java.io.File;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import nz.ac.waikato.cs.weka.Utils;
import org.apache.commons.collections15.Bag;
import org.apache.commons.collections15.bag.HashBag;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.OpenMapRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
//import org.springframework.beans.factory.annotation.Required;
import trendanalisis.main.util.TextUtils;
import trendanalisis.main.tools.weka.StopwordRecognizer;
import trendanalisis.main.util.FileImportUtils;
import trendanalisis.main.util.WekaUtils;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.CosineSimilarity;
import weka.core.Instances;

/**
 * Generate the word occurence vector for a document collection.
 * @author Sujit Pal
 * @version $Revision: 21 $
 */
/*  KODE
 * 0= POST TAG N and Verb
 * 1= POST TAG N and Verb + STOPWORD
 * 2=--
 */
public class PraproccesingText {

    private Map<Integer, String> wordIdValueMap = new HashMap<Integer, String>();
    private Map<Integer, String> documentIdNameMap = new HashMap<Integer, String>();
    private RealMatrix matrix;
    private List<String> useTokenId = Arrays.asList("WORD");
    private List<String> usePos = Arrays.asList("NN", "NNP", "NNG", "VBI", "VBT");
    private List<String> usePosVerb = Arrays.asList("VBI", "VBT");
    private List<String> usePosNoun = Arrays.asList("NN", "NNP", "NNG");
    // private List<String> usePos = Arrays.asList("VBI", "VBT");
    //private List<String> useNE = Arrays.asList("LOCATION-I" , "LOCATION-B", "PERSON-I" , "PERSON-B", "ORGANIZATION-I" , "ORGANIZATION-B", "DATETIME-I", "DATETIME-B");
    //private List<String> useNE = Arrays.asList("LOCATION", "PERSON", "ORGANIZATION", "DATETIME");
    private List<String> useNE = Arrays.asList("PERSON", "ORGANIZATION");
    public static final int COMBINE_PARSE_AND_VERB_NOUN = 0;
    public static final int SUBTITTLE = 1;
    public static final int PERSENT_LENGTH_DOC_100 = 100;
    public static final int PERSENT_LENGTH_DOC_30 = 30;
    public static final int PERSENT_LENGTH_DOC_60 = 60;
    private String STOPWORD_DIR = "resource/stopword.txt";
    private String UTF_8 = "UTF-8";
    private String[] stopwordArray;
    private int indexTime = -1;
    private boolean statusSatuData = false;
    private boolean Stammer = false;
    private int KODE_PROSESS = 0;
    private Map<Integer, String> MapPersonOrg;
    private Map<Integer, String> Maplocation;
    private Map<Integer, String> MapTime;
    private Map<Integer, String> MapSubtittle;
    private ArrayList<String> lParseLocation = new ArrayList<>();
    private ArrayList<String> lParseTime = new ArrayList<>();
    private ArrayList<String> lParsePersonOrg = new ArrayList<>();
    private ArrayList<String> lContent = new ArrayList<>();
    
    private static String KAB_DIR = "resource/kab-kota.xls";
    Bag<String> kabBag = null;
    ArrayList<String> lDaftarKab = null;

    public PraproccesingText() {
    }

    public PraproccesingText(boolean stamm, int kode) {
        Stammer = stamm;
        KODE_PROSESS = kode;
        MapPersonOrg = new HashMap<Integer, String>();
        Maplocation = new HashMap<Integer, String>();
        MapTime = new HashMap<Integer, String>();
        MapSubtittle = new HashMap<Integer, String>();

        /*
         * Init filter lokasi
         */
        kabBag = new HashBag<String>();
        String kab[][] = FileImportUtils.ReadExcel(KAB_DIR, true);
        lDaftarKab = new ArrayList<String>(Arrays.asList(kab[2]));
        for (String tmpKab : lDaftarKab) {
            kabBag.addAll(Arrays.asList(tmpKab.split(" ")));

        }
    }
    //@Required

    public void BuildWordWithDocument(Map<String, String> documents, int tokenFitur, int docLength) throws Exception {

        StopwordRecognizer stopW = new StopwordRecognizer();
        Set<String> stopwords = stopW.getStopWord();

       
        int docID = 0;
        for (String key : documents.keySet()) {
            String text = (documents.get(key));
              documentIdNameMap.put(docID, key);

            System.out.println( docID + "\t: " + key);
            statusSatuData = false;
            Bag<String> wordFrequencies = getWordFrequencies(text, tokenFitur, docLength);


            getMapPersonOrg().put(docID, lParsePersonOrg.toString());

            if (lParseLocation.size() > 0) {
                Maplocation.put(docID, lParseLocation.get(0).toString());
            } else {
                Maplocation.put(docID, "");
            }


            if (lParseTime.size() > 0) {
                MapTime.put(docID, lParseTime.get(0).toString());
            } else {
                MapTime.put(docID, "");
            }

            /*ekstarct subtittle
             * 
             */
            
            File f = new File(key);
            String[] splitS = f.getName().toLowerCase().replace(".txt", "").split("-");
            String subtitle = ArrayUtils.toString(ArrayUtils.remove(splitS, 0));
            subtitle = TextUtils.cleanAlphaNominal(subtitle);
            IndonesianStemmer stemmer = new IndonesianStemmer();
             subtitle= stemmer.stemSentence(subtitle);
             ArrayList<String> lSub= new ArrayList<>(Arrays.asList(subtitle.split(" ")));
           
            lSub.removeAll(stopwords);
            lSub.removeAll(Maplocation.values());
           // lSub.retainAll(wordFrequencies);
            
            String subtitleExtract= TextUtils.CleanArraysChar(ArrayUtils.toString(lSub.toArray()));
          
            MapSubtittle.put(docID, subtitleExtract);
            
            String textExtract = ArrayUtils.toString(wordFrequencies.toArray());
            System.out.println(textExtract);
            System.out.println("subtitle extract : " + subtitleExtract);
            System.out.println("-------------------------------------------------------------");
            
            lContent.add(textExtract);
            
            docID++;
        }


        

    }

    public void generateVectorString(Map<String, String> documents, int tokenFitur, int docLength) throws Exception {
        Map<String, Bag<String>> documentWordFrequencyMap = new HashMap<String, Bag<String>>();
        SortedSet<String> wordSet = new TreeSet<String>();
        Integer docId = 0;
        File f = new File(STOPWORD_DIR);
        stopwordArray = StringUtils.split(FileUtils.readFileToString(f, UTF_8), " ");
        for (String key : documents.keySet()) {
            String text = (documents.get(key));

            statusSatuData = false;
            Bag<String> wordFrequencies = getWordFrequencies(text, tokenFitur, docLength);
            getMapPersonOrg().put(docId, lParsePersonOrg.toString());
            getMaplocation().put(docId, lParseLocation.get(0));
            MapTime.put(docId, lParseTime.get(0));
            //System.out.println(ArrayUtils.toString(wordFrequencies.toArray()));
            if (!wordFrequencies.isEmpty()) {
                wordSet.addAll(wordFrequencies.uniqueSet());
            }
            documentWordFrequencyMap.put(key, wordFrequencies);
            documentIdNameMap.put(docId, key);
            docId++;


        }
        // create a Map of ids to words from the wordSet
        int wordId = 0;
        for (String word : wordSet) {
            wordIdValueMap.put(wordId, word);
            wordId++;
        }
        // we need a documents.keySet().size() x wordSet.size() matrix to hold
        // this info

        int numDocs = documents.keySet().size();
        int numWords = wordSet.size();
        matrix = new OpenMapRealMatrix(numWords, numDocs);
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            for (int j = 0; j < matrix.getColumnDimension(); j++) {
                String docName = documentIdNameMap.get(j);
                Bag<String> wordFrequencies = documentWordFrequencyMap.get(docName);
                String word = wordIdValueMap.get(i);
                int count = wordFrequencies.getCount(word);
                /*
                 * test untuk mengambil jumlah 1 saja kata yang sama 
                 * dalam 1 dokumen
                 */
                //  count= (count>0)?1:0;
                matrix.setEntry(i, j, count);
            }
        }

        // CorrectWords();
    }

    public RealMatrix getMatrix() {
        return matrix;
    }

    public String[] getDocumentNames() {
        String[] documentNames = new String[documentIdNameMap.keySet().size()];
        for (int i = 0; i < documentNames.length; i++) {
            documentNames[i] = documentIdNameMap.get(i);
        }
        return documentNames;
    }

    public String[] getWords() {
        String[] words = new String[wordIdValueMap.keySet().size()];
        for (int i = 0; i < words.length; i++) {
            String word = wordIdValueMap.get(i);
            if (word.contains("|||")) {
                // phrases are stored with length for other purposes, strip it off
                // for this report.
                word = word.substring(0, word.indexOf("|||"));
            }
            words[i] = word;
        }
        return words;
    }

    public void CorrectWords() {
        String[] words = new String[wordIdValueMap.keySet().size()];
        ArrayList<Integer> lRemove = new ArrayList<Integer>();
        AbstractStringMetric metric = new CosineSimilarity();
        for (int i = 0; i < words.length - 1; i++) {
            String word = wordIdValueMap.get(i);
            for (int j = (i + 1); j < words.length; j++) {
                String word2 = wordIdValueMap.get(j);
                char[] s1 = word.toCharArray();
                char[] s2 = word2.toCharArray();
                float result = metric.getSimilarity(ArrayUtils.toString(s1).replace(",", " "), ArrayUtils.toString(s2).replace(",", " "));
                if (result > 0.88) {
                    System.out.println(i + "//" + ArrayUtils.toString(s1).replace(",", " ") + j + "//" + "//" + ArrayUtils.toString(s2).replace(",", " ") + " = " + result);
                    RealMatrix mSub = matrix.getRowMatrix(j);
                    for (int k = 0; k < documentIdNameMap.keySet().size(); k++) {
                        matrix.setEntry(i, k, matrix.getEntry(i, k) + mSub.getEntry(0, k));
                    }
                    if (!lRemove.contains(j)) {
                        lRemove.add(j);
                    }
                }
            }
            System.out.println(lRemove);
        }

        RealMatrix m = MatrixUtils.createRealMatrix(matrix.getRowDimension() - lRemove.size(), matrix.getColumnDimension());
        int hit = 0;
        words = getWords();
        wordIdValueMap.clear();
        for (int k = 0; k < words.length - 1; k++) {
            if (!lRemove.contains(k)) {
                m.setRow(hit, matrix.getRow(k));
                System.out.println(words[k]);
                wordIdValueMap.put(hit, words[k]);
                hit++;

            }
        }
        matrix = m;
    }

    private String getText(Reader reader) throws Exception {
        StringBuilder textBuilder = new StringBuilder();
        char[] cbuf = new char[1024];
        int len = 0;
        while ((len = reader.read(cbuf, 0, 1024)) != -1) {
            textBuilder.append(ArrayUtils.subarray(cbuf, 0, len));
        }
        reader.close();
        return textBuilder.toString();
    }

    private Bag<String> getWordFrequencies(String text, int tokenFitur, int lengtDoc) throws Exception {

        IndonesianNETagger ne = new IndonesianNETagger();

        /*
         * String Detector // mengabil kalimat pertama
         * PROSES 1. SELECTED NE and VERB
         * PROSES 2. STOPWORD REMOVAL
         * PROSES 3. STEMMING
         * 
         */
        // text = text.replace(",", " ");


        //IndonesianSentenceDetector detector = new IndonesianSentenceDetector();
        ArrayList<String> sentenceList = new ArrayList<String>();
        //sentenceList = detector.splitSentence(text);
        System.out.println(TextUtils.cleanChar(text));
        ArrayList<String> listNE = ne.extractNamedEntity(TextUtils.cleanChar(text));
        IndonesianStemmer stemmer = new IndonesianStemmer();
        ArrayList<String> lToken = new ArrayList<String>();


        //   stopW = new StopwordRecognizer();

        //  Set<String> stopwordsEvent = stopW.getStopWordEvent();

        lParseLocation = new ArrayList<String>();
        lParseTime = new ArrayList<String>();
        lParsePersonOrg = new ArrayList<String>();
        ArrayList<String> lTokenDefault = new ArrayList<String>();

        //   lToken = getWordFrequencies(sentenceList.get(0));
        //  String str_location = (((i * 1.0) / (ne.getToken().size())) <= (1.0 / 3.0)) ? "AWAL" : (((i * 1.0) / (ne.getToken().size())) <= (2.0 / 3.0)) ? "TENGAH" : "AKHIR";            

        int length = ((lengtDoc == PERSENT_LENGTH_DOC_100) ? listNE.size() - 3 : (int) Math.round(listNE.size() * ((double) lengtDoc / (double) 100)));
        // System.out.println("LENGTH : " + length);


        for (int i = 0; i <= length - 1; i++) {

            String token = ne.getToken().get(i).replace("'", "").toLowerCase();
            lTokenDefault.add(stemmer.stem(token));
            //  System.out.println(token + " = "+ne.getTokenKind().get(i) +" "+ ne.getPOSFeature().get(i)+" "+ ne.getContextualFeature().get(i) + " " +ne.getNE().get(i));

            i = Location(ne, i, lToken, lParseLocation);
            i = TimeNews(ne, i, lToken, lParseTime);
            i = PersonOrganization(ne, i, lToken, lParsePersonOrg);

//            switch (tokenFitur) {
//
//                 
//                case COMBINE_PARSE_AND_VERB_NOUN:
//
//                    i = Location(ne, i, lToken, lParseLocation);
//                    i = TimeNews(ne, i, lToken, lParseTime);
//                    i = PersonOrganization(ne, i, lToken, lParsePersonOrg);
//
//                    break;
//                case 99:
//                    break;
//
//            }



        }

        //  lToken.removeAll(Arrays.asList("comma"));

        System.out.println("Token   \t" + lToken.toString());
        System.out.println("Location \t" + lParseLocation.toString());
        System.out.println("PersonOrg\t" + lParsePersonOrg.toString());
        System.out.println("Time     \t" + lParseTime.toString());


        ArrayList<String> lTokenCombine = new ArrayList<String>();

        //  indexTime = (indexTime == -1) ? (listNE.size() * (60 / 100)) : indexTime - 1;

        indexTime = ((lengtDoc == PERSENT_LENGTH_DOC_100) ? listNE.size() - 1 : (int) Math.round(listNE.size() * ((double) lengtDoc / (double) 100)));

        for (int i = 0; i <= indexTime; i++) {

            String token = ne.getToken().get(i).replace("'", "").toLowerCase();

//            System.out.println(token + "\t"
//                    + ne.getPOSFeature().get(i) + "\t"
//                    + ne.getNE().get(i) + "\t");


            // token= stemmer.stem(token);
            //lTokenCombine.add((token));
            //  if ((!stopwords.contains(token))) {

            if ((usePos.contains(ne.getPOSFeature().get(i))
                    && (stemmer.stem(token)).length() > 2
                    && "OTHER".equals(ne.getNE().get(i)))) {


                token = stemmer.stem(token);


                String tokenCLesrDi = (token).substring(2, token.length());
                token = ((token.substring(0, 2).equals("di") && tokenCLesrDi.length() > 2) ? tokenCLesrDi : token);

                lTokenCombine.add((token));
            }



        }






        if (tokenFitur == SUBTITTLE) {

            lTokenCombine.addAll(lToken);

        }

        //  lTokenCombine.removeAll(stopwords);
        // lToken.removeAll(Arrays.asList("comma"));


        /*
         * Ambil Location dan cek di 3 list
         * 1. di location list
         * 2. di PersonOrg list
         * 3.   di token list
         * jika di semua list tidak ada maka lokasi kosong
         */
        String locFilter = GetFilterLocation(lParseLocation);
        if (locFilter.equals("")) {
            locFilter = GetFilterLocation(lParsePersonOrg);
        }

        if (locFilter.equals("")) {
            locFilter = GetFilterLocation(lToken);
        }


        lParseLocation.clear();
        lParseLocation.add(locFilter);
        //lToken.removeAll(stopwords);
        /*
         * 0= POST TAG N and Verb
         * 1= POST TAG N and Verb + STOPWORD
         * 2=--
         */

        lToken = lTokenCombine;

        Bag<String> wordBag = new HashBag<String>(lToken);

        //System.out.println("JUMLAH WORD : \t" + wordBag.size());
        return wordBag;

    }

    public Instances GetTitleLocTimeInsatences() throws ParseException {

        return WekaUtils.TransformToDataWekaLocationAndTimeSubtittle(Maplocation, MapTime, MapSubtittle);

    }
    
    public Instances GetContentInsatences() throws ParseException {

        return WekaUtils.TransformToDataWeka(lContent);

    }

    private String GetFilterLocation(ArrayList<String> lLoc) {

        lLoc = new ArrayList<String>(lLoc);
        String HasilExtract = "";
        for (int i = 0; i < lLoc.size(); i++) {

            ArrayList<String> tmpParseLoc = new ArrayList<String>(Arrays.asList(lLoc.get(i).split(" ")));
            tmpParseLoc.retainAll(kabBag.uniqueSet());
            String tmpS = "";
            tmpS = TextUtils.cleanAlphaNominal(tmpParseLoc.toString());
            if (lDaftarKab.indexOf(tmpS) != -1) {
                //System.out.println("-- DAPAT WORD : \t" + tmpS);
                HasilExtract = tmpS;
                //i = lParseLocation.size();
                break;
            } else {
                tmpS = "";
            }

//            String tmpS = "";
//            for (int j = i + 1; j <= lParseLocation.size(); j++) {
//                tmpS = TextUtils.cleanAlphaNominal(lParseLocation.subList(i, j).toString());
//                System.out.println("CAri WORD : \t" + tmpS);
//                if (lDaftarKab.indexOf(tmpS) != -1) {
//                    System.out.println("-- DAPAT WORD : \t" + tmpS);
//                    HasilExtract = tmpS;
//                    //i = lParseLocation.size();
//
//                } else {
//                    tmpS = "";
//                }
//
//            }
        }

        lLoc.clear();
        lLoc.add(HasilExtract);

        System.out.println("lLocation WORD : \t" + HasilExtract);

        return HasilExtract;
    }

    /* Get Location
     * find location with flag kata 'di' POS-IN next POS NN next find with NE must LOCATION
     *koMBINASI =  IN+NN+NNP/NN-+LOCATION until Not LOCATION
     *  
     * 
     * Kombinasi =  IN+NN+NNP/NN-+LOCATION until Not LOCATION
     *  TOKEN           POS     NE
     * --------------------------------
     *  di              IN	OTHER 
    cipinang	NN	LOCATION-B 
    melayu          NNP	LOCATION-I 
    jatinegara      NNP	LOCATION-I 
    jakarta 	NNP	LOCATION-I 
    timur           NN	LOCATION-I 
    
    
     */
    public int Location(IndonesianNETagger ne, int i, ArrayList<String> lToken, ArrayList<String> lParseLoc) {
        int idReturn = i;
        String token = ne.getToken().get(i).replace("'", "").toLowerCase();
        String loc = "";

        if ("di".equals(token) && ("IN".equals(ne.getPOSFeature().get(i)))
                && ("NN".equals(ne.getPOSFeature().get(i + 1)))) {
            lToken.add(ne.getToken().get(i + 1).replace("'", "").toLowerCase());
            lToken.add(ne.getToken().get(i + 2).replace("'", "").toLowerCase());

//
//            //   lToken.add(ne.getToken().get(i + 2).replace("'", "").toLowerCase());
//
//            loc = lToken.get(lToken.size() - 2) + " " + lToken.get(lToken.size() - 1);
//
////            System.out.println(ne.getToken().get(i + 1).replace("'", "").toLowerCase() + "\t"
////                    + ne.getPOSFeature().get(i + 1) + "\t"
////                    + ne.getNE().get(i + 1) + "\t");
////
////            System.out.println(ne.getToken().get(i + 2).replace("'", "").toLowerCase() + "\t"
////                    + ne.getPOSFeature().get(i + 2) + "\t"
////                    + ne.getNE().get(i + 2) + "\t");
//
//
//
//            idReturn = i + 3;
//            for (int j = idReturn; j <= ne.getToken().size() - 1; j++) {
//                token = ne.getToken().get(j).replace("'", "").toLowerCase();
//
//                if ("LOCATION".equals(ne.getNE().get(j).substring(0, ne.getNE().get(j).length() - 2))) {
//
//                    lToken.add(token);
//                    loc += " " + token;
////
////                    System.out.println(token + "\t"
////                            + ne.getPOSFeature().get(j) + "\t"
////                            + ne.getNE().get(j) + "\t");
//
//
//                } else {
//                    idReturn = j - 1;
//                    break;
//                }
//
//            }
//
//            //    System.out.println("");
//            
//            lParseLoc.add(loc);
        } else if ("LOCATION-B".equals(ne.getNE().get(i)) && statusSatuData == false && i >= 5) {
            //statusSatuData = true;
            lToken.add(token);
            String tmploc = token;

            idReturn = i + 1;
            for (int j = idReturn; j <= ne.getToken().size() - 1; j++) {
                token = ne.getToken().get(j).replace("'", "").toLowerCase();
                if ("comma".equals(token)) {
                    lParseLoc.add(tmploc + loc + "");
                    tmploc = "";
                    loc = "";
                    //  idReturn = j - 1;
                    continue;
                }
                if ("LOCATION".equals(ne.getNE().get(j).substring(0, ne.getNE().get(j).length() - 2))) {

                    lToken.add(token);
                    loc += (loc.equals("") && (tmploc.equals("")) ? "" : " ");
                    loc += token;

                    System.out.println(i +" "+ tmploc +" "+ token + "\t"
                            + ne.getPOSFeature().get(j) + "\t"
                            + ne.getNE().get(j) + "\t");


                } else {
                    idReturn = j - 1;
                    break;
                }

            }


            if (!loc.equals("") && !tmploc.equals("")) {
                lParseLoc.add(tmploc + loc + "");
            }
            //   lParseLoc.add(tmploc + loc + "");

        }



        return idReturn;
    }

    /*
     * Get DateTime 
     * Kombinasi OP+CDP+CP (22/8/2015) atau CDP+NN+CDP (22 Agustus 2015)
     * TOKEN           POS     NE
     * --------------------------------
     * (                OP	OTHER 
     * 22/8/2015	CDP	OTHER 
     * )               CP	OTHER 
     * --------------------------------
     *  18              CDP	DATETIME-I 
    april       	NN	DATETIME-I 
    2013            CDP	DATETIME-I 
     */
    public int TimeNews(IndonesianNETagger ne, int i, ArrayList<String> lToken, ArrayList<String> lParseTime) {
        int idReturn = i;
        String token = ne.getToken().get(i).replace("'", "").toLowerCase();

        if ("(".equals(token) && ("OP".equals(ne.getPOSFeature().get(i)))
                && ("CDP".equals(ne.getPOSFeature().get(i + 1)))
                && ("CP".equals(ne.getPOSFeature().get(i + 2)))
                && ("OTHER".equals(ne.getNE().get(i + 1)))) {
            lToken.add(ne.getToken().get(i + 1).replace("'", "").toLowerCase());
            lParseTime.add(lToken.get(lToken.size() - 1));


//            System.out.println(ne.getToken().get(i + 1).replace("'", "").toLowerCase() + "\t"
//                    + ne.getPOSFeature().get(i + 1) + "\t"
//                    + ne.getNE().get(i + 1) + "\t");

            if (indexTime == -1) {
                indexTime = i;
            }

            i += 2;

        } else if ("CDP".equals(ne.getPOSFeature().get(i))
                && ("NN".equals(ne.getPOSFeature().get(i + 1)))
                && ("CDP".equals(ne.getPOSFeature().get(i + 2)))
                && ("DATETIME".equals(ne.getNE().get(i + 1).substring(0, ne.getNE().get(i + 1).length() - 2)))) {

            int intDate = TextUtils.ParsingMonthDateIndonesia(ne.getToken().get(i + 1).replace("'", "").toLowerCase());
            String sdate = ne.getToken().get(i).replace("'", "") + "/" + intDate + "/" + ne.getToken().get(i + 2).replace("'", "");

            lToken.add(sdate.toLowerCase());

            lParseTime.add(lToken.get(lToken.size() - 1));
//            System.out.println(sdate.toLowerCase() + "\t"
//                    + ne.getPOSFeature().get(i + 1) + "\t"
//                    + ne.getNE().get(i + 1) + "\t");

            if (indexTime == -1) {
                indexTime = i;
            }

            i += 2;

        }


        idReturn = i;
        return idReturn;
    }

    /*
     * Get Events
     * Kombinasi = VBT+NN(OTHER)
     * JIka NN(!OTHER) cek kata selajutnya
     */
    public int EventsNews(IndonesianNETagger ne, int i, ArrayList<String> lToken) {
        IndonesianStemmer stemmer = new IndonesianStemmer();
        int idReturn = i;
        String token = ne.getToken().get(i).replace("'", "").toLowerCase();

        if (("VBT".equals(ne.getPOSFeature().get(i)))
                && ("NN".equals(ne.getPOSFeature().get(i + 1)))) {

            if ("OTHER".equals(ne.getNE().get(i + 1))) {

                // lToken.add(stemmer.stem   (ne.getToken().get(i).replace("'", "").toLowerCase()));
                lToken.add(stemmer.stem(ne.getToken().get(i + 1).replace("'", "").toLowerCase()));

//                lToken.add(ne.getToken().get(i).replace("'", "").toLowerCase());
//                lToken.add( ne.getToken().get(i + 1).replace("'", "").toLowerCase());

                System.out.print(stemmer.stem(token) + " ");

                System.out.print(stemmer.stem(ne.getToken().get(i + 1).replace("'", "").toLowerCase()) + " ");

                if (usePos.contains(ne.getPOSFeature().get(i + 2))) {

                    lToken.add(stemmer.stem(ne.getToken().get(i + 2).replace("'", "").toLowerCase()));

                    System.out.print(stemmer.stem(ne.getToken().get(i + 2).replace("'", "").toLowerCase()) + " ");

                    i += 1;
                }

                i += 1;


            } else if (useNE.contains(ne.getNE().get(i + 1).substring(0, ne.getNE().get(i + 1).length() - 2))
                    && (usePos.contains(ne.getPOSFeature().get(i + 2)))) {

                // lToken.add(stemmer.stem ( ne.getToken().get(i).replace("'", "").toLowerCase()));
                lToken.add(stemmer.stem(ne.getToken().get(i + 1).replace("'", "").toLowerCase()));
                lToken.add(stemmer.stem(ne.getToken().get(i + 2).replace("'", "").toLowerCase()));

//                lToken.add( ne.getToken().get(i).replace("'", "").toLowerCase());
//                lToken.add(ne.getToken().get(i + 1).replace("'", "").toLowerCase());
//                lToken.add (ne.getToken().get(i + 2).replace("'", "").toLowerCase());

                System.out.print(stemmer.stem(token) + " ");

                System.out.print(stemmer.stem(ne.getToken().get(i + 1).replace("'", "").toLowerCase()) + " ");

                System.out.print(stemmer.stem(ne.getToken().get(i + 2).replace("'", "").toLowerCase()) + " ");

                i += 2;



            }

            System.out.println("");
        }


        idReturn = i;
        return idReturn;
    }


    /*
     * find location with flag kata 'di' POS-IN next POS NN next find with NE must LOCATION
     *koMBINASI =  IN+NN+NNP/NN-+LOCATION until Not LOCATION
     */
    public int PersonOrganization(IndonesianNETagger ne, int i, ArrayList<String> lToken, ArrayList<String> lParsePersonOrg) {
        int idReturn = i;
        String token = ne.getToken().get(i).replace("'", "").toLowerCase();
        String person = "";
        if ("(".equals(token) && ("OP".equals(ne.getPOSFeature().get(i)))
                && ((useNE.contains(ne.getNE().get(i + 1).substring(0, ne.getNE().get(i + 1).length() - 2))))
                && ("CP".equals(ne.getPOSFeature().get(i + 2)))) {
            lToken.add(ne.getToken().get(i + 1).replace("'", "").toLowerCase());


            lParsePersonOrg.add(lToken.get(lToken.size() - 1));

            System.out.println("<1>" + lToken.get(lToken.size() - 1));
            i += 2;

            idReturn = i;

        } else if ((useNE.contains(ne.getNE().get(i).substring(0, ne.getNE().get(i).length() - 2)))
                && (useNE.contains(ne.getNE().get(i + 1).substring(0, ne.getNE().get(i + 1).length() - 2)))) {

            lToken.add(ne.getToken().get(i).replace("'", "").toLowerCase());
            lToken.add(ne.getToken().get(i + 1).replace("'", "").toLowerCase());

            person = lToken.get(lToken.size() - 2) + " " + lToken.get(lToken.size() - 1);


            for (int j = i + 2; j <= ne.getToken().size() - 1; j++) {
                token = ne.getToken().get(j).replace("'", "").toLowerCase();

                if (useNE.contains(ne.getNE().get(j).substring(0, ne.getNE().get(j).length() - 2))) {

                    lToken.add(token);
                    person += " " + token;


                } else {
                    idReturn = j - 1;
                    break;
                }

            }
            System.out.println("<2>" + person);
            lParsePersonOrg.add(person);

        }

        return idReturn;
    }

    /**
     * @return the MapPersonOrg
     */
    public Map<Integer, String> getMapPersonOrg() {
        return MapPersonOrg;
    }

    /**
     * @param MapPersonOrg the MapPersonOrg to set
     */
    public void setMapPersonOrg(Map<Integer, String> MapPersonOrg) {
        this.MapPersonOrg = MapPersonOrg;
    }

    /**
     * @return the Maplocation
     */
    public Map<Integer, String> getMaplocation() {
        return Maplocation;
    }

    /**
     * @param Maplocation the Maplocation to set
     */
    public void setMaplocation(Map<Integer, String> Maplocation) {
        this.Maplocation = Maplocation;
    }

    /**
     * @return the MapSubtittle
     */
    public Map<Integer, String> getMapSubtittle() {
        return MapSubtittle;
    }

    /**
     * @param MapSubtittle the MapSubtittle to set
     */
    public void setMapSubtittle(Map<Integer, String> MapSubtittle) {
        this.MapSubtittle = MapSubtittle;
    }
}
