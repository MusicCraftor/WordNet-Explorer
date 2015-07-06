package wordNetManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;



import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IPointer;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;

public class WordDic {
	Map<String, String> map;
	IDictionary dict;
	String[] words;
	int wordsTop=0;
	String wordName;
	IIndexWord wordIndexArray[];
	boolean showExample[][] ;
	public static final String[] POfS={"Noun","Verb","Adj","Adv"}; 
	public static final int nNOUN=0;
	public static final int nVERB=1;
	public static final int nADJ= 2;
	public static final int nADV= 3;
	public static final String TSYNSET = "synset";
	public static final String TALSO_SEE="also see";
	public static final String TANTONYM= "antonym";
	public static final String TATTRIBUTE = "attribute";
	public static final String TCAUSE = "cause";
	public static final String TDERIVATIONALLY_RELATED = "derivationally related";
	public static final String TDERIVED_FROM_ADJ = "derived from adj";
	public static final String TDOMAIN = "domain";
	public static final String TENTAILMENT = "entailment";
	public static final String THOLONYM_MEMBER = "holonym member";
	public static final String THOLONYM_PART = "holonym part";
	public static final String THOLONYM_SUBSTANCE = "holonym substance";
	public static final String THYPERNYM = "hypernym";
	public static final String THYPERNYM_INSTANCE = "hypernym instance";
	public static final String THYPONYM = "hyponym";
	public static final String THYPONYM_INSTANCE = "hyponym instance";
	public static final String TMEMBER = "member";
	public static final String TMERONYM_MEMBER = "meronym member";
	public static final String TMERONYM_PART = "meronym part";
	public static final String TMERONYM_SUBSTANCE = "meronym substance";
	public static final String TPARTICIPLE = "participle";
	public static final String TPERTAINYM = "pertainym";
	public static final String TREGION = "region";
	public static final String TREGION_MEMBER = "region member";
	public static final String TSIMILAR_TO = "similar to";
	public static final String TTOPIC = "topic";
	public static final String TTOPIC_MEMBER = "topic member";
	public static final String TUSAGE = "usage";
	public static final String TUSAGE_MEMBER = "usage member";
	public static final String TVERB_GROUP = "verb group";
	public static final String[] TARRAY = new String[]{TSYNSET,TALSO_SEE,TANTONYM,TATTRIBUTE,TCAUSE,TDERIVATIONALLY_RELATED,TDERIVED_FROM_ADJ
		,TDOMAIN,TENTAILMENT,THOLONYM_MEMBER,THOLONYM_PART,THOLONYM_SUBSTANCE,THYPERNYM,THYPERNYM_INSTANCE,THYPONYM,THYPONYM_INSTANCE,TMEMBER,TMERONYM_MEMBER
		,TMERONYM_PART,TMERONYM_SUBSTANCE,TPARTICIPLE,TPERTAINYM,TREGION,TREGION_MEMBER,TSIMILAR_TO,TTOPIC,TTOPIC_MEMBER,TUSAGE,TUSAGE_MEMBER,TVERB_GROUP};
	public WordDic(){
		this(null);
	}
	public WordDic(String path){
		super();
		initDic(path);
		wordName = null;
	}
	public void setWord(String wordname){
	//	System.out.println(wordname);
		wordName = wordname;

	    wordIndexArray= new IIndexWord[4];
	    wordIndexArray[0]= dict.getIndexWord(wordname, POS.NOUN);
	    wordIndexArray[1]= dict.getIndexWord(wordname, POS.VERB);
	    wordIndexArray[2]= dict.getIndexWord(wordname, POS.ADJECTIVE);
	    wordIndexArray[3]= dict.getIndexWord(wordname, POS.ADVERB);	
	    showExample = new boolean[4][100];
	}
	
	void initDic(String wnhome){
		if(wnhome == null)wnhome = System.getenv("WNHOME");
	    if(wnhome == null)System.out.println("can not get WNHOME environment path");
	   // else System.out.println("The env arg is "+wnhome);
	    String path = wnhome + File.separator + "dict";
	    URL url = null;
	    try{ url = new URL("file", null, path); } 
	    catch(MalformedURLException e){ e.printStackTrace(); }
	    if(url == null){System.out.println("Can't find file"); return;}
	    
	    dict = new Dictionary(url);
	    try{
	    	 dict.open();
	    }catch (Exception e){System.out.println("Can't open the Dictionary");e.printStackTrace();}
	    words = new String[160000];
	    map = new HashMap<String,String>();
	    String fileName = path + File.separator +"verb.exc";
	    FileInputStream inputStream = null;
	    String trans,ori;
	    try{
	    	inputStream = new FileInputStream(fileName);
	    	Scanner scanner = new Scanner(inputStream);
	    	while(scanner.hasNext()){
		    	trans = scanner.next();
		    	ori = scanner.nextLine();
		    	if(ori.indexOf(' ') > 0)ori = ori.substring(0,ori.indexOf(' '));
		    	map.put(trans, ori);
	    	}
	    	inputStream.close();
	    	scanner.close();
	    	fileName = path + File.separator + "noun.exc";
	    	inputStream = new FileInputStream(fileName);
	    	scanner = new Scanner(inputStream);
	    	while(scanner.hasNext()){
		    	trans = scanner.next();
		    	ori = scanner.nextLine();
		    	if(ori.indexOf(' ') > 0)ori = ori.substring(0,ori.indexOf(' '));
		    	map.put(trans, ori);
	    	}
	    	inputStream.close();
	    	scanner.close();
	    	fileName = path + File.separator + "adj.exc";
	    	inputStream = new FileInputStream(fileName);
	    	scanner = new Scanner(inputStream);
	    	while(scanner.hasNext()){
		    	trans = scanner.next();
		    	ori = scanner.nextLine();
		    	if(ori.indexOf(' ') > 0)ori = ori.substring(0,ori.indexOf(' '));
		    	map.put(trans, ori);
	    	}
	    	inputStream.close();
	    	scanner.close();
	    	fileName = path + File.separator + "adv.exc";
	    	inputStream = new FileInputStream(fileName);
	    	scanner = new Scanner(inputStream);
	    	while(scanner.hasNext()){
		    	trans = scanner.next();
		    	ori = scanner.nextLine();
		    	if(ori.indexOf(' ') > 0)ori = ori.substring(0,ori.indexOf(' '));
		    	map.put(trans, ori);
	    	}
	    	inputStream.close();
	    	scanner.close();
	    	fileName = path + File.separator + "index.sense";
	    	inputStream = new FileInputStream(fileName);
	    	scanner = new Scanner(inputStream);
	    	String lastWord = scanner.nextLine();
	    	lastWord = lastWord.substring(0,lastWord.indexOf('%'));
	    	String thisWord;
	    	words[wordsTop++]=lastWord;
	    	while(scanner.hasNext()){
		    	thisWord = scanner.nextLine();
		    	thisWord = thisWord.substring(0,thisWord.indexOf('%'));
		    	if(thisWord.equals(lastWord))continue;
		    	words[wordsTop++]=thisWord;
		    	lastWord = thisWord;
	    	}
	    	inputStream.close();
	    	scanner.close();
	    }catch (FileNotFoundException e){
			System.out.println("IOERROR: " +fileName +" NOT found\n");
		//	e.printStackTrace();
		}catch(IOException e){		System.out.println("IOERROR: "+e.getMessage()+"\n");
			e.printStackTrace();
		}
	}
	public String getT(){
		if(wordName==null)return "";
		String ret = "<html><body>";
		ret+= "<center><h1><b>"+wordName.replaceAll("_", " ")+"</b></h1></center>";
		for(int i=0;i<4;++i){
			if(wordIndexArray[i]==null)continue;
			ret+="<h2><b>"+POfS[i]+".</h2><b>";
			List<IWordID> temp = wordIndexArray[i].getWordIDs();
			for(int j=0;j<temp.size();++j){
				IWordID wordID = temp.get(j);
			ISynset synset = dict.getWord(wordID).getSynset();
			String gloss = synset.getGloss();
			ret+="<li>"+parseMean(gloss)+parseSynset(synset)+parseExample(gloss,i,j)+"</li>";
			}
		}
		ret+="</body></html>";
		return ret;
	}
	public void setShow(int i, int j)
	{
		showExample[i][j] = !showExample[i][j];
	}
	/*
	 * getMap
	 * 返回所有关联词的map
	 * map中的key值可以用map.get(WPane.TXX)(如 WPane.TSYNSET)进行检索,返回值为String[],即符合条件的词集合
	 * 值得注意的是如果没有属于该属性的词则该词集不会出现在map中,如果调用map.get()会返回null,建议进行检查
	 * 无参的getMap返回所有词集
	 * 有参的getMap返回nType(nNOUN,nVERB,nADJ,nADV中一个)的第order个义项对应的map
	 * 
	 * 
	 * */
	public Map<String,String[]> getMap(){
		return getTMap();
	}
	public Map<String,String[]> getMap(int nType,int order){
		return getTMap(nType,order);
	}
	String parseSynset(ISynset synset){
		String ret = "";
		if(synset.getWords().size()==1)return ret;
		ret+="<p>Synsets:&nbsp&nbsp&nbsp";
		for(IWord word:synset.getWords()){
			String t=word.getLemma();
			if(!t.equals(wordName)){
				ret += "<a href="+t+">"+t.replaceAll("_", " ")+"</a>&nbsp&nbsp&nbsp";
			}
		}
		
		return ret;
	}
	String parseMean(String gloss){

		if(gloss.indexOf("; \"")<0)return gloss;
		return gloss.substring(0,gloss.indexOf("; \""));
	}
	String parseExample(String gloss,int i,int j){
		String wordMatch =wordName.toLowerCase().replaceAll("_", " ")+" ";
		String wordMatch2 =" "+wordMatch.replaceFirst(" ", "s ");
		String wordMatch3;
		wordMatch3 = wordMatch.replaceFirst("f ", "fe ");
		wordMatch3 = wordMatch3.replaceFirst("fe ","v ");
		wordMatch3 =" "+ wordMatch3.replaceFirst(" ","es ");
		String wordMatch4;
		wordMatch4 = wordMatch.replaceFirst("e ", " ");
		wordMatch4 =" "+ wordMatch4.replaceFirst(" ","ed ");
		wordMatch = " "+wordMatch;
		if(gloss.indexOf("; \"")<0)return "";
		gloss = gloss.substring(gloss.indexOf("; \"")+1);
		if(transfer(gloss).indexOf(wordMatch)<0&&transfer(gloss).indexOf(wordMatch2)<0&&transfer(gloss).indexOf(wordMatch3)<0&&transfer(gloss).indexOf(wordMatch4)<0)return "";
		if(!showExample[i][j])return  "	<p align=\"right\"><a href="+i+","+j+">[show_example]</a>";
		String ret ="<p align=\"right\"><a href="+i+","+j+">[hide example]</a>" + "<p><b>Examples:</b></p>"+ "<ol>";
		String temp,temp2;
		while(gloss.indexOf(";")>0){
			temp = gloss.substring(2,gloss.indexOf(';')-1);
			temp2 = transfer(temp);
			if(temp2.indexOf(wordMatch)>=0||temp2.indexOf(wordMatch2)>=0||temp2.indexOf(wordMatch3)>=0||temp2.indexOf(wordMatch4)>=0){ret+="<li>"+temp+"</li>";}
			//else System.out.println(temp2);
			gloss = gloss.substring(gloss.indexOf(";")+1);
		}
		temp2 = transfer(gloss);
		if(temp2.indexOf(wordMatch)>=0||temp2.indexOf(wordMatch2)>=0||temp2.indexOf(wordMatch3)>=0||temp2.indexOf(wordMatch4)>=0)ret+="<li>"+gloss.substring(2,gloss.length()-1)+"</li>";
		ret+="</ol>";
		return ret;
	}
	@SuppressWarnings("resource")
	String transfer(String str){
		String ret=" ";
		str = str.toLowerCase();
		Scanner scanner = new Scanner(str);
		scanner = scanner.useDelimiter("(\"| |,|\\.|\\?|!|;)");
		while(scanner.hasNext()){
			String t = scanner.next();
			if(map.containsKey(t)){t = map.get(t);}
			if(!t.equals(""))
				ret+=t+" ";
		}
		ret = ret.replaceAll("ies ", "y ");
		ret = ret.replaceAll("ied ", "y ");
		ret = ret.replaceAll("  ", " ");
	//	System.out.println(ret);
		
		return ret;
	}
	@SuppressWarnings("unchecked")
	protected Map<String,String[]> getTMap(){
		Map<String,String[]>ret = new HashMap<String, String[]>();
		Object gmap[][]=new Object[wordIndexArray.length][];
		for(int p=0;p<wordIndexArray.length;++p){
			IIndexWord tids = wordIndexArray[p];
			if(tids!=null)gmap[p] = new Object[tids.getWordIDs().size()];
			else gmap[p] = new Object[0];
		}
		for(int p=0;p<wordIndexArray.length;++p){
			int size;
			if(wordIndexArray[p]!=null)size = wordIndexArray[p].getWordIDs().size();
			else size=0;
			for(int j=0;j<size;++j){
				gmap[p][j] = getTMap(p,j,0);
			}
		}
		for(int i=0;i<TARRAY.length;++i){
			Set<String> strset = new HashSet<String>();
			for(int p=0;p<wordIndexArray.length;++p){
				int size;
				if(wordIndexArray[p]!=null)size = wordIndexArray[p].getWordIDs().size();
				else size=0;
				for(int j=0;j<size;++j){
					if(((Map<String,Set<String>>)gmap[p][j]).containsKey(TARRAY[i]))strset.addAll(((Map<String,Set<String>>)gmap[p][j]).get(TARRAY[i]));
				}
			}
			if(!strset.isEmpty())ret.put(TARRAY[i], strset.toArray(new String[strset.size()]));
		}
		return ret;
	};
	protected Map<String,String[]> getTMap(int nType,int nOrder){
		Map<String,String[]> ret = new HashMap<String,String[]>();
		Map<String,Set<String>> gmap = getTMap(nType,nOrder,0);
		for(int i=0;i<TARRAY.length;++i){
			if(gmap.containsKey(TARRAY[i]))ret.put(TARRAY[i], gmap.get(TARRAY[i]).toArray(new String[gmap.get(TARRAY[i]).size()]));
		}
		return ret;
	}
	public int[] getPOS_NUM(){
		int[] ret = new int[wordIndexArray.length];
		for(int i=0;i<wordIndexArray.length;++i){
			if(wordIndexArray[i]!=null)
			ret[i] = wordIndexArray[i].getWordIDs().size();
			else ret[i]=0;
		}
		return ret;
	};
	public String getWord(){
		return wordName;
	}
	public String[] getNextWords(String word,int length){
		word = word.replaceAll(" ", "_");
		int index = BSearch(word,0,wordsTop);
		String ret[] = new String[length];
		for(int i=0;i<length;++i){
			ret[i]=words[index+i];
		}
		return ret;
	}
	private Map<String,Set<String>> getTMap(int nType,int nOrder,int n){
		if(nType>=wordIndexArray.length)return null;
		Map<String,Set<String>>ret = new HashMap<String, Set<String>>();
		IIndexWord iarray = wordIndexArray[nType];
		IWordID wordID = iarray.getWordIDs().get(nOrder);
		Map<IPointer, List<IWordID>> wordMap =  dict.getWord(wordID).getRelatedMap();
		Map<IPointer, List<ISynsetID>> synMap = dict.getSynset(wordID.getSynsetID()).getRelatedMap();
		Set<String> stemp;
		if((stemp = fillList(Pointer.ALSO_SEE,wordMap,synMap))!=null)ret.put(TALSO_SEE,stemp);
		if((stemp = fillList(Pointer.ANTONYM,wordMap,synMap))!=null)ret.put(TANTONYM,stemp);
		if((stemp = fillList(Pointer.ATTRIBUTE,wordMap,synMap))!=null)ret.put(TATTRIBUTE,stemp);
		if((stemp = fillList(Pointer.CAUSE,wordMap,synMap))!=null)ret.put(TCAUSE,stemp);
		if((stemp = fillList(Pointer.DERIVATIONALLY_RELATED,wordMap,synMap))!=null)ret.put(TDERIVATIONALLY_RELATED,stemp);
		if((stemp = fillList(Pointer.DERIVED_FROM_ADJ,wordMap,synMap))!=null)ret.put(TDERIVED_FROM_ADJ,stemp);
		if((stemp = fillList(Pointer.DOMAIN,wordMap,synMap))!=null)ret.put(TDOMAIN,stemp);
		if((stemp = fillList(Pointer.ENTAILMENT,wordMap,synMap))!=null)ret.put(TENTAILMENT,stemp);
		if((stemp = fillList(Pointer.HOLONYM_MEMBER,wordMap,synMap))!=null)ret.put(THOLONYM_MEMBER,stemp);
		if((stemp = fillList(Pointer.HOLONYM_PART,wordMap,synMap))!=null)ret.put(THOLONYM_PART,stemp);
		if((stemp = fillList(Pointer.HOLONYM_SUBSTANCE,wordMap,synMap))!=null)ret.put(THOLONYM_SUBSTANCE,stemp);
		if((stemp = fillList(Pointer.HYPERNYM,wordMap,synMap))!=null)ret.put(THYPERNYM,stemp);
		if((stemp = fillList(Pointer.HYPERNYM_INSTANCE,wordMap,synMap))!=null)ret.put(THYPERNYM_INSTANCE,stemp);
		if((stemp = fillList(Pointer.HYPONYM,wordMap,synMap))!=null)ret.put(THYPONYM,stemp);
		if((stemp = fillList(Pointer.HYPONYM_INSTANCE,wordMap,synMap))!=null)ret.put(THYPONYM_INSTANCE,stemp);
		if((stemp = fillList(Pointer.MEMBER,wordMap,synMap))!=null)ret.put(TMEMBER,stemp);
		if((stemp = fillList(Pointer.MERONYM_MEMBER,wordMap,synMap))!=null)ret.put(TMERONYM_MEMBER,stemp);
		if((stemp = fillList(Pointer.MERONYM_PART,wordMap,synMap))!=null)ret.put(TMERONYM_PART,stemp);
		if((stemp = fillList(Pointer.MERONYM_SUBSTANCE,wordMap,synMap))!=null)ret.put(TMERONYM_SUBSTANCE,stemp);
		if((stemp = fillList(Pointer.PARTICIPLE,wordMap,synMap))!=null)ret.put(TPARTICIPLE,stemp);
		if((stemp = fillList(Pointer.PERTAINYM,wordMap,synMap))!=null)ret.put(TPERTAINYM,stemp);
		if((stemp = fillList(Pointer.REGION,wordMap,synMap))!=null)ret.put(TREGION,stemp);
		if((stemp = fillList(Pointer.REGION_MEMBER,wordMap,synMap))!=null)ret.put(TREGION_MEMBER,stemp);
		if((stemp = fillList(Pointer.SIMILAR_TO,wordMap,synMap))!=null)ret.put(TSIMILAR_TO,stemp);
		if((stemp = fillList(Pointer.TOPIC,wordMap,synMap))!=null)ret.put(TTOPIC,stemp);
		if((stemp = fillList(Pointer.TOPIC_MEMBER,wordMap,synMap))!=null)ret.put(TTOPIC_MEMBER,stemp);
		if((stemp = fillList(Pointer.USAGE,wordMap,synMap))!=null)ret.put(TUSAGE,stemp);
		if((stemp = fillList(Pointer.USAGE_MEMBER,wordMap,synMap))!=null)ret.put(TUSAGE_MEMBER,stemp);
		if((stemp = fillList(Pointer.VERB_GROUP,wordMap,synMap))!=null)ret.put(TVERB_GROUP,stemp);
		List<IWord> synsetWords = dict.getSynset(wordID.getSynsetID()).getWords();
		if(synsetWords.size()>1){
			stemp = new TreeSet<String>();
			for(IWord tword:synsetWords){
				String tstr;
				if((tstr=tword.getLemma())!=wordName){
					stemp.add(tstr);
				}
			}
			ret.put(TSYNSET, stemp);
		}
		return ret;
	};
	Set<String> fillList(Pointer ptype,Map<IPointer, List<IWordID>> wordMap,Map<IPointer, List<ISynsetID>> synMap){
		Set<String> ts;
		List<IWordID> tw;
		List<ISynsetID> tsy;
		ts  = new TreeSet<String>();
		tw = wordMap.get(ptype);
		if(tw!=null){
			for(IWordID wid:tw){
				String tempString = wid.getLemma();
				if(tempString!=null)ts.add(tempString);
			}
		}
		tsy = synMap.get(ptype);
		if(tsy!=null){
			for(ISynsetID sid:tsy){
				List<IWord> t = dict.getSynset(sid).getWords();
				if(t!=null)
					for(IWord w:t){
						ts.add(w.getLemma());
					}
			}
		}
		if(!ts.isEmpty())return ts;
		return null;
	}
	public String BSearch(String name){
		return words[BSearch(name,0,wordsTop)].replaceAll("_", " ");
	}
	int BSearch(String name,int start,int end){
		int mid = (start+end)/2;
		int val = name.compareTo(words[mid]);
		if(val>0)return BSearch(name,mid+1,end);
		if(val == 0||mid == start)return mid;
		else return BSearch(name,start,mid);
	}
	public boolean hasWord(String name){
		name = name.replaceAll(" ", "_");
		String lowerName = name.toLowerCase();
		String ans = BSearch(lowerName).replaceAll(" ", "_");
		return lowerName.equals(ans);
	}
}
