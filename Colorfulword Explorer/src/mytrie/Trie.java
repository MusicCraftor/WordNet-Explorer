package mytrie;
/**
 * Tire�࣬���ʹ���һ��Trie��
 * @author Lostmonkey
 * version 1.0
 */
public class Trie {

	TrieNode root=new TrieNode();
	private void search(int level,TrieNode now,String st,WordList mList,boolean checkall){
		//level��ǰ�Ѷൽ��Ĳ���now��ǰ��ڵ㣬st������ַ�mList���ѵ��ĵ��ʱ?checkall�Ƿ�����ֹ�*�����ֹ���Ҫcheck���������е��ʡ�
		if (now==null) return;
		if (mList.getSize()>=mList.getMaxSize()) return;
		if (now.match(st)) mList.putWord(now.getWord());
		Character mCh='*';
		if (!checkall) mCh=st.charAt(level);
		if (mCh=='*') checkall=true;
		if (mCh=='?'||checkall)
			for (ChildLink tmp=now.getHead();tmp!=null;tmp=tmp.getNext())
				search(level+1,tmp.getChild(),st,mList,checkall);
		else if (mCh!='*'&&now.getChild(mCh)!=null)
			search(level+1,now.getChild(mCh),st,mList,checkall);
	}
	/**
	 * ��Trie���в���һ��ʡ�
	 */
	public void insert(String word){

		TrieNode now=root,next;
		for (int i=0;i<word.length();++i){
			next=now.getChild(word.charAt(i));
			if (next==null) next=now.addChild(word.charAt(i));
			now=next;
		}
		now.setWord(word);
	}
	/**
	 * ����һ��WordList���󣬱�ʾ��patternƥ��ĵ��ʡ�
	 */
	public WordList getWordList(String pattern,int maxsize){
		WordList mList=new WordList();
		mList.setMaxSize(maxsize);
		boolean b = true;
		for (int i=0;i<pattern.length();++i) 
			if (pattern.charAt(i)=='?'||pattern.charAt(i)=='*') b=false; 
		if (pattern.charAt(pattern.length()-1)!='*') pattern+='*';
		search(0,root,pattern,mList,false);
		for (int i=pattern.length()-1;b&&i>=0&&mList.getSize()==0;--i){
			char[] st=pattern.toCharArray();
			st[i]='?'; 
			String tmp=new String(st);
			search(0,root,tmp,mList,false);			
		}
		return mList;
	}
	
}

class ChildLink{
	/*
	 * ���ӵ�t����
	 * �������Ǹ���mChar����ַ��Ӧ�Ķ���
	 * �����ӵ�TrieNode����ΪmChild
	 *
	 */
	Character mchar;
	TrieNode mchild;
	ChildLink next;
	ChildLink(Character ch,TrieNode node){
		//���캯��
		mchar=ch;
		mchild=node;			
	}
	Character getChar(){
		return mchar;
	}
	TrieNode getChild(){
		return mchild;
	}
	void setNext(ChildLink node){
		next=node;
	}
	ChildLink getNext(){
		return next;
	}
};

class TrieNode{
	/*
	 * TireNode�࣬��ʾTrie���һ��ڵ㡣
	 * ��˽ڵ��Ӧһ��ʣ���mWordΪ�˵��ʷ���Ϊnull��
	 * ������ChildLink��t��ṹ��ʾ��
	 *
	 */
	String mWord;
	ChildLink head;
	TrieNode(){
		head=null;
		mWord=null;
	}
	private ChildLink getByChar(Character ch){
		//�õ�����t����ch����ַ��Ӧ�Ľڵ�
		ChildLink pre=null;
		for (ChildLink tmp=head;tmp!=null&&tmp.getChar()<=ch;tmp=tmp.getNext()) pre=tmp;
		return pre;
	}
	TrieNode getChild(Character ch){
		//�õ�ch�ַ��Ӧ�Ķ���
		ChildLink pre=getByChar(ch);
		if (pre==null||pre.getChar()<ch) return null;
		else return pre.getChild();
	}
	TrieNode addChild(Character ch){
		//���һ��ch�ַ��Ӧ�Ľڵ�
		ChildLink pre=getByChar(ch);
		ChildLink tmp=new ChildLink(ch,new TrieNode());
		if (pre==null){
			tmp.setNext(head);
			head=tmp;
		} else {
			tmp.setNext(pre.getNext());
			pre.setNext(tmp);
		} 
		return tmp.getChild();
	}
	void setWord(String word){
		mWord=word;
	}
	String getWord(){
		return mWord;
	}
	ChildLink getHead(){
		return head;
	}
	
	boolean match(String pattern){
		//�ж��ҵĵ����Ƿ��patternƥ�䡣
		if (mWord==null) return false;
		//Ӧ�ö�̬�滮�㷨��
		boolean[][] f=new boolean[mWord.length()+1][pattern.length()+1];
		f[0][0]=true;
		if (pattern.charAt(0)=='*')
			for (int i=0;i<=mWord.length();++i) f[i][1]=true;
		for (int i=1;i<=mWord.length();++i)
			for (int j=1;j<=pattern.length();++j){
				char ch1=mWord.charAt(i-1);
				char ch2=pattern.charAt(j-1);
				if (ch2=='?'||ch1==ch2)
					f[i][j]=f[i][j]|f[i-1][j-1];
				if (ch2=='*') 
					f[i][j]=f[i][j]|f[i-1][j]|f[i][j-1];
			}
		return f[mWord.length()][pattern.length()];
	}
	
}