/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trendanalisis.main;

import IndonesianNLP.IndonesianNETagger;
import IndonesianNLP.IndonesianPhraseChunker;
import IndonesianNLP.IndonesianSentenceDetector;
import IndonesianNLP.IndonesianStemmer;
import JavaMI.Entropy;
import JavaMI.MutualInformation;
import java.io.PrintWriter;
import java.lang.String;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.jtmt.clustering.DocumentCollection;
import net.sf.jtmt.indexers.matrix.IdfIndexer;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.stat.StatUtils;
import trendanalisis.main.document.nlp.PraproccesingText;
import trendanalisis.main.util.TextUtils;
import trendanalisis.main.tools.weka.StopwordRecognizer;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.CosineSimilarity;

/**
 *
 * @author asus
 */
public class MainFitur {

    private static List<String> usePos = Arrays.asList("NN", "NNG", "NNP", "VBI", "VBT");
    // private static List<String> usePos = Arrays.asList("VBI", "VBT");
    private static List<String> useNE = Arrays.asList("PERSON", "ORGANIZATION");
    private static Set<String> stopwords;
    private static int indexTime = -1;

    public static void main(String[] args) throws Exception {

        //saya pergi kepasar saya
        // saya tidak pulang
        //  saya,pergi,kepasar,tidak,pulang
        //1:2,1,1,0,0
        //2:1,0,0,1,1
        //3:0,1,1,0,0


        double[][] data = new double[5][3];
        double[] y = new double[]{1, 1, 0};
        data[0] = y;
        y = new double[]{1, 1, 0};
        data[1] = y;
        y = new double[]{0, 0, 1};
        data[2] = y;
        y = new double[]{0, 0, 1};
        data[3] = y;
        y = new double[]{0, 0, 1};
        data[4] = y;

        for (int j = 0; j < 5; j++) {
            double a = 0;
            for (int k = 0; k < 5; k++) {
                a += MutualInformation.calculateMutualInformation(data[k], data[j]);
                System.out.println(MutualInformation.calculateMutualInformation(data[k], data[j]));
            }
            System.out.println("--> " + a / 5);
            System.out.println("---");
           
        }

        Map<String, String> documentWordFrequencyMap = new HashMap<String, String>();


        String s = "aborsi abu adiktif air aktivitas alkohol ambruk amplituda amplitudo ancam angin aniaya api asap asusila bacok bahaya bajak bakar bandang bangsat banjir bantai batu bayi bea bebas begal beliung bendung bentrok black bocor bom bong brutal buang bukti bumi bunuh buruh burung buta butir cekik celurit copet cracker crime cukai culik curang curanmor curi cyber demo deras duplikat edar ekstasi elektronik erupsi flu gaji ganja gantung gas gelap geledah gempa genang geng genk gerak-gerik gerebek gerombol getar gila golok goyang granat guncang gunung h2n1 hacker hacking hadang hamil hancur hangus hantam haram heroin hilang hipnotis hitam hujan ilegal imigran isap jahat jarah jebol jual judi kabur kampanye kdrt kejar kelompok keras koplo korup korupsi koruptor kriminal krisis kurir lahan langgar larang lempeng letus licik longsor loundry luap mabuk mafia magnitude maling manipulasi market mati mayat mesum miras modus molotov moneter money motor mucikari muntah mutilasi narkoba ngungsi nikotin nipu nyawa nyuri obat otopsi padam pajak paket palak palsu pasar penjara penyandraan perang perkosa pil pilkada pipet pisau pistol porak-poranda potong psikolog psikologi psikotropika puing pukul puting racun radius rampok razia rebek rekam rendam residivis reta retak ribut richter ricuh ringkus rob rokok rubuh rumah sabu sabung sakit sandra seks selundup serang serbu sex sindikat sita skala skotik sporter stress sungai tabrak tahan tanggul tektonik tembak tenggelam tengkar terisolir teror teroris tikam tilang tipu todong topan tremor tsunami tusuk uang ungsi vulkanik jenazah korban rusuh kerusuhan pelecehan leceh runtuh hancur oplos oplosan pengoplos cuci pencucian kapal asing ham pohon gusur penggusuran hina penghinaan kekerasan cuaca tumbang angin kencang cari pencarian tawuran hanyut suap penyuapan teror formalin pelanggaran perjokian joki sekap penyekapan perbudakan perkelahian keroyok pengeroyokan penyekapan penimbunan timbun premanisme jambret preman";
        RealMatrix tdMatrix;
        String[] documentNames;
        DocumentCollection documentCollection;
        documentWordFrequencyMap.put("dd", s);
        PraproccesingText vectorGenerator = new PraproccesingText();
        vectorGenerator.generateVectorString(documentWordFrequencyMap, PraproccesingText.COMBINE_PARSE_AND_VERB_NOUN, PraproccesingText.PERSENT_LENGTH_DOC_100);
        IdfIndexer indexer = new IdfIndexer();
        // TfIndexer indexer = new TfIndexer();

        tdMatrix = indexer.transform(vectorGenerator.getMatrix());
        // tdMatrix= vectorGenerator.getMatrix();
        documentNames = vectorGenerator.getDocumentNames();


        System.out.println(" GET WORD \t:" + ArrayUtils.toString(vectorGenerator.getWords()));


        String text = "Sangatta (ANTARA News)-  Sebanyak 2.000 jiwa lebih warga dari 353 Kepala Keluarga (KK) desa Malupan Kecamatan Muara Bengkal  Kabupaten Kutai Timur  Kalimantan Timur masih terisolir akibat banjir setinggi dada  merendam daerah itu sejak seminggu lalu. Kepala Desa Malupan  Fatul  saat menghubungi Antara  melaporkan  hingga hari ini seluruh warga terisolir akibat terendam banjir setinggi dada orang dewasa  seluruh kampung dari ujung ke ujung terendam air banjir. Akses transportasi darat lumpuh total  sedangkan warga membuat panggung diatas lantai rumah masing-masing setinggi satu meter untuk tidur dan istrahat kata Fatul  Kamis. Menurutnya  meski hingga saat ini tidak ada korban jiwa  namun perekoniomian warga lumpuh akibat banjir melanda desa selama seminggu ini. Petani dan pedagang tidak bisa beraktivitas. Fasilitas umum seperti listrik sudah mati karena mesin terendam air banjir  jembatan juga terendam banjir  sedangkan layanan air bersih mau pun pelayanan umum di kantor desa terhenti. \"Lapangan sepakbola sudah menjadi danau dan sekolah ditutup serta anak-anak diliburkan untuk menjaga keselamatan mereka  tambah Fatul yang berharap pemerintah memberikan  bantuan sembako dan bantuan lainnya yang dibutuhkan warga.";
        IndonesianNETagger ne = new IndonesianNETagger();
        IndonesianStemmer stemmer = new IndonesianStemmer();
        text = text.replace(",", "");
        ArrayList<String> listNE = ne.extractNamedEntity(TextUtils.cleanChar(text.toLowerCase()));

        ArrayList<String> lToken = new ArrayList<String>();
        StopwordRecognizer stopW = new StopwordRecognizer();
        stopwords = stopW.getStopWord();
        AbstractStringMetric metric = new CosineSimilarity();
        char[] s1 = "meperkosa".toCharArray();
        char[] s2 = "pembunuh".toCharArray();
        float result = metric.getSimilarity(ArrayUtils.toString(s1).replace(",", " "), ArrayUtils.toString(s2).replace(",", " "));
        System.out.println(stemmer.stemSentence("perkosa memperkosa perkosa"));
        System.out.println(ArrayUtils.toString(s1));
        System.out.println(ArrayUtils.toString(s2));
        System.out.println(result);
        lToken = Fitur(text);

        //String a[] = lToken.toArray(new String[lToken.size()]);
        //Arrays.fill(a, "");
        // String remove = StringUtils.replaceEach(text.toLowerCase(), lToken.toArray(new String[lToken.size()]), a);
        System.out.println(lToken.toString());

                IndonesianPhraseChunker chunker = new IndonesianPhraseChunker();

       
        chunker.setSentence(text);
        chunker.extractPhrase();
        chunker.printPhraseTree(chunker.getPhraseTree());
       
          
        
        indexTime = (indexTime == -1) ? (int) (listNE.size() * 0.6) : indexTime;
        indexTime = listNE.size() - 1;
        for (int i = 0; i <= indexTime; i++) {

            String token = ne.getToken().get(i).replace("'", "").toLowerCase();

            if ((usePos.contains(ne.getPOSFeature().get(i)) && (!lToken.contains(token))
                    && !stopwords.contains(token))) {

                lToken.add(stemmer.stem(token));



                System.out.println(stemmer.stem(token) + "\t"
                        + ne.getPOSFeature().get(i) + "\t"
                        + ne.getNE().get(i) + "\t");
            }

            System.out.println(stemmer.stem(token) + "\t"
                    + ne.getPOSFeature().get(i) + "\t"
                    + ne.getNE().get(i) + "\t");





//                 



//         if ((usePos.contains(ne.getPOSFeature().get(i))
//                    || useNE.contains(ne.getNE().get(i).substring(0, ne.getNE().get(i).length() - 2))) 
//                 && !stopwords.contains(token)) {
//             //if ((usePos.contains(ne.getPOSFeature().get(i))))
//                     {
//                     System.out.println(token + "\t"
//                   + ne.getPOSFeature().get(i)+"\t"
//                +ne.getNE().get(i)+" "); 
//                 
//                     
////               System.out.println( stemmer.stem(token) + " = "
////                    + ne.getPOSFeature().get(i)+" "
////                   +ne.getNE().get(i));
//          }


//                     if ( useNE.contains(ne.getNE().get(i).substring(0, ne.getNE().get(i).length() - 2)) 
//                 && !stopwords.contains(token)) {
//             //if ((usePos.contains(ne.getPOSFeature().get(i))))
//                     {
//                     System.out.println(token + "\t"
//                   + ne.getPOSFeature().get(i)+"\t"
//                +ne.getNE().get(i)+" "); 
//                 
//                 
//          }


//            if ((usePos.contains(ne.getPOSFeature().get(i))
//                   || useNE.contains(ne.getNE().get(i).substring(0, ne.getNE().get(i).length() - 2))))  {
//                
//                System.out.println(token + "\t"
//                    + ne.getPOSFeature().get(i) + "\t"
//                    + ne.getNE().get(i) + "\t ");
//                
//            }





        }


    }

