/*
 * File: edu/cs/rochester/WordNet/wn/SynsetFrame.java
 * Creator: George Ferguson
 * Created: Fri May 21 15:21:26 2010
 * Time-stamp: <Thu Jun  3 11:35:13 EDT 2010 ferguson>
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

import java.io.IOException;

/*
 * A SynsetFrame is a pair of numbers (f_num, w_num).
 */
public class SynsetFrame {

    protected int f_num;
    protected int w_num;

    public int getFNum() {
	return f_num;
    }
    public int getWNum() {
	return w_num;
    }

    public String toString() {
	return "[" + f_num + ", " + w_num + "]";
    }

    /**
     * Read and return the next SynsetFrame from the input.
     */
    public static SynsetFrame read(WordNetFileReader input) throws IOException {
	SynsetFrame frame = new SynsetFrame();
	frame.f_num = input.readNumber();
	frame.w_num = input.readHexNumber();
	return frame;
    }
}
