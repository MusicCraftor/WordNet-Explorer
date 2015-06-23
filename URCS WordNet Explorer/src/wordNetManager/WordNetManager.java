/*
 * File: edu/cs/rochester/WordNet/wn/WordNetManager.java
 * Creator: George Ferguson
 * Created: Tue May 18 13:21:20 2010
 * Time-stamp: <Mon Jun 14 14:55:18 EDT 2010 ferguson>
 *
 * Copyright 2010 George Ferguson, ferguson@cs.rochester.edu.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package wordNetManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * A WordNetManager maintains a set of WordNet indices and a set of
 * readers for accessing WordNet data files, both organized by part of speech
 * (like the WordNet files).
 * The indices are loaded in their entirety the first time they are needed
 * (that is, the first time a lookup is done for that part of speech).
 * The data files are opened the first time they are needed, but are never
 * loaded in the their entirety.
 */
public class WordNetManager {

    /**
     * The default base directory for WordNet.
     * @see http://wordnet.princeton.edu/wordnet/man/wnintro.1WN.html
     */
    public static final String WNHOME_DEFAULT = 
	(File.separatorChar == '\\' ?
	 "C:\\Program Files\\WordNet\\3.0" : "/usr/local/WordNet-3.0");
    /**
     * The default directory in which the WordNet database has been installed.
     * @see http://wordnet.princeton.edu/wordnet/man/wnintro.1WN.html
     */
    public static final String WNSEARCHDIR_DEFAULT =
	WNHOME_DEFAULT + File.separatorChar + "dict";

    /**
     * The pathname to the ``dict'' directory of WordNet, for this
     * WordNetManager.
     */
    protected String basepath;

    /**
     * All the parts of speech that have data files in WordNet.
     * Can't just use PartOfSpeech.values() here because, for example,
     * ADJS (type `s') doesn't have its own file.
     */
    public static final PartOfSpeech[] ALL_POS = { PartOfSpeech.NOUN,
						   PartOfSpeech.VERB,
						   PartOfSpeech.ADJ,
						   PartOfSpeech.ADV };

    /**
     * Construct and return an instance of WordNet initialized with the
     * given basepath (pathname of the ``dict'' directory).
     * For convenience, this method will also check for a subdirectory of
     * the given path named "dict".
     * If the given basepath is null, uses the value of the WNSEARCHDIR
     * environment variable (if set), otherwise the value of the WNHOME
     * environment variable (if set) with "dict" appended, otherwise
     * the WordNet default basepath.
     * @see WNHOME_DEFAULT
     * @see WNSEARCHDIR_DEFAULT
     */
    public WordNetManager(String basepath) throws IOException {
	if (basepath == null) {
	    basepath = System.getenv("WNSEARCHDIR");
	    if (basepath == null) {
		if ((basepath = System.getenv("WNHOME")) != null) {
		    basepath += File.separator + "dict";
		} else {
		    basepath = WNSEARCHDIR_DEFAULT;
		}
	    }
	}

	// Sanity check basepath immediately
	if (!isWordNetDir(basepath)) {
	    // Also consider adding "/dict" (I know we did this for the envvar)
	    if (!isWordNetDir(basepath + File.separator + "dict")) {
		throw new FileNotFoundException("Invalid WordNet basepath: " + basepath);
	    }
	    basepath += File.separator + "dict";
	}
    
	// Ok!
	this.basepath = basepath;

	// Could load indices now to shake out file issues immediately
	// but IOExceptions can still come when data is read
	//loadIndices();
    }

    /**
     * Returns true if path is a valid WordNet database directory
     * (that is, one containing the index and data files, named "dict"
     * by default).
     */
    public boolean isWordNetDir(String path) {
	File file = new File(path + File.separator + "index.noun");
	return file.exists();
    }

    /**
     * Construct and return a WordNetManager with the default basepath.
     * @see WordNetManager(String)
     */
    public WordNetManager() throws IOException {
	this(null);
    }

    /**
     * Return the pathname of the index file for the given PartOfSpeech,
     * for this WordNetManager.
     */
    protected String getIndexFilename(PartOfSpeech pos) {
	return basepath + File.separator + "index." + pos.getFilenameSuffix();
    }

    /**
     * Return the pathname of the data file for the given PartOfSpeech,
     * for this WordNetManager.
     */
    protected String getDataFilename(PartOfSpeech pos) {
	return basepath + File.separator + "data." + pos.getFilenameSuffix();
    }

