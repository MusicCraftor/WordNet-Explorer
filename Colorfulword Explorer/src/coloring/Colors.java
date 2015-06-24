package coloring;
/*
 * Colors类来存放多个Color。
 */
public class Colors implements ColorStoreInfo{
	public Color[] data;
	/*
	 * 产生一个全空的Colors
	 */
	public Colors(){
		data=new Color[STORENUM];
		for (int i=0;i<STORENUM;++i)
			data[i]=new Color(0,0,0,0);
	}
	/*
	 * 利用之前序列化的data，还原Colors类
	 */
	public Colors(byte[] data){
		this();
		for (int i=0;i<STORENUM;++i){
//			System.out.println((int)data[i*4]+","+(int)data[i*4+1]+" "+(int)data[i*4+2]+","+(int)data[i*4+3]);
			this.data[i].setR((int)(data[i*4])+128);
			this.data[i].setG((int)(data[i*4+1])+128);
			this.data[i].setB((int)(data[i*4+2])+128);
			this.data[i].setA((int)(data[i*4+3])+128);
		}
	}
	/*
	 * 将此类序列化
	 */
	public byte[] serialize(){
		byte[] res=new byte[STORENUM*4];
		for (int i=0;i<STORENUM;++i){
			res[i*4]=(byte) (data[i].getR()-128);
			res[i*4+1]=(byte) (data[i].getG()-128);
			res[i*4+2]=(byte) (data[i].getB()-128);
			res[i*4+3]=(byte) (data[i].getA()-128);
//			System.out.println((int)res[i*4]+","+(int)res[i*4+1]+" "+(int)res[i*4+2]+","+(int)res[i*4+3]);
		}
		return res;
	}
	public void addColor(Color color){
		for (int i=0;i<STORENUM;++i)
			if (data[i].getA()==0){
				data[i]=color;
				return;
			}
		int j=0,k=-1;
		for (int i=1;i<STORENUM;++i)
			if (data[i].getA()>data[j].getA()){
				k=j;j=i;
			}else if (k==-1 || data[i].getA()>data[k].getA())
				k=i;
		char tot=(char) (data[j].getA()+data[k].getA());
		data[j].setR((char) (data[j].getR()*data[j].getA()/tot+data[k].getR()*data[k].getA()/tot));
		data[j].setG((char) (data[j].getG()*data[j].getA()/tot+data[k].getG()*data[k].getA()/tot));
		data[j].setB((char) (data[j].getB()*data[j].getA()/tot+data[k].getB()*data[k].getA()/tot));
		data[j].setA(tot);
		if (tot>255)
			for (int i=0;i<STORENUM;++i)
				data[i].setA((char) (data[i].getA()>>1));
		data[k]=color;
	}
	public void setR(int index,int r){
		data[index].setR(r);
	}
	public void setG(int index,int g){
		data[index].setG(g);
	}
	public void setB(int index,int b){
		data[index].setB(b);
	}
	public void setA(int index,int a){
		data[index].setA(a);
	}
	public int getR(int index){
		return data[index].getR();
	}
	public int getG(int index){
		return data[index].getG();
	}
	public int getB(int index){
		return data[index].getB();
	}
	public int getA(int index){
		return data[index].getA();
	}
}