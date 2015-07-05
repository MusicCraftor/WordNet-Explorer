package wordNetParser;
import edu.mit.jwi.item.IPointer;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.Pointer;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.Dictionary;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

import edu.mit.jwi.item.POS;
public class WordNetParser {
	 public void runExample()throws IOException{
		     
		      // construct the URL to the Wordnet dictionary directory
		     String wnhome = System.getenv("WNHOME");
		     if(wnhome == null)System.out.println("can not get WNHOME environment path");
		     else System.out.println("The env arg is "+wnhome);
		     String path = wnhome + File.separator + "dict";
		     URL url = null;
		     try{ url = new URL("file", null, path); } 
		     catch(MalformedURLException e){ e.printStackTrace(); }
		     if(url == null) return;
		     
		     // construct the dictionary object and open it
		     IDictionary dict = new Dictionary(url);
		     dict.open();
		 
		     // look up first sense of the word "dog"
		     IIndexWord idxWord = dict.getIndexWord("run", POS.VERB);
		     IWordID wordID;
		     IWord word;
		     System.out.println("MeaningNUM = "+idxWord.getWordIDs().size());
		     for(int ii=0;ii< idxWord.getWordIDs().size();++ii){
		     wordID = idxWord.getWordIDs().get(ii);
		     word = dict.getWord(wordID);
			     for(IWordID synsetid:word.getRelatedWords(Pointer.ALSO_SEE)){
			    	 IWord synset = dict.getWord(synsetid);
			    	 System.out.println("ALSO_SEE "+synset.getLemma());
			     }
		    	 System.out.println("Id = " + wordID);
			     System.out.println("Lemma = " + word.getLemma());
			     System.out.println("Gloss = " + word.getSynset().getGloss());
			     System.out.println("Synset NUM = " + word.getSynset().getWords().size());
			     for(int i=0;i<word.getSynset().getWords().size();++i)
			    	 	System.out.println("Synset["+i+"] = " + word.getSynset().getWords().get(i));
			     
			    	
		     }
		     
		   }
	 public static void main(String[] arg)throws IOException{
		  (new WordNetParser()).runExample();
	 }
}
