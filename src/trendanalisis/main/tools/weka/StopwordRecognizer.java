package trendanalisis.main.tools.weka;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import weka.core.stopwords.StopwordsHandler;

/**
 * A recognizer that recognizes common stop words. Special stopwords may
 * be passed in through the non-default constructor.
 * @author Sujit Pal
 * @version $Revision: 2 $
 */
public final class StopwordRecognizer implements StopwordsHandler  {

  private static final String DEFAULT_STOPWORDS = "resource/stopword.txt";
  private static final String DEFAULT_STOPWORDS_LIST = "resource/stopword_list.txt";
   private static final String DEFAULT_STOPWORDS_EVENT = "resource/peristiwa2.txt";
    

  private Set<String> stopwords = new HashSet<String>();
  
    private Set<String> stopwordsEvent = new HashSet<String>();
    
  public StopwordRecognizer() throws Exception {
    init();
  }
  
  public StopwordRecognizer(String[] stopwords) {
    this.stopwords.addAll(Arrays.asList(stopwords));
 
  }
  
  public void init() throws Exception {
       
    if (stopwords.isEmpty()) {
      File f= new File(DEFAULT_STOPWORDS);
      File fl= new File(DEFAULT_STOPWORDS_LIST);
           String[] stopwordArray= StringUtils.split(FileUtils.readFileToString(f, "UTF-8"), " ");
                //String[] stopwordArray = StringUtils.split(DEFAULT_STOPWORDS, " ");
        
       stopwords.addAll(FileUtils.readLines(fl, "UTF-8"));    
      stopwords.addAll(Arrays.asList(stopwordArray));
      
    }
    
     if (stopwordsEvent.isEmpty()) {
      File f= new File(DEFAULT_STOPWORDS_EVENT);
          String[] stopwordArray= StringUtils.split(FileUtils.readFileToString(f, "UTF-8"), " ");
                //String[] stopwordArray = StringUtils.split(DEFAULT_STOPWORDS, " ");
        
      stopwordsEvent.addAll(Arrays.asList(stopwordArray));
    }
  }

  public List<String> recognize(List<String> tokens) {
    List<String> recognizedTokens = new ArrayList<String>();
       for (String token : tokens) {
      
        if (stopwords.contains(StringUtils.lowerCase(token))) {
                      recognizedTokens.add(token);
        }
            
    }
    return recognizedTokens;
  }
  
  public Set<String>  getStopWord(){
   return stopwords;
  }
  
  public Set<String>  getStopWordEvent(){
   return stopwordsEvent;
  }

    @Override
    public boolean isStopword(String string) {
         return stopwords.contains(string); 
    }

}
