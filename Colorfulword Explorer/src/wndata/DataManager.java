/* coding = utf8 */

package wndata;

import java.io.*;
import java.util.*;

import coloring.*;
import mytrie.*;
/**
 * 鏉╂瑤閲滅猾璇茬安鐠囥儳绮伴崗銊ョ湰閻ㄥ嫬绨崇仦鍌濐嚢閸愭瑦褰佹笟娑欐暜閹镐緤绱濋張锟姐偨閼宠姤婀侀張鍝勫煑娣囨繆鐦夐弫缈犻嚋缁嬪绨稉顓炲涧閺堝绔存稉顏囩箹娑擃亞琚惃鍕杽娓氾拷鏉╂瑦鐗遍崣顏囶渽鐠囪褰囨稉锟筋偧缁便垹绱╅弬鍥︽,閼板奔绗栭幋鎴滄粦娑斿澧犵拋銊啈閻ㄥ嫮绱︾�涙ɑ婧�閸掓儼鍏樻潏鍐ㄣ偨閸︽澘褰傞幐銉ょ稊閻拷
 * 鐎圭偟骞囨稉濠冨灉閺勵垵绻栭弽鐤拷閾忔垹娈�.閻滄澘婀崗鍫滅瑝闂囷拷顪呴悽銊ヮ樉閺夊倻娈戦弫鐗堝祦缂佹挻鐎崢鏄忕槸閸ョ偓褰佹妯绘櫏閻滐拷閸欘垯浜掗崷銊ょ瀵拷顬婄亸鍗炵殺閹碉拷婀侀惃鍒琻dex閺傚洣娆㈡稉顓犳畱閺夛紕娲扮拠璇插煂閸愬懎鐡ㄦ稉锟介悽銊ょ娑撶嫻ap閹存牞锟芥禒锟界疄娣囨繂鐡ㄧ挧閿嬫降.閺屻儴顕楅惃鍕閸婏拷閻╁瓨甯撮崷銊ㄧ箹娑撶嫻ap娑擃厼褰囬崙铏规祲鎼存梻娈慖ndex閺夛紕娲版潻鏂挎礀鐏忓崬褰叉禒銉ょ啊
 * 婵″倹鐏夐弰顖濐渽閺岊櫚ynset閿涘奔缍橀崗鍫㈡箙缂佹瑥鍤惃鍑穣nset閺勵垯绗夐弰顖氭躬缂傛挸鐡ㄩ柌锟介弰顖滄畱鐠囨繄娲块幒銉ㄧ箲閸ワ拷娑撳秵妲搁惃鍕樈閸欘垯浜掗悽鈺搊rdNetFileReader鐠囪鍤弶锟�
**/

                                                             

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DataManager implements ColorStoreInfo                                                        //  妫版粏澹�,鐠嬪啴鏁�
{
	static Trie trie;
   static Synset[] n_cache;  //create cache for different types of words
   static Synset[] v_cache;
   static Synset[] a_cache;
   static Synset[] r_cache; //????
   static HashMap<String,String> n_index,          //the map from word to its line in index
                                 v_index,
                                 a_index,
                                 r_index;
   
    static HashMap<Integer,byte[]> color_map;                    //the map from a synset's hashcode to its color
   
   private static DataManager instance;                        //singleton class
   private DataManager(){}
   public static DataManager getSingleton(){
	if (instance==null){
	 instance=new DataManager();
	}
	return instance;
   }
   static                                                             //閸掓繂顬婇崠鏍ф健閿涘苯鐨ndex閸掑棛琚拠璇插弳閸愬懎鐡ㄩ敍灞间簰map瑜般垹绱＄�涙ê鍋�
   {
	 
	 RandomAccessFile raf; 
	 trie = new Trie();
	  n_cache = new Synset[10000];                    //缂傛挸鐡�    
	  v_cache = new Synset[10000];                    //缂傛挸鐡�
	  a_cache = new Synset[10000];                    //缂傛挸鐡�
	  r_cache = new Synset[10000];                    //缂傛挸鐡�
	 
	 File n_index_file = new File("./dict/index.noun");                //閸ユ稐閲滅槐銏犵穿閻ㄥ嫮娴夌�电鐭惧锟�
	 File v_index_file = new File("./dict/index.verb");
	 File a_index_file = new File("./dict/index.adj");                 //create File by relative address
	 File r_index_file = new File("./dict/index.adv");
	 
	 /*
	 File n_index_file = new File("./dict/index.noun");                //閸ユ稐閲滅槐銏犵穿閻ㄥ嫮娴夌�电鐭惧锟�
	 File v_index_file = new File("F:\\eclipse\\colorfulword\\src\\dict\\index.verb");
	 File a_index_file = new File("F:\\eclipse\\colorfulword\\src\\dict\\index.adj");
	 File r_index_file = new File("F:\\eclipse\\colorfulword\\src\\dict\\index.adv");
	 */
	 
	 
	  n_index = new HashMap<String,String>();                                     //閸ユ稓顬岄崡鏇＄槤瀵よ櫣鐝� 閸楁洝鐦濋崚鏉款嚠鎼存攰ndex鐞涘瞼娈憁ap
	  v_index = new HashMap<String,String>();
	  a_index = new HashMap<String,String>();                                     //four maps to store the lines in index files
	  r_index = new HashMap<String,String>();
	  
	 /*File temp = new File("../dict/preindex.adj");
	 try
	 {
	 RandomAccessFile raf_1 = new RandomAccessFile(temp,"r");                    //閸楁洜瀚崣鏍у毉閸撳秹娼伴惃鍕嚛閺勫海绱幋鎰瀮娴狅拷
	 }catch(FileNotFoundException e)
	 {
			e.printStackTrace();
	 }
	 long pre ;*/                                        	                 //鐠囧瓨妲戦惃鍕毐鎼达附宕茬悰宀�顑�
	 String wor,inde;                                                    //wor閺勵垰宕熺拠宥忕礉inde閺勵垵顕氱悰宀�鍌ㄥ鏇礉娴犳牔婊戦惃鍕嚠閺�鎯у弳map                                                                  	
	 String[] strs;                                                      //鐎佃鐦＄悰瀹╪dex閸掑洩鐦濋惃鍕殶缂侊拷
	 //long totallen;
	 
	 File[] index_file ={n_index_file,v_index_file,a_index_file,r_index_file};   //create array for the following circle
	 HashMap[] index_map = {n_index,v_index,a_index,r_index};
	 
	 /*for(int i =0 ;i<4;i++)
	 {
	     pre = raf_1.length();                                           //鐎规矮缍呴敍灞炬箒瀵板懘鐛欑拠锟�
		 raf = new RandomAccessFile(index_file[i], "r");                 //閸氬秷鐦�
	     totallen = raf.length();                                        //閸忋劍鏋冮梹鍨
	     do
	     {
		    raf.seek(pre);                          //閻╁瓨甯寸�规矮缍呴崚鐗堫劀閺傦拷
		    inde = raf.readLine();                  //鐠囪褰囨稉锟筋攽
		    strs = inde.split(" ");                 //閸掑洩鐦�
		    wor = strs[0];                          //閸欐牕宕熺拠锟�
		    index_map[i].put(wor,inde);                  //閺�鎯у弳map娣囨繂鐡�
		    pre += inde.length()+2;                   //缁夎鍩屾稉瀣╃鐞涘瞼娈戞担宥囩枂,2閺勵垱宕茬悰宀�顑�
	     }while(pre <= totallen);
	 }*/
	
	 /*for(int i = 0;i < 4 ;i++)
	 {
		 while((inde = raf.readLine()).length()!=0)
		 {
			 strs = inde.split(" ");
			 wor = strs[0];
			 if(wor.length()==0)
				 continue;
			 else
			 {
				 index_map[i].put(wor,inde);
			 }
			 
		 }
	 }*/  //鐟佸懓绻榤ap閻ㄥ嫮顑囨禍灞奸嚋閻楀牊婀�
   try
   { //System.out.println("try");
	 for(int i = 0;i < 4 ;i++)
	 {
		 //System.out.println("for");
		 raf = new RandomAccessFile(index_file[i], "r");        
		 while(true)
		 {
			 //System.out.println("true");
		    inde = raf.readLine();
		    
		    
		    if(inde==null)      //閺傚洣娆㈤張顐㈢啲                        //if this raf arrives the end, break
		       break;
			     
		    strs = inde.split(" ");                   // cut the line 
		    wor = strs[0];                            // get the word
		    //System.out.println(inde);
		    if(wor.length()==0)        //鐠哄疇绻冨锟姐仈閻ㄥ嫯顕╅弰锟�   
		    {
		    	//System.out.println("skip");                 
		    	continue;                          //skip the first interpretations
		    }
		    else
		    {
			 index_map[i].put(wor,inde);   //閺�鎯у弳map     //put them into maps
			 trie.insert(wor);                             // put into trie
		    }
		    //System.out.println("true end");
		}
		 raf.close();
	 }
   }catch(FileNotFoundException e)                        //deal with errors
	 {
		e.printStackTrace();
     }
     catch(IOException e)
     {
		e.printStackTrace();
     }
	                                                             //????
     color_map = new HashMap<Integer,byte[]>();                 //read the current files of colors
     try
     {
       FileInputStream fis = new FileInputStream("store_color.txt");    // by byte stream
       int hash_code;
       while(true){
           byte[] cbuf = new byte[STORENUM*4];
    	   hash_code=0;
    	   int i=fis.read();                                        //read all the relationships ,put them into the maps
    	   if (i==-1)
    		   break;
    	   hash_code=i-'0';
    	   while ((i=fis.read())!=' '){
    		   hash_code=hash_code*10+i-'0';
    	   }
    	   fis.read(cbuf,0,STORENUM*4);
    	   color_map.put(hash_code,cbuf);
    	   
       }
       fis.close();
    }catch(IOException ioe)
    {
    	ioe.printStackTrace();
    }
  }	 
      
   
   
	
   public IndexEntry getIndex(String word,PartOfSpeech pos)   //閺嶈宓侀崡鏇＄槤 閸滃矁鐦濋幀锟介敍灞芥躬map娑擃厽鐓＄拠锟介敍宀冃掗弸鎰嫙鏉╂柨娲栭張澶婂彠缁便垹绱╅惃鍒琻dexentry缁拷
  {                                                          //get the outcome of dealing with index
	 word = word.replace(' ', '_');
	 word = word.toLowerCase();                           // pre operation
	 String index_line =null;
	 String[] strs;
		 
	 switch(pos)                    // open corresponding index files
	 {                                   //lemma pos  synset_cnt  p_cnt  [ptr_symbol...]  sense_cnt  tagsense_cnt   synset_offset  [synset_offset...] 
	  case NOUN :                        //abasement n 2 3 @ ~ + 2 1 14248927 00269679  
		 index_line = n_index.get(word); //  0       1 2 3 4 5 6 7 8     9     10                 protected String lemma;
		 break;
		 
	  case VERB:
		  index_line = v_index.get(word);
		  break;
	
	  case ADJ:
		  index_line = a_index.get(word);
		  break;
		  
	  case ADV:
		  index_line = r_index.get(word);
		  break;
		 
	  case ADJS:
		  index_line = a_index.get(word);
		  break;
		 
	 }
	     if(index_line == null) 
	    	 return null;
	     strs = index_line.split(" ");          //cut the line 閸掑洩鐦�                                                                    // protected PartOfSpeech pos;
		 //entry.lemma = word;                                                                              // protected int synset_cnt;
		 //entry.pos = pos;                                                                               //protected int p_cnt;
		 //entry.synset_cnt = strs[2];                                                                      //         protected String[] ptr_symbols;
		 //entry.p_cnt = strs[3];                                                                             //  protected int sense_cnt;
		 String ptr_sym[] = new String[Integer.parseInt(strs[3])];  //get data by its location                                            		 //protected int tagsense_cnt;
		 for(int i = 0 ;i<Integer.parseInt(strs[3]);i++)                                              			                //protected int[] synset_offsets;
		      ptr_sym[i] = strs[4+i];
		 //entry.tagsense_cnt = strs[5+strs[3]];
		 
		 int[] syn_offsets = new int[Integer.parseInt(strs[2])];        // pay attention to the change between different types of numbers
		 for(int i = 0 ;i < Integer.parseInt(strs[2]) ;i++)
			 syn_offsets[i] =Integer.parseInt(strs[6+Integer.parseInt(strs[3])+i]);
		 		                                                                            //create new object by constructors
		 IndexEntry entry = new IndexEntry(word,pos,Integer.parseInt(strs[2]),Integer.parseInt(strs[3]),ptr_sym,Integer.parseInt(strs[5+Integer.parseInt(strs[3])]),syn_offsets);	 
	     //IndexEntry(String lemma,PartOfSpeech pos,int synset_cnt,int p_cnt,String[] ptr_symbols,int tagsense_cnt,int[] synset_offsets)
	 return entry;
  }
   
   
   
   public Synset getSynset(int offset,PartOfSpeech pos)       //鐠囪褰噑ynset閿涳拷 閸忓牐绻橀崗銉х处鐎涙﹫绱濋張澶婂灟閻╁瓨甯存潻鏂挎礀閿涘苯鎯侀崚娆掔箻閸忣殎ata閿涘苯鍟撶紓鎾崇摠閿涘矁绁撮崐鑹扮箲閸ワ拷
   {                                                          //get synset by a word's offset and its attribute(n,v,r,a,s)
	   Synset   syn = new Synset();                           //鏉╂柨娲栭崣姗�鍣�
	   String[] strs;
	   String  data = null;                                          //鏉堝懎濮幏鍡氱槤
	   int add = offset%10000;                                     //  get the location in cache      鏉烆剚宕瞔ache閸︽澘娼�
	   int local,lenofgloss;                                      //assistant variables
	   //boolean judge_cache =false;
	   switch(pos)                                            // if the word has been in cache ,get out of and return it
	   {
	     case NOUN: 
		   if(n_cache[add]!=null && n_cache[add].getOffset() == offset && n_cache[add].getSSType().equals(pos))  //judge whether they are the same                          
		   {
			   return n_cache[add];
		   }
		   break;
		   
	     case VERB:
	    	 if(v_cache[add]!=null && v_cache[add].getOffset() == offset && v_cache[add].getSSType().equals(pos))
	    	 {
	    		 return v_cache[add];
	    	 }
	     break;	 
		   
	     case ADV:
	    	 if(r_cache[add]!=null && r_cache[add].getOffset() == offset && r_cache[add].getSSType().equals(pos))
	    	 {
	    		 return r_cache[add];
	    	 }
	     break;	
	     
	     case ADJ:
	    	 if(a_cache[add]!=null && a_cache[add].getOffset() == offset && a_cache[add].getSSType().equals(pos))
	    	 {
	    		 return a_cache[add];
	    	 }
	     break;	
	     
	     case ADJS:
	    	 if(a_cache[add]!= null && a_cache[add].getOffset() == offset && a_cache[add].getSSType().equals(pos))
	    	 {
	    		 return a_cache[add];
	    	 }
	     break;	
	  }  
	     
	     RandomAccessFile raf = null;
	     try{
	     switch(pos)                //open corresponding data file 
	     {
	        case NOUN:
	        	/*RandomAccessFile*/ raf = new RandomAccessFile(new File("./dict/data.noun"),"r");
	        break;
	        
	        case VERB:
	        	/*RandomAccessFile*/ raf = new RandomAccessFile(new File("./dict/data.verb"),"r");
	        break;
	        
	        case ADV:
	        	/*RandomAccessFile*/ raf = new RandomAccessFile(new File("./dict/data.adv"),"r");
	        break;
	        
	        case ADJ:
	        	/*RandomAccessFile*/ raf = new RandomAccessFile(new File("./dict/data.adj"),"r");
	        break;
	        
	        case ADJS:
	        	/*RandomAccessFile*/ raf = new RandomAccessFile(new File("./dict/data.adj"),"r");
	        break;
	        default:
	            
	     }
	      raf.seek(offset);                       // get the location of word in data file
		  data = raf.readLine();
	      raf.close();
	     }catch(FileNotFoundException e)
		 {
				e.printStackTrace();
		 }
		 catch(IOException e)
		 {
				e.printStackTrace();
		 }
	     
		                                               //                                                                  顢�
		  strs = data.split(" ");                      //cut the line,then get different elements by its location
          // synset_offset  lex_filenum  ss_type  w_cnt  word  lex_id  [word  lex_id...]  p_cnt  [ptr...]  [frames...]  |   gloss 
		//    00072592        04          n        02        omission 1 skip 0             005   @ 00068933 n 0000   + 02588754 v 0202   + 00607944 v 0105 + 00607346 v 0103 ~ 00064448 n 0000 | a mistake resulting from neglect  			  
		  //int offset = Integer.parseInt(strs[0]);                                // protected int offset;
		  int lex_filenum = Integer.parseInt(strs[1]);                           // protected int lex_filenum;
		  PartOfSpeech ss_type = PartOfSpeech.forString(strs[2]);                            // protected PartOfSpeech ss_type;
		  int w_cnt = Integer.parseInt(strs[3], 16);       //16鏉╂稑鍩�         // protected int w_cnt;
			
		  WordSense[] wordsen = new WordSense[w_cnt];
		                                       		  // // protected int lex_id;閺佹壆绮�
		  for(int i =0;i<w_cnt;i++)                                                        
		  {
			   //if(strs[2]=="a"||strs[2]=="s")  //adj 閸滃畮djs鐟曚礁骞撻幒濉礱rker
			  if(pos.equals(PartOfSpeech.forString("a"))||pos.equals(PartOfSpeech.forString("s")))
			  {                                           // for some adjective word, we should cut the suffix of(a) or (p),saving it as another attribute
				String worsen_tmp = strs[4+2*i];
				if(worsen_tmp.contains("("))                               //if contains '('
				{	
				  int loc = worsen_tmp.lastIndexOf("(");                    //cut the word
				  String way_put = worsen_tmp.substring(loc+1,loc+2);
				  worsen_tmp = worsen_tmp.substring(0,loc);
				  
				  wordsen[i] = new WordSense(worsen_tmp,Integer.parseInt(strs[5+2*i],16),way_put);
				}
				else
				{
					wordsen[i] = new WordSense(worsen_tmp,Integer.parseInt(strs[5+2*i],16)); //create new object
				}
			  }
			  else
			  {
				  wordsen[i] = new WordSense(strs[4+2*i],Integer.parseInt(strs[5+2*i],16));
			  }  //02575056 41 v 01 take 4 001 @ 02344645 v 0000 01 + 08 00 | carry out
		  }//02227391 40 v 05 find 2 happen c chance c bump c encounter 0 003 + 07313943 n 0502 + 00041548 n 0102 + 09940447 n 0101 01 + 08 00 | come
		  
		  local = 4+2*w_cnt;//6
		  int p_cnt = Integer.parseInt(strs[local]);                                  // protected int p_cnt;
			  
			  SynsetPointer[] ptrs = new SynsetPointer[p_cnt];                                // protected SynsetPointer[] ptrs;        
			   for(int i = 0;i<p_cnt ;i++)
			   {
				   //PointerSymbol pointer_symbol,int synset_offset,PartOfSpeech pos,int source_target
				   ptrs[i] = new SynsetPointer(PointerSymbol.forString(strs[local+1+i*4]),Integer.parseInt(strs[local+2+i*4]),PartOfSpeech.forString(strs[local+3+i*4]),Integer.parseInt(strs[local+4+i*4],16));
				   //syn.ptrs[i].PointerSymbol = strs[local+1+i*4];                   //
 				   //syn.ptrs[i].synset_offset = Integer.parseInt(strs[local+2+i*4]);  //閺嬪嫰锟介崙鑺ユ殶
				   //syn.ptrs[i].pos = strs[local+3+i*4];   // forstring
				   //syn.ptrs[i].source_target = strs[local+4+i*4];    //娑撱倗绮嶉崡浣稿彋鏉╂稑鍩楅幏鐓庡毉閺夈儳娈�  SynsetPointer闁插矂娼伴悽鈺t鐠佹澘缍嶆潻娆庤⒈缂佸嫭鏆�
			   }
//	0		1 2  3  4     5  6  7   8      9  10  11  12     13  14 15 16 17 18
//00006735 29 v 01 wheeze 0 002 @ 00001740 v 0000 + 00824340 n 0101 01 + 02 00 | breathe with difficulty			   
			   if(pos.equals(PartOfSpeech.forString("v")))               //for a verb,it has frames,needing special operation 
			   {
				   local += 4*p_cnt+1;    //鐎规矮缍呴崚鐨�umofframes  15
				   int lenofframes = Integer.parseInt(strs[local]);//1
				   SynsetFrame[] frames =new SynsetFrame[lenofframes];
				   for(int i =0 ;i< lenofframes ;i++)
				   {                                        //            17                                  18
					   frames[i] = new SynsetFrame(Integer.parseInt(strs[local+2+3*i]),Integer.parseInt(strs[local+3+3*i],16));
					   //syn.frames[i].f_num = Integer.parseInt(strs[local+2+2*i]);
				       //syn.frames[i].w_num = Integer.parseInt(strs[local+3+2*i]);
				   }
				   		   
				   
				   lenofgloss = strs.length-local-2-3*lenofframes;//-3-2*lenofframes;     // save glosses;
				   String[] glosses = new String[1];//String[] glosses = new String[lenofgloss];
				   glosses[0] = "";
				   for(int i =0; i<lenofgloss ;i++)
				   {
					   
					   glosses[0] += strs[local+3*lenofframes+2+i];//p_cnt*4+2+i];
				       glosses[0] +=" ";
				   }
				   				  
				   
				   
				   syn =new Synset(offset,lex_filenum,ss_type,w_cnt,wordsen,p_cnt,ptrs,frames,glosses); 
			   }
			   else
			   {                                                // save glosses
			      lenofgloss = strs.length-local-p_cnt*4-2;     // protected String[] glosses;
			      String[] glosses = new String[1];//String[] glosses = new String[lenofgloss];
			      glosses[0]="";
			      for(int i =0; i<lenofgloss ;i++)
			      {
			    	 
				     glosses[0] += strs[local+p_cnt*4+2+i];
				     glosses[0] +=" ";
			      }
			   		                          
			      syn =new Synset(offset,lex_filenum,ss_type,w_cnt,wordsen,p_cnt,ptrs,glosses); //create new object
	//public Synset(int offset,int lex_filenum,PartOfSpeech ss_type,int w_cnt,WordSense[] words,int p_cnt,SynsetPointer[] ptrs,SynsetFrame[] frames,String[] glosses)		   
			   }
			   switch(pos) //put the new object into cache
			   {
			     case NOUN:
				    n_cache[add] = syn;                 //閺�鎯у弳缂傛挸鐡�
			     break;
			     
			     case VERB:
					    v_cache[add] = syn;                 //閺�鎯у弳缂傛挸鐡�
				 break;
			     
			     case ADJ:
					    a_cache[add] = syn;                 //閺�鎯у弳缂傛挸鐡�
				 break;
				 
			     case ADV:
					    r_cache[add] = syn;                 //閺�鎯у弳缂傛挸鐡�
				 break;
				 
			     case ADJS:
					    a_cache[add] = syn;                 //閺�鎯у弳缂傛挸鐡�
				 break;
			   }
        return syn;
   }
             
        /*       
			     
		 case VERB:  //婢舵矮绨� frame  
			 if(v_cache[add]!=null)                            //
			   {
				   return v_cache[add];
			   }
			 else
			 {
				   RandomAccessFile raf_n = new RandomAccessFile("../dict/data.verb");    //
				   raf.seek(offset);                                                                      //鐎规矮缍�
				   data = raf.readLine();                                                                 //鐠囨槒顢�
				   strs = data.split(" ");                                                                //閹峰棜鐦�
	         	//synset_offset  lex_filenum  ss_type  w_cnt  word  lex_id  [word  lex_id...]  p_cnt  [ptr...]  [frames...]  |   gloss 
			   //00003826 29                   v        02        hiccup 0 hiccough 0          003    @ 00001740 v 0000 + 14168180 n 0202 + 14168180 n 0101    01 + 02 00 | breathe spasmodically,                             
				   
				   syn.offset = strs[0];                                // protected int offset;
				   syn.lex_filenum = strs[1];                           // protected int lex_filenum;
				   syn.ss_type = strs[2];                            // protected PartOfSpeech ss_type;
				   syn.w_cnt = Integer.parseInt(strs[3], 16);       //16鏉╂稑鍩�         // protected int w_cnt;
				                                                                                  
				                                                                  // // protected int lex_id;閺佹壆绮�
				   local = 4+2*syn.w_cnt;
				   syn.p_cnt = Integer.parseInt(strs[local]);               // protected int p_cnt;
				  
				   syn.ptrs = new SynsetPointer[syn.p_cnt];        // protected SynsetPointer[] ptrs;        
				   for(int i = 0;i<syn.p_cnt ;i++)
				   {
					   syn.ptrs[i].PointerSymbol = strs[local+1+i*4];                   //
					   syn.ptrs[i].synset_offset = Integer.parseInt(strs[local+2+i*4]);
					   syn.ptrs[i].pos = strs[local+3+i*4];
					   syn.ptrs[i].source_target = strs[local+4+i*4];
				   }
				   
				                                       // protected SynsetFrame[] frames;
				   local += 4*syn.p_cnt+1;    //鐎规矮缍呴崚鐨�umofframes
				   int lenofframes = Integer.parseInt(strs[local]);
				   syn.frames =new SynsetFrame[lenofframes];
				   for(int i =0 ;i< lenofframes ;i++)
				   {
					   syn.frames[i].f_num = Integer.parseInt(strs[local+2+2*i]);
				       syn.frames[i].w_num = Integer.parseInt(strs[local+3+2*i]);
				   }
				   		   
				   
				   lenofgloss = strs.size()-local-2-2*lenofframes;     // protected String[] glosses;
				   syn.glosses = new String[lenofgloss];
				   for(int i =0; i<lenofgloss ;i++)
				   {
					   syn.glosses[i] = strs[local+syn.p_cnt*4+1+i];
				   }
                 
			   v_cache[add] = syn;                 //閺�鎯у弳缂傛挸鐡�	       
	   		   return syn;
			 }
	      break;				   
	   }//switch
   */
    
   
   
   
   public Synset[] lookup(String word, PartOfSpeech pos)      //鏉╂柨娲栭崡鏇＄槤鐎电懓绨茬拠宥嗭拷閻ㄥ嫭澧嶉張锟絪ynset
   {                                                      //return all the synset which has the word 
	   
	   IndexEntry tmp = getIndex(word,pos);     // create the indexentry
	   if(tmp == null)
	      return null;
	   int[] offsets = tmp.getSynsetOffets();   // get the word's offsets in data file
	   Synset[] syn = new Synset[tmp.getSynsetCount()];
	   for(int i =0 ;i < tmp.getSynsetCount();i++)
	   {
		   syn[i] = getSynset(offsets[i],pos);     //use the function get its synsets
	   }
	   return syn;   
   }
   
   
   public static void main(String[] args)    //test the opeartion of files
   {
	 System.out.println("start");
	
	 /*FileWriter fw =null;
	 try
	 {
		 fw = new FileWriter("./test.txt");
		 fw.write("sadfjk;l\r\n");
	 }catch(IOException ioe)
	 {
		 ioe.printStackTrace();
	 }*/
	 
	 
	 //public IndexEntry getIndex(String word,PartOfSpeech pos) 濞村鐦痝etindex   
		IndexEntry t1 = DataManager.getSingleton().getIndex("word", PartOfSpeech.forString("v"));
		//System.out.println(t.senseCount());
		//System.out.println(t.getLemma());
		//System.out.println(t.toString());
		int [] s = t1.getSynsetOffets();
		String[] uv =t1.getPtrSymbols();
		for(int i =0;i<s.length;i++)
			System.out.println(s[i]);
		
		for(int i =0;i<uv.length;i++)
		{
		   System.out.println(uv[i]);	
		}
          	 
	 
	 /*  test the second function
	 Synset x = new DataManager().getSynset(779834,PartOfSpeech.forString("s"));
	 Synset t = new DataManager().getSynset(6319490,PartOfSpeech.forString("n"));
	 System.out.println(t.getOffset()+" "+x.getOffset());
	 System.out.println(t.getLexFilenum()+" "+x.getLexFilenum());
	*/
	    Synset[] t = DataManager.getSingleton().lookup("word",PartOfSpeech.forString("v"));   
        
		
		   
		for(int i =0;i<t.length;i++)
		{
        	System.out.println(t[i].getLexFilenum()+" "+t[i].getLexId()+" "+t[i].getOffset()+" "+t[i].getPtrCount()+" "+t[i].getWordCount());
        	WordSense[] wo = t[i].getWords();
        	for(int j =0;j<wo.length;j++)
        	{
        		System.out.println(wo[j].getWord()+" "+wo[j].getLexId()+" "+wo[j].getway_put());
        	}
        	
        	SynsetPointer[] ptrs = t[i].getPointers();
        	for(int z =0;z<ptrs.length;z++)
        	{
        		System.out.println(ptrs[z].toString());
        	}
        	
        	String[] glo = t[i].getGlosses();
        	for(int k =0;k<glo.length;k++)
        	{
        		System.out.print(glo[k]+" ");
        		
        	}
        	System.out.println(".");
        	System.out.println(" these are the glosses of the "+ i+ "th synset");
        }
   
   }
   /**
    * 娴犮儰绗呴弰顖涙煀閸旂姴鍙嗛惃鍑渙lor閻╃鍙ч惃鍕嚢閸愭瑦鎼锋担婊愮礉閸掑棗鍩嗛弰鐤宱lor閻ㄥ嫬鍟撻崗銉ユ嫲鐠囪鍤幙宥勭稊閿涘瓔olor缁崵娈戞竟鐗堟鐠囷箒顬�Color閵嗕坎ava
    * By Zero閿涳拷
    */
   public boolean setColor(Synset synset, Colors colors){                  //set colors for a synset
	   
	   int code = synset.hashCode();
	   byte[] t = colors.serialize();//color_map.get(code);
       color_map.put(code, t);
	   return true;
   }
   public Colors getColor(Synset synset)   //get the synset's colors
   {
	   int code = synset.hashCode();
	   if(color_map.containsKey(code))
	   {
		   return new Colors(color_map.get(code));
	   }
	   else
	   {
	   return new Colors();
       }
   }
   public void writeColor()               //write current colors of synsets back to a file,storing them for the next time use
   {
	 try  
	 { 
	   FileOutputStream fos = new FileOutputStream("store_color.txt");
	   for(Integer key:color_map.keySet())
	   {
		   fos.write(key.toString().getBytes());
		   fos.write((byte) (' '));
		   fos.write(color_map.get(key));
	   }
	   fos.close();
	 }catch (IOException ioe)
	 {
		 ioe.printStackTrace();
	 }
   }
   public static Trie getTrie()
   {
	   return trie;
   }
   //鍒濆鍖栧潡璇绘枃浠讹紝hashcode鍜�10涓猚har锛岀粍鎴恗ap,  setColor锛宻ynset,hashcode锛岀紪杈�10涓猚har锛実etchar 鏄痵ynset鐭ラ亾hashcode锛屽啀寰楀埌10涓猚har. 鏈�鍚庤繕搴斿啓鍥瀖ap
}
 