    public static ArrayList<String> Fitur(String text) throws Exception {

//        IndonesianSentenceDetector detector = new IndonesianSentenceDetector();
//        ArrayList<String> sentenceList = new ArrayList<String>();
//        sentenceList = detector.splitSentence(text);
//        System.out.println(sentenceList.get(0));

        IndonesianNETagger ne = new IndonesianNETagger();
        IndonesianStemmer stemmer = new IndonesianStemmer();
        text = text.replace(",", " ");
        ArrayList<String> listNE = ne.extractNamedEntity(TextUtils.cleanChar(text));

        ArrayList<String> lToken = new ArrayList<String>();
        int length = ((int) Math.round(listNE.size() * ((double) 30 / (double) 100)));
        length = listNE.size();
        for (int i = 0; i <= length - 1; i++) {

            i = Location(ne, i, lToken);
            i = TimeNews(ne, i, lToken);
            //i  = EventsNews(ne, i, lToken);
            i = PersonOrganization(ne, i, lToken);

        }

        return lToken;
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
    public static int Location(IndonesianNETagger ne, int i, ArrayList<String> lToken) {
        int idReturn = i;
        String token = ne.getToken().get(i).replace("'", "").toLowerCase();
        if ("di".equals(token) && ("IN".equals(ne.getPOSFeature().get(i)))
                && ("NN".equals(ne.getPOSFeature().get(i + 1)))) {
            lToken.add(ne.getToken().get(i + 1).replace("'", "").toLowerCase());
            lToken.add(ne.getToken().get(i + 2).replace("'", "").toLowerCase());

            System.out.println(ne.getToken().get(i + 1).replace("'", "").toLowerCase() + "\t"
                    + ne.getPOSFeature().get(i + 1) + "\t"
                    + ne.getNE().get(i + 1) + "\t");

            System.out.println(ne.getToken().get(i + 2).replace("'", "").toLowerCase() + "\t"
                    + ne.getPOSFeature().get(i + 2) + "\t"
                    + ne.getNE().get(i + 2) + "\t");



            idReturn = i + 3;
            for (int j = idReturn; j <= ne.getToken().size() - 1; j++) {
                token = ne.getToken().get(j).replace("'", "").toLowerCase();

                if ("LOCATION".equals(ne.getNE().get(j).substring(0, ne.getNE().get(j).length() - 2))) {

                    lToken.add(token);

                    System.out.println(token + "\t"
                            + ne.getPOSFeature().get(j) + "\t"
                            + ne.getNE().get(j) + "\t");

                } else {
                    idReturn = j - 1;
                    break;
                }

            }

            System.out.println("");
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
    public static int TimeNews(IndonesianNETagger ne, int i, ArrayList<String> lToken) {
        int idReturn = i;
        String token = ne.getToken().get(i).replace("'", "").toLowerCase();

        if ("(".equals(token) && ("OP".equals(ne.getPOSFeature().get(i)))
                && ("CDP".equals(ne.getPOSFeature().get(i + 1)))
                && ("CP".equals(ne.getPOSFeature().get(i + 2)))
                && ("OTHER".equals(ne.getNE().get(i + 1)))) {
            lToken.add(ne.getToken().get(i + 1).replace("'", "").toLowerCase());

            System.out.println(ne.getToken().get(i + 1).replace("'", "").toLowerCase() + "\t"
                    + ne.getPOSFeature().get(i + 1) + "\t"
                    + ne.getNE().get(i + 1) + "\t");

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

            System.out.println(sdate.toLowerCase() + "\t"
                    + ne.getPOSFeature().get(i + 1) + "\t"
                    + ne.getNE().get(i + 1) + "\t");

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
    public static int EventsNews(IndonesianNETagger ne, int i, ArrayList<String> lToken) {
        int idReturn = i;
        String token = ne.getToken().get(i).replace("'", "").toLowerCase();

        if (("VBT".equals(ne.getPOSFeature().get(i)))
                && ("NN".equals(ne.getPOSFeature().get(i + 1)))) {

            if ("OTHER".equals(ne.getNE().get(i + 1))) {

                lToken.add(ne.getToken().get(i).replace("'", "").toLowerCase());
                lToken.add(ne.getToken().get(i + 1).replace("'", "").toLowerCase());

                System.out.println(token + "\t"
                        + ne.getPOSFeature().get(i) + "\t"
                        + ne.getNE().get(i) + "\t");

                System.out.println(ne.getToken().get(i + 1).replace("'", "").toLowerCase() + "\t"
                        + ne.getPOSFeature().get(i + 1) + "\t"
                        + ne.getNE().get(i + 1) + "\t");

                if (usePos.contains(ne.getPOSFeature().get(i + 2))) {

                    lToken.add(ne.getToken().get(i + 2).replace("'", "").toLowerCase());

                    System.out.println(ne.getToken().get(i + 2).replace("'", "").toLowerCase() + "\t"
                            + ne.getPOSFeature().get(i + 2) + "\t"
                            + ne.getNE().get(i + 2) + "\t");
                    i += 1;
                }

                i += 1;

                System.out.println("");

            } else if (useNE.contains(ne.getNE().get(i + 1).substring(0, ne.getNE().get(i + 1).length() - 2))
                    && (usePos.contains(ne.getPOSFeature().get(i + 2)))) {

                lToken.add(ne.getToken().get(i).replace("'", "").toLowerCase());
                lToken.add(ne.getToken().get(i + 1).replace("'", "").toLowerCase());
                lToken.add(ne.getToken().get(i + 2).replace("'", "").toLowerCase());

                System.out.println(token + "\t"
                        + ne.getPOSFeature().get(i) + "\t"
                        + ne.getNE().get(i) + "\t");

                System.out.println(ne.getToken().get(i + 1).replace("'", "").toLowerCase() + "\t"
                        + ne.getPOSFeature().get(i + 1) + "\t"
                        + ne.getNE().get(i + 1) + "\t");

                System.out.println(ne.getToken().get(i + 2).replace("'", "").toLowerCase() + "\t"
                        + ne.getPOSFeature().get(i + 2) + "\t"
                        + ne.getNE().get(i + 2) + "\t");
                i += 2;

                System.out.println("");

            }


        }


        idReturn = i;
        return idReturn;
    }


    /*
     * find location with flag kata 'di' POS-IN next POS NN next find with NE must LOCATION
     *koMBINASI =  IN+NN+NNP/NN-+LOCATION until Not LOCATION
     */
    public static int PersonOrganization(IndonesianNETagger ne, int i, ArrayList<String> lToken) {
        int idReturn = i;
        String token = ne.getToken().get(i).replace("'", "").toLowerCase();
        if ("(".equals(token) && ("OP".equals(ne.getPOSFeature().get(i)))
                && ("CP".equals(ne.getPOSFeature().get(i + 2)))) {

            if ((useNE.contains(ne.getNE().get(i + 1).substring(0, ne.getNE().get(i + 1).length() - 2)))
                    || "NNP".equals(ne.getPOSFeature().get(i + 1))) {


                lToken.add(ne.getToken().get(i + 1).replace("'", "").toLowerCase());

                System.out.println(ne.getToken().get(i + 1).replace("'", "").toLowerCase() + "\t"
                        + ne.getPOSFeature().get(i + 1) + "\t"
                        + ne.getNE().get(i + 1) + "\t");

                i += 2;

                idReturn = i;

            }


        } else if ((useNE.contains(ne.getNE().get(i).substring(0, ne.getNE().get(i).length() - 2)))
                && (useNE.contains(ne.getNE().get(i + 1).substring(0, ne.getNE().get(i + 1).length() - 2)))) {

            lToken.add(ne.getToken().get(i).replace("'", "").toLowerCase());
            lToken.add(ne.getToken().get(i + 1).replace("'", "").toLowerCase());

            System.out.println(token + "\t"
                    + ne.getPOSFeature().get(i) + "\t"
                    + ne.getNE().get(i) + "\t");

            System.out.println(ne.getToken().get(i + 1).replace("'", "").toLowerCase() + "\t"
                    + ne.getPOSFeature().get(i + 1) + "\t"
                    + ne.getNE().get(i + 1) + "\t");

            for (int j = i + 2; j <= ne.getToken().size() - 1; j++) {
                token = ne.getToken().get(j).replace("'", "").toLowerCase();

                if (useNE.contains(ne.getNE().get(j).substring(0, ne.getNE().get(j).length() - 2))) {

                    lToken.add(token);

                    System.out.println(token + "\t"
                            + ne.getPOSFeature().get(j) + "\t"
                            + ne.getNE().get(j) + "\t");

                } else {
                    idReturn = j - 1;
                    break;
                }

            }



        }

        return idReturn;
    }
}
