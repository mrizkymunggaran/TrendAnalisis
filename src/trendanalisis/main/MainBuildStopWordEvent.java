/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trendanalisis.main;

import IndonesianNLP.IndonesianStemmer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.commons.collections15.Bag;
import org.apache.commons.collections15.bag.HashBag;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author asus
 */
public class MainBuildStopWordEvent {

    public static void main(String[] args) {

        String txt = "diri situs buruk shabu narkotika aborsi abu adiktif air aktivitas alkohol ambruk amplituda amplitudo ancam angin aniaya api asap asusila bacok bahaya bajak bakar bandang bangsat banjir bantai batu bayi bea bebas begal beliung bendung bentrok black bocor bom bong brutal buang bukti bumi bunuh buruh burung buta butir cekik celurit copet cracker crime cukai culik curang curanmor curi cyber demo deras duplikat edar ekstasi elektronik erupsi flu gaji ganja gantung gas gelap geledah gempa genang geng genk gerak-gerik gerebek gerombol getar gila golok goyang granat guncang gunung h2n1 hacker hacking hadang hamil hancur hangus hantam haram heroin hilang hipnotis hitam hujan ilegal imigran isap jahat jarah jebol jual judi kabur kampanye kdrt kejar kelompok keras koplo korup korupsi koruptor kriminal krisis kurir lahan langgar larang lempeng letus licik longsor loundry luap mabuk mafia magnitude maling manipulasi market mati mayat mesum miras modus molotov moneter money motor mucikari muntah mutilasi narkoba ngungsi nikotin nipu nyawa nyuri obat otopsi padam pajak paket palak palsu pasar penjara penyandraan perang perkosa pil pilkada pipet pisau pistol porak-poranda potong psikolog psikologi psikotropika puing pukul puting racun radius rampok razia rebek rekam rendam residivis reta retak ribut richter ricuh ringkus rob rokok rubuh rumah sabu sabung sakit sandra seks selundup serang serbu sex sindikat sita skala skotik sporter stress sungai tabrak tahan tanggul tektonik tembak tenggelam tengkar terisolir teror teroris tikam tilang tipu todong topan tremor tsunami tusuk uang ungsi vulkanik jenazah korban rusuh kerusuhan pelecehan leceh runtuh hancur oplos oplosan pengoplos cuci pencucian kapal asing ham pohon gusur penggusuran hina penghinaan kekerasan cuaca tumbang angin kencang cari pencarian tawuran hanyut suap penyuapan teror formalin pelanggaran perjokian joki sekap penyekapan perbudakan perkelahian keroyok pengeroyokan penyekapan penimbunan timbun premanisme jambret preman";

        txt= "aborsi abu adiktif air aktivitas alkohol ambruk amplituda amplitudo ancam angin aniaya api asap asing asusila bacok bahaya bajak bakar bandang bangsat banjir bantai batu bayi bea bebas begal beliung bendung bentrok bertengkar bertikai black bocor bom bong brutal buang bukti bumi bunuh buruh buruk burung buta butir cari cekik celaka celurit copet cracker crime cuaca cuci cukai culik curang curanmor curi cyber demo deras diri dirusak duplikat edar ekstasi elektronik erupsi evakuasi flu formalin gaji ganja gantung gas gelap geledah gempa genang geng genk gerak-gerik gerebek gerombol getar gila golok goyang granat guling guncang gunung gusur h2n1 hacker hacking hadang ham hamil hancur hangus hantam hanyut haram heroin hilang hina hipnotis hitam hujan ilegal imigran insiden isap jahat jambret jarah jatuh jebol jenazah joki jual judi kabur kampanye kapal kasus kdrt kecelakaan kejar kekerasan kekeringan kelompok kencang keras kering keroyok kerusuhan komplotan koplo korban korup korupsi koruptor kriminal krisis kurir lahan langgar laporkan larang leceh ledak ledakan lempeng letus liar licik longsor loundry luap mabuk mafia magnitude maling mangkir manipulasi market mati mayat melaporkan meledak membekuk memeras meras mesum miras modus molotov moneter money motor mucikari muntah mutilasi narkoba narkotika ngungsi nikotin nipu nyawa nyuri obat oplos oplosan otopsi padam pajak paket palak palsu pasar pelanggaran pelecehan pembekuan pencarian pencucian pengeroyokan penggusuran penghinaan pengoplos penimbunan penjara penyandraan penyekapan penyuapan perang perbudakan perjokian perkelahian perkosa pertengkaran pertikaian pil pilkada pipet pisau pistol pohon porak-poranda potong preman premanisme psikolog psikologi psikotropika puing pukul puting racun radius rampok razia rebek rekam rendam residivis reta retak ribut richter ricuh ringkus rob roboh rokok rubuh rumah runtuh rusuh sabetan sabu sabung sakit sandra sekap seks selundup serang serbu sex shabu sindikat sita situs skala skotik sporter stress suap sungai tabrak tahan tanggul tawuran teguran tektonik tembak tenggelam tengkar terguling terisolir teror teroris tikam tilang timbun tipu todong topan tremor tsunami tumbang tusuk uang ungsi vulkanik rusak pungutan tumbang";
        IndonesianStemmer stemmer = new IndonesianStemmer();
        txt = stemmer.stem(txt);
        List<String> asList = Arrays.asList(txt.split(" "));
        Bag<String> wordBag = new HashBag<String>(asList);
        SortedSet<String> wordSet = new TreeSet<String>();
        wordSet.addAll(wordBag.uniqueSet());

        System.out.println(ArrayUtils.toString(wordSet.toArray()));

    }
}
