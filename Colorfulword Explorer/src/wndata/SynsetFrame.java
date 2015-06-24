package wndata;

import java.io.IOException;

/*
 * A SynsetFrame is a pair of numbers (f_num, w_num).
 * 可以考虑将这个SynsetFrame仿照PointerSymbol也实现成丄1�7个enum类�1�7�1�7
 */
public class SynsetFrame {

    protected int f_num;
    protected int w_num;
    
    public SynsetFrame()
    {
    	
    }
    public SynsetFrame(int f_num,int w_num)
    {
    	this.f_num = f_num;
    	this.w_num = w_num;
    }
    
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
