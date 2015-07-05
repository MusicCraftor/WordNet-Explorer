package wordNetParser;

import java.util.*;

import javax.swing.event.HyperlinkListener;

public class WPane extends WnTextArea {
	private static final long serialVersionUID = 1L;

	public WPane(String word){
		super(word);
	}
	/*
	 * getSynsetArray
	 * 返回各个词性的词义数
	 * 若无该词义,对应为0
	 * 如:new WPane("word").getSynsetArray()[WPane.nADJ]表示"word"中形容词的个数
	 * */
	public int[] getSynsetArray()
	{
		return super.getPOS_NUM();
	}
	public int getSynsetArray(int nType){
		return getSynsetArray()[nType];
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
		return super.getTMap();
	}
	public Map<String,String[]> getMap(int nType,int order){
		return super.getTMap(nType,order);
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
	public void doWhenCalled(String s){
		super.doWhenCalled(s);
	}
	/*
	 * 设置显示的单词
	 * */
	public void setWord(String word){
		initWord(word);
		setText(getT());
		setSelectionStart(0);setSelectionEnd(0);
	}
	/*
	static public void main(String[] args){
		WPane pane = new WPane("word");
		Map<String, String[]> map = pane.getMap();
		for(int i=0;i<TARRAY.length;++i)
		if(map.containsKey(TARRAY[i]))System.out.println(map.get(TARRAY[i])[0]);
	}*/
}
