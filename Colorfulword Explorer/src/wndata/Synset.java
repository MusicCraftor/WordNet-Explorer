package wndata;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
//LZYNOTE:鐎瑰啫婀弸鍕拷锟界姾绻栨禍娑楃鐟楄法娈戦弮璺猴拷锟芥瑧鏁ら惃鍕Ц娑擄拷锟芥稉鐚篹ad閺傝纭舵禒搴″従鐏忎浇顥栭惃鍒塷rdNetFileReader缁鑵戠拠缁樻殶閹诡噯绱濇担鐘插讲娴犮儲濡告潻娆庨嚋WordNetFileReader閹肩偞鍣﹂敍宀�1�7鏁ょ�1�7瑰啰娈慠ead閺傝纭堕敍灞惧灗閼板懓鍤滃鍗炵杽閻滈绔存稉顏囶嚢閺佺増宓侀惃鍕拷锟芥槒绶敍灞藉晙閸愭瑤绔存稉顏呯�1�7柅鐘插毐閺佽埇锟斤拷锟斤拷
public class Synset {

    protected int offset;
    protected int lex_filenum;
    protected PartOfSpeech ss_type;
    protected int w_cnt;
   
    protected WordSense[] words;
    protected int lex_id;
    protected int p_cnt;
    protected SynsetPointer[] ptrs;
    protected SynsetFrame[] frames;
    protected String[] glosses;
    
    public Synset()                            //构造函数
    {
    	
    }
    public Synset(int offset,int lex_filenum,PartOfSpeech ss_type,int w_cnt,WordSense[] words,int p_cnt,SynsetPointer[] ptrs,String[] glosses)
    {
    	this.offset = offset;
    	this.lex_filenum = lex_filenum;
    	this.ss_type = ss_type;
    	this.w_cnt = w_cnt;
    	this.words = words;
    	this.p_cnt = p_cnt;
    	this.ptrs = ptrs;
    	this.glosses = glosses;
    }
    
    public Synset(int offset,int lex_filenum,PartOfSpeech ss_type,int w_cnt,WordSense[] words,int p_cnt,SynsetPointer[] ptrs,SynsetFrame[] frames,String[] glosses)
    {
    	this.offset = offset;
    	this.lex_filenum = lex_filenum;
    	this.ss_type = ss_type;
    	this.w_cnt = w_cnt;
    	this.words = words;
    	this.p_cnt = p_cnt;
    	this.ptrs = ptrs;
    	this.frames = frames;
    	this.glosses = glosses;
    }
 
    
    
    public int getOffset() {
        return offset;
    }
    public int getLexFilenum() {
        return lex_filenum;
    }
    public PartOfSpeech getSSType() {
        return ss_type;
    }
    public int getWordCount() {
        return w_cnt;
    }
    public WordSense[] getWords() {
        return words;
    }
    public int getLexId() {
        return lex_id;
    }
    public int getPtrCount() {
        return p_cnt;
    }
    public SynsetPointer[] getPointers() {
        return ptrs;
    }
    public SynsetFrame[] getFrames() {
        return frames;
    }
    public String[] getGlosses() {
        return glosses;
    }

    public String toString() {
        return "[" + ss_type + ", " + "[" + Printing.printListToString(words, ",") + "]]";
    }

    /**
     * Dump this Synset to the given output.
     * This is primarily for debugging.
     */
    public void dump(PrintWriter output) {
        output.write(ss_type + ": " + Printing.printListToString(words) + "\n");
        output.write("  offset=" + offset + ", lex_filenum=" + lex_filenum + ", lex_id=" + lex_id + "\n");
        output.write("  pointers: " + p_cnt + "\n");
        for (int i=0; i < p_cnt; i++) {
            output.write("   " + i + ": " + ptrs[i] + "\n");
        }
        if (frames != null) {
            output.write("  frames: " + frames.length + "\n");
            for (int i=0; i < frames.length; i++) {
                output.write("   " + i + ": " + frames[i] + "\n");
            }
        }
        output.write("  glosses: " + glosses.length + "\n");
        for (int i=0; i < glosses.length; i++) {
            output.write("   \"" + glosses[i] + "\"\n");
        }
    }

