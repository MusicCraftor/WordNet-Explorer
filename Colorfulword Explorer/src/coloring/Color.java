package coloring;

public class Color {
	private int r;
	private int g;
	private int b;
	private int a;
	Color(float r, float g, float b, float a){
		this.r=(int)(r*255+0.5);
		this.g=(int)(g*255+0.5);
		this.b=(int)(b*255+0.5);
		this.a=(int)(a*255+0.5);
	}
	Color(int r,int g,int b,int a){
		this.r=(int)r;
		this.g=(int)g;
		this.b=(int)b;
		this.a=(int)a;
	}
	public void setR(int r){
		this.r=r;
	}
	public void setG(int g){
		this.g=g;
	}
	public void setB(int b){
		this.b=b;
	}
	public void setA(int a){
		this.a=a;
	}
	public int getR(){
		return r;
	}
	public int getG(){
		return g;
	}
	public int getB(){
		return b;
	}
	public int getA(){
		return a;
	}
}
