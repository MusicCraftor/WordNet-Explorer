package wordNetBrowser;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import wordNetManager.WordDic;

public class WPane extends JEditorPane {
	public WPane(String wordname,String path){
		super();
		initEditorPane(path);
		initWord(wordname);
		setText(wdic.getT());
		setSelectionStart(0);
		setSelectionEnd(0);
		this.setEditable(false);
		this.setContentType("text/html");
	}
	public WPane(String wordname){
		this(wordname,null);
	}
	public WPane(){
		this("");
	}/*
	 * getSynsetArray
	 * 返回各个词性的词义数
	 * 若无该词义,对应为0
	 * 如:new WPane("word").getSynsetArray()[WPane.nADJ]表示"word"中形容词的个数
	 * */
	public int[] getSynsetArray()
	{
		return wdic.getPOS_NUM();
	}
	public int getSynsetArray(int nType){
		return getSynsetArray()[nType];
	}
	/*
	 *其必须实现HyperlinkListener接口
	 *接口中需添加
	public void hyperlinkUpdate(HyperlinkEvent e) {
	if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
		wpane.doWhenCalled(e.getDescription());
	}
		
	} 
	 *其中e.getDescription()返回得到的词,如果得到"num1,num2"表示接到"show example"命令,否则是词的跳转命令,得到相对的词
	 * */
	public void addHyperlinkListener(HyperlinkListener listener){
		super.addHyperlinkListener(listener);
	}
	public void doWhenCalled(String t){
		if(t.contains(",")){
			int i = Integer.parseInt(t.substring(0, t.indexOf(',')));
			t = t.substring(1+t.indexOf(','));
			int j = Integer.parseInt(t);
			wdic.setShow(i, j);
			int val=0;
			val = getSelectionStart();
			setText(wdic.getT());
			setSelectionStart(val);setSelectionEnd(val);
		}
		else{	
			initWord(t);
			setText(wdic.getT());
			setSelectionStart(0);setSelectionEnd(0);
		}
		
	}
	/*
	 * setWord
	 * 设置显示的单词
	 * */
	public void setWord(String word){
		initWord(word);
		setText(wdic.getT());
		setSelectionStart(0);setSelectionEnd(0);
	}
	
	public String getWord()
	{
		return wdic.getWord();
	}
	/*
	 * hasWord
	 * 寻找单词是否存在
	 * */
	public boolean hasWord(String s){
		return wdic.hasWord(s);
	}
	public WordDic getDic(){
		return wdic;
	}
	/*
	 * main:显示一个只有WPane的JFrame
	 * 
	 * */
	public static void main(String[] args){
		//*/
		JFrame jf = new JFrame();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		WPane dog= new WPane("run");
		dog.addHyperlinkListener(new HyperlinkAdapter(dog));
		jf.add(dog);
		JScrollPane jsp = new JScrollPane(dog);
		jf.add(jsp);
		jf.setVisible(true);
		jf.setSize(700,400);
		String[] getW = dog.getDic().getNextWords("anothe",10);
		for(String str:getW){System.out.println(str);}
		//*/
	}
	private static final long serialVersionUID = 1L;
	public static final String[] POfS={"Noun","Verb","Adj","Adv"}; 
	public static final int nNOUN=0;
	public static final int nVERB=1;
	public static final int nADJ= 2;
	public static final int nADV= 3;
	public static final String TSYNSET = WordDic.TSYNSET;
	public static final String TALSO_SEE=WordDic.TALSO_SEE;
	public static final String TANTONYM= WordDic.TANTONYM;
	public static final String TATTRIBUTE = WordDic.TATTRIBUTE;
	public static final String TCAUSE = WordDic.TCAUSE;
	public static final String TDERIVATIONALLY_RELATED = WordDic.TDERIVATIONALLY_RELATED;
	public static final String TDERIVED_FROM_ADJ = WordDic.TDERIVED_FROM_ADJ;
	public static final String TDOMAIN = WordDic.TDOMAIN;
	public static final String TENTAILMENT = WordDic.TENTAILMENT;
	public static final String THOLONYM_MEMBER = WordDic.THOLONYM_MEMBER;
	public static final String THOLONYM_PART = WordDic.THOLONYM_PART;
	public static final String THOLONYM_SUBSTANCE = WordDic.THOLONYM_SUBSTANCE;
	public static final String THYPERNYM = WordDic.THYPERNYM;
	public static final String THYPERNYM_INSTANCE = WordDic.THYPERNYM_INSTANCE;
	public static final String THYPONYM = WordDic.THYPONYM;
	public static final String THYPONYM_INSTANCE = WordDic.THYPONYM_INSTANCE;
	public static final String TMEMBER = WordDic.TMEMBER;
	public static final String TMERONYM_MEMBER = WordDic.TMERONYM_MEMBER;
	public static final String TMERONYM_PART = WordDic.TMERONYM_PART;
	public static final String TMERONYM_SUBSTANCE = WordDic.TMERONYM_SUBSTANCE;
	public static final String TPARTICIPLE = WordDic.TPARTICIPLE;
	public static final String TPERTAINYM = WordDic.TPERTAINYM;
	public static final String TREGION = WordDic.TREGION;
	public static final String TREGION_MEMBER = WordDic.TREGION_MEMBER;
	public static final String TSIMILAR_TO = WordDic.TSIMILAR_TO;
	public static final String TTOPIC = WordDic.TTOPIC;
	public static final String TTOPIC_MEMBER = WordDic.TTOPIC_MEMBER;
	public static final String TUSAGE = WordDic.TUSAGE;
	public static final String TUSAGE_MEMBER = WordDic.TUSAGE_MEMBER;
	public static final String TVERB_GROUP = WordDic.TVERB_GROUP;
	public static final String[] TARRAY = new String[]{TSYNSET,TALSO_SEE,TANTONYM,TATTRIBUTE,TCAUSE,TDERIVATIONALLY_RELATED,TDERIVED_FROM_ADJ
		,TDOMAIN,TENTAILMENT,THOLONYM_MEMBER,THOLONYM_PART,THOLONYM_SUBSTANCE,THYPERNYM,THYPERNYM_INSTANCE,THYPONYM,THYPONYM_INSTANCE,TMEMBER,TMERONYM_MEMBER
		,TMERONYM_PART,TMERONYM_SUBSTANCE,TPARTICIPLE,TPERTAINYM,TREGION,TREGION_MEMBER,TSIMILAR_TO,TTOPIC,TTOPIC_MEMBER,TUSAGE,TUSAGE_MEMBER,TVERB_GROUP};

	WordDic wdic;

	void initWord(String wordname){
	//	System.out.println(wordname);
		if(wdic.hasWord(wordname))
			{wdic.setWord(wordname);}
	}
	
	void initEditorPane(String path){
		this.setEditable(false);
		this.setContentType("text/html");
		wdic = new WordDic(path);
	}
}
class HyperlinkAdapter implements HyperlinkListener{
	WPane list;
	HyperlinkAdapter(WPane wa){
		list = wa;
	};
	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
			String t = e.getDescription();
			list.doWhenCalled(t);
		}
	
	}
} 