    /**
     * Positions the given input to offset, reads the next synset, and
     * returns it.
     */
    public static Synset read(WordNetFileReader input, int offset) throws IOException {
        input.seek(offset);
        Synset synset = new Synset();
        synset.offset = input.readNumber();
        synset.lex_filenum = input.readNumber();
        synset.ss_type = PartOfSpeech.read(input);
        synset.w_cnt = input.readHexNumber();
        synset.words = readWords(input, synset.w_cnt);
        synset.p_cnt = input.readNumber();
        synset.ptrs = readPointers(input, synset.p_cnt);
        if (synset.ss_type.equals(PartOfSpeech.VERB)) {
            // only in data.verb
            synset.frames = readFrames(input);
        }
        synset.glosses = readGlosses(input);
        input.skipToNewline();
        return synset;
    }

    protected static WordSense[] readWords(WordNetFileReader input, int w_cnt) throws IOException {
        WordSense[] words = new WordSense[w_cnt];
        for (int i=0; i < w_cnt; i++) {
            words[i] = WordSense.read(input);
        }
        return words;
    }

    protected static SynsetPointer[] readPointers(WordNetFileReader input, int p_cnt) throws IOException {
        SynsetPointer[] ptrs = new SynsetPointer[p_cnt];
        for (int i=0; i < p_cnt; i++) {
            ptrs[i] = SynsetPointer.read(input);
        }
        return ptrs;
    }

    protected static SynsetFrame[] readFrames(WordNetFileReader input) throws IOException {
        int cnt = input.readNumber();
        SynsetFrame[] frames = new SynsetFrame[cnt];
        for (int i=0; i < cnt; i++) {
            frames[i] = SynsetFrame.read(input);
        }
        return frames;
    }

    protected static String[] readGlosses(WordNetFileReader input) throws IOException {
        List<String> glosses = new ArrayList<String>();
        while (input.skipVerticalBar()) {
            glosses.add(input.readToVerticalBar());
        }
        return glosses.toArray(new String[glosses.size()]);
    }

    /**
     * Return the ``sense key'' for the given WordSense in this Synset.
     * As specified in the WordNet SENSEIDX manpage, this format is:
     * <pre>
     *  lemma%ss_type:lex_filenum:lex_id:head_word:head_id
     * </pre>
     * The lex_id is apparently that of the lemma (WordSense), not the one
     * associated with this Synset.
     * It isn't specified in the docs, but the head word (and id)
     * for an adjective satellite is the first word in its list of words
     * (and its lex_id).
     * It is specified that for non-satellite senses, the head_word and
     * head_id are empty but the colons are present.
     * @see http://wordnet.princeton.edu/wordnet/man/senseidx.5WN.html
     */
    public String getSenseKey(WordSense ws) {
        String sense_key = String.format("%s%%%1d:%02d:%02d",
                                         ws.getWord(),
                                         ss_type.getNumber(),
                                         lex_filenum,
                                         ws.getLexId());
        if (ss_type == PartOfSpeech.ADJS) {
            WordSense head = words[0];
            String head_word = head.getWord();
            int head_id = head.getLexId();
            sense_key += String.format(":%s:%02d", head_word, head_id);
        } else {
            sense_key += "::";
        }
        return sense_key;
    }
    public int hashCode(){
    	return (offset<<3)+ss_type.number;
    }
    public boolean equals(Object arg0){
    	if (arg0 instanceof Synset)
    		return this.offset==((Synset) arg0).offset
    			&& this.ss_type.number==((Synset) arg0).ss_type.number;
    	else
    		return false;
    }
}
