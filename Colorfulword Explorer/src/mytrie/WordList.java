package mytrie;
import java.util.*;

/**
 * WordList�࣬�����б���
 */
public class WordList {
	private ArrayList<String> strList=new ArrayList<String>();
	private int maxsize;
	/**
	 * �޲ι��캯��
	 */
	public WordList(){
		strList.clear();
		maxsize=0;
	}
	/**
	 * ��Word�����List
	 */
	public void putWord(String word){
		if (strList.size()<maxsize)
			strList.add(word);
	}
	/**
	 * �õ���index��ʣ���0���
	 */
	public String getWord(int index){
	
		return strList.get(index);
	}
	/**
	 * �õ�List��������
	 */		
	public int getSize(){
		return strList.size();
	}
	/**
	 * �õ��������ܺ���󵥴���
	 */
	public int getMaxSize(){
	
		return maxsize;
	}
	/**
	 * �����б������ܺ���󵥴���
	 */
	public void setMaxSize(int size){
		maxsize=size;
	}
}