    /**
     * The mapping from parts of speech to indices used by this WordNetManager.
     */
    protected Map<PartOfSpeech,Index> indexMap =
	new Hashtable<PartOfSpeech,Index>();

    /**
     * Return the Index for the given PartOfSpeech for this WordNetManager,
     * loading it if necessary (whihc may throw an IOException).
     */
    protected Index getIndex(PartOfSpeech pos) throws IOException {
	Index index = indexMap.get(pos);
	if (index == null) {
	    File file = new File(getIndexFilename(pos));
	    fireStatusReport("Loading index " + file.getName());
	    WordNetFileReader reader = new WordNetFileReader(file);
	    index = Index.read(reader);
	    reader.close();
	    indexMap.put(pos, index);
	}
	return index;
    }

    /**
     * Load the indices for all the parts of speech into this WordNetManager.
     * This method can be used to bring in all the WordNet indices
     * potentially required by this WordNetManager in one go.
     * Note however that individual lookups can still generate IOExceptions
     * when they access the data files.
     */
    protected void loadIndices() throws IOException {
	for (PartOfSpeech pos : ALL_POS) {
	    getIndex(pos);
	}
    }

    /**
     * The mapping from parts of speech to a Reader for the corresponding
     * data file. The file is opened and the Reader created the first time
     * the file is needed, and left open for the lifetime of this
     * WordNetManager.
     */
    protected Map<PartOfSpeech,WordNetFileReader> readerMap =
	new Hashtable<PartOfSpeech,WordNetFileReader>();

    /**
     * Return the WordNetFileReader for the given PartOfSpeech data file
     * for this WordNetManager, creating (opening) it if necessary (which
     * may throw an IOException).
     */
    protected WordNetFileReader getDataFileReader(PartOfSpeech pos) throws IOException {
	WordNetFileReader reader = readerMap.get(pos);
	if (reader == null) {
	    File file = new File(getDataFilename(pos));
	    reader = new WordNetFileReader(file);
	    readerMap.put(pos, reader);
	}
	return reader;
    }

    /**
     * Return the IndexEntry for the given lemma and PartOfSpeech, or null
     * if none is found.
     * @see prepareKey
     */
    protected IndexEntry getIndexEntry(String lemma, PartOfSpeech pos) throws IOException {
	Map<String,IndexEntry> map = getIndex(pos);
	return map.get(prepareKey(lemma));
    }

    /**
     * Prepare the given string for use as a key in a WordNet index.
     * Currently this amounts to downcasing it and replacing spaces
     * with underscores.
     */
    protected String prepareKey(String s) {
	return s.toLowerCase().replace(' ', '_');
    }

    protected boolean lookupSenseNumbers = true;
    protected boolean lookupSenseKeys = true;

    /**
     * Return an array of Synsets for the given string (lemma) and PartOfSpeech.
     */
    public Synset[] lookup(String lemma, PartOfSpeech pos) throws IOException {
	IndexEntry ie = getIndexEntry(lemma, pos);
	if (ie == null) {
	    return null;
	} else {
	    fireStatusReport("Looking up \"" + lemma + "\"");
	    Synset[] synsets = ie.readSynsets(getDataFileReader(pos));
	    if (lookupSenseNumbers) {
		for (Synset synset : synsets) {
		    for (WordSense ws : synset.getWords()) {
			ws.setSenseNumber(lookupSenseNumber(ws.getWord(), synset));
		    }
		}
	    }
	    if (lookupSenseKeys) {
		for (Synset synset : synsets) {
		    for (WordSense ws : synset.getWords()) {
			ws.setSenseKey(synset.getSenseKey(ws));
		    }
		}
	    }
	    return synsets;
	}
    }
    /**
     * Return all the Synsets for the given string (lemma) as any part
     * of speech as a Map from the PartOfSpeech to a Synset[].
     */
    public Map<PartOfSpeech,Synset[]> lookup(String s) throws IOException {
	Map<PartOfSpeech,Synset[]> map = new Hashtable<PartOfSpeech,Synset[]>();
	for (PartOfSpeech pos : ALL_POS) {
	    Synset[] synsets = lookup(s, pos);
	    if (synsets != null) {
		map.put(pos, synsets);
	    }
	}
	return map;
    }
     
