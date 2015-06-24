package wndata;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


/***
    LZYNOTE:这个类用来代表一个Index条目。不过他实现的时候用的是丄1�7个Read方法生成数据，你也可以实现一个构造函敄1�7
***/

public class IndexEntry {
    protected String lemma;
    protected PartOfSpeech pos;
    protected int synset_cnt;
    protected int p_cnt;
    protected String[] ptr_symbols;
    protected int sense_cnt;
    protected int tagsense_cnt;
    protected int[] synset_offsets;
    
    public IndexEntry()
    {
    	
    }
    //IndexEntry(String, PartOfSpeech, int, int, String[], String, int[]) is
    public IndexEntry(String lemma,PartOfSpeech pos,int synset_cnt,int p_cnt,String[] ptr_symbols,int tagsense_cnt,int[] synset_offsets)
    {
    	this.lemma = lemma;
    	this.pos = pos;
    	this.synset_cnt = synset_cnt;
    	this.p_cnt = p_cnt;
    	this.ptr_symbols = ptr_symbols;
    	this.tagsense_cnt = tagsense_cnt;
    	this.synset_offsets = synset_offsets;
    }
    
    public String getLemma() {
        return lemma;
    }
    public PartOfSpeech getPartOfSpeech() {
        return pos;
    }
    public int getSynsetCount() {
        return synset_cnt;
    }
    public int getPCount() {
        return p_cnt;
    }
    public String[] getPtrSymbols() {
        return ptr_symbols;
    }
    public int senseCount() {
        return sense_cnt;
    }
    public int[] getSynsetOffets() {
        return synset_offsets;
    }

    public String toString() {
        return this.getClass().getName() + "(" + lemma + ", " + pos + ")";
    }

    /**
     * Dump this IndexEntry to the given output.
     * This is primarily for debugging.
     */
    public void dump(PrintWriter output) {
        output.write("\"" + lemma + "\" " + pos + "\n");
        output.write("  ptr_symbols[" + p_cnt + "] = ");
        Printing.printList(ptr_symbols, output);
        output.write("\n");
        output.write("  synset_offsets[" + synset_cnt + "] = ");
        Printing.printList(synset_offsets, output);
        output.write("\n");
    }

    /**
     * Reads the next entry of a WordNet index from the given input and
     * returns the resulting IndexEntry.
     */
    public static IndexEntry read(WordNetFileReader input) throws IOException {
        IndexEntry entry = new IndexEntry();
        entry.lemma = input.readToken();
        entry.pos = PartOfSpeech.read(input);
        entry.synset_cnt = input.readNumber();
        entry.p_cnt = input.readNumber();
        entry.ptr_symbols = input.readTokenList(entry.p_cnt);
        entry.sense_cnt = input.readNumber(); // this is apparently redundant
        entry.tagsense_cnt = input.readNumber();
        entry.synset_offsets = input.readNumberList(entry.synset_cnt);
        input.skipToNewline();
        return entry;
    }

    /**
     * Returns an array of Synsets corresponding to the senses in this
     * IndexEntry, read from the given input.
     */
    public Synset[] readSynsets(WordNetFileReader input) throws IOException {
        Synset[] synsets = new Synset[synset_cnt];
        for (int i=0; i < synset_cnt; i++) {
            synsets[i] = Synset.read(input, synset_offsets[i]);
        }
        return synsets;
    }

    /**
     * Return the position (+1) of the given Synset in this IndexEntry's
     * list of senses (a.k.a., the ``sense number'').
     * Sense numbers start from 1 in WordNet.
     * Returns 0 if the Synset isn't one of the senses in this IndexEntry.
     */
    public int getSenseNumberForSynset(Synset synset) {
        for (int i=0; i < synset_cnt; i++) {
            if (synset_offsets[i] == synset.getOffset()) {
                return i+1;
            }
        }
        return 0;
    }

}
