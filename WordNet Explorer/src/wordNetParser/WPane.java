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
	 * ���ظ������ԵĴ�����
	 * ���޸ô���,��ӦΪ0
	 * ��:new WPane("word").getSynsetArray()[WPane.nADJ]��ʾ"word"�����ݴʵĸ���
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
	 * �������й����ʵ�map
	 * map�е�keyֵ������map.get(WPane.TXX)(�� WPane.TSYNSET)���м���,����ֵΪString[],�����������Ĵʼ���
	 * ֵ��ע��������û�����ڸ����ԵĴ���ôʼ����������map��,�������map.get()�᷵��null,������м��
	 * �޲ε�getMap�������дʼ�
	 * �вε�getMap����nType(nNOUN,nVERB,nADJ,nADV��һ��)�ĵ�order�������Ӧ��map
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
	 *�����ʵ��HyperlinkListener�ӿ�
	 *�ӿ��������
	public void hyperlinkUpdate(HyperlinkEvent e) {
	if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
		wpane.doWhenCalled(e.getDescription());
	}
		
	} 
	 *����e.getDescription()���صõ��Ĵ�,����õ�"num1,num2"��ʾ�ӵ�"show example"����,�����Ǵʵ���ת����,�õ���ԵĴ�
	 * */
	public void addHyperlinkListener(HyperlinkListener listener){
		super.addHyperlinkListener(listener);
	}
	public void doWhenCalled(String s){
		super.doWhenCalled(s);
	}
	/*
	 * ������ʾ�ĵ���
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