    /**
     * Lookup and return the sense number for the given Synset in the
     * senses of the given lemma, or 0 if the Synset is not a sense of the
     * lemma.
     * Sense numbers come from the ordering of senses in the WordNet
     * index file.
     */
    public int lookupSenseNumber(String lemma, Synset synset) throws IOException {
	IndexEntry ie = getIndexEntry(lemma, synset.getSSType());
	if (ie == null) {
	    return 0;
	} else {
	    return ie.getSenseNumberForSynset(synset);
	}
    }

    public boolean getLookupSenseNumbers() {
	return lookupSenseNumbers;
    }
    public boolean getLookupSenseKeys() {
	return lookupSenseKeys;
    }
    public void setLookupSenseNumbers(boolean b) {
	lookupSenseNumbers = b;
    }
    public void setLookupSenseKeys(boolean b) {
	lookupSenseKeys = b;
    }

    /**
     * Return the Synset pointed to by the given SynsetPointer.
     * The handling of sense numbers and keys is taken from lookup().
     * Arguably it shouldn't happen here at all...
     */
    public Synset getPointerSynset(SynsetPointer ptr) throws IOException {
	WordNetFileReader input = getDataFileReader(ptr.getPartOfSpeech());
	Synset synset = Synset.read(input, ptr.getSynsetOffset());
	if (lookupSenseNumbers) {
	    for (WordSense ws : synset.getWords()) {
		ws.setSenseNumber(lookupSenseNumber(ws.getWord(), synset));
	    }
	}
	if (lookupSenseKeys) {
	    for (WordSense ws : synset.getWords()) {
		ws.setSenseKey(synset.getSenseKey(ws));
	    }
	}
	return synset;
    }

    /**
     * Finalizer for WordNetManager:
     * Closes all data file readers.
     */
    protected void finalize() {
	for (WordNetFileReader reader : readerMap.values()) {
	    try {
		reader.close();
	    } catch(IOException ex) {
	    }
	}
    }

    /**
     * Dump the contents of this WordNetManager to the given output.
     * This is primarily for debugging.
     */
    public void dump(PrintWriter output) {
	for (PartOfSpeech pos : ALL_POS) {
	    output.println(pos + ": ");
	    indexMap.get(pos).dump(output);
	}
    }

    /**
     * Test harness for WordNetManager.
     */
    public static void main(String[] argv) throws IOException {
	WordNetManager manager = new WordNetManager();
	/*
	manager.loadIndices();
	manager.dump(output);
	*/
	///*
	String word = argv[0];
	if (argv.length > 1) {
	    PartOfSpeech pos = PartOfSpeech.forString(argv[1]);
	    Synset[] synsets = manager.lookup(word, pos);
	    for (Synset synset : synsets) {
		printSynset(synset);
	    }
	} else {
	    Map<PartOfSpeech,Synset[]> map = manager.lookup(word);
	    for (PartOfSpeech pos : ALL_POS) {
		Synset[] synsets = map.get(pos);
		if (synsets != null) {
		    for (Synset synset : synsets) {
			printSynset(synset);
		    }
		}
	    }
	}
	//output.flush();
	//output.close();
	//*/
    }

    protected static void printSynset(Synset synset) {
	System.out.print(synset.getSSType() + ": ");
	WordSense[] words = synset.getWords();
	for (int i=0; i < words.length; i++) {
	    WordSense ws = words[i];
	    String w = ws.getWord();
	    if (i > 0) {
		System.out.print(" ");
	    }
	    System.out.print(w + "#" + ws.getSenseNumber() + " (" + ws.getSenseKey() + ")");
	}
	System.out.println();
	String[] glosses = synset.getGlosses();
	for (int i=0; i < glosses.length; i++) {
	    System.out.println("   \"" + glosses[i] + "\"");
	}
    }

    /**
     * The list of WordNetManagerListeners listening to this WordNetManager.
     */
    protected List<WordNetManagerListener> listeners;

    /**
     * Add the given WordNetManagerListener to this WordNetManager's listeners.
     */
    public synchronized void addWordNetManagerListener(WordNetManagerListener l) {
	if (listeners == null) {
	    listeners = new ArrayList<WordNetManagerListener>(1);
	}
	listeners.add(l);
    }

    /**
     * Remove the given WordNetManagerListener from this WordNetManager's
     * listeners.
     */
    public synchronized void removeWordNetManagerListener(WordNetManagerListener l) {
	listeners.remove(l);
    }
    
    /**
     * Notify this WordNetManager's listeners of a new status report.
     */
    public synchronized void fireStatusReport(String msg) {
	for (WordNetManagerListener l : listeners) {
	    l.statusReport(msg);
	}
    }
}
