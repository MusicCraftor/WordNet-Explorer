package coloring;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import wndata.*;

public class ColorManager implements ColorStoreInfo{
	static Map<String,Float> weight = new HashMap<String,Float>(); 
	static{
		weight.put("antonym",new Float(0.01f));
		weight.put("hypernym",new Float(0.1f));
		weight.put("instance hypernym",new Float(0.1f));
		weight.put("hyponym",new Float(0.2f));
		weight.put("instance hyponym",new Float(0.2f));
		weight.put("member holonym",new Float(0.05f));
		weight.put("substance holonym",new Float(0.05f));
		weight.put("part holonym",new Float(0.05f));
		weight.put("member meronym",new Float(0.15f));
		weight.put("substance meronym",new Float(0.15f));
		weight.put("part meronym",new Float(0.15f));
		weight.put("attribute",new Float(0.075f));
		weight.put("derivationally related form",new Float(0.4f));
		weight.put("domain of synset - TOPIC",new Float(0.05f));
		weight.put("member of this domain - TOPIC",new Float(0.15f));
		weight.put("domain of synset - REGION",new Float(0.05f));
		weight.put("member of this domain - REGION",new Float(0.15f));
		weight.put("domain of synset - USAGE",new Float(0.05f));
		weight.put("member of this domain - USAGE",new Float(0.15f));
		weight.put("entailment",new Float(0.2f));
		weight.put("cause",new Float(0.1f));
		weight.put("also see",new Float(0.01f));
		weight.put("verb group",new Float(0.1f));
		weight.put("similar to",new Float(0.5f));
		weight.put("particle of verb",new Float(0.4f));
		weight.put("pertainym (for adjectives) or derived from adjective (for adverbs)",new Float(0.6f));
	}
	private static ColorManager instance;
	private ColorManager(){}
	public static ColorManager getSingleton(){
		if (instance==null){
			instance=new ColorManager();
		}
		return instance;
	}
	public boolean setColor(Synset synset, java.awt.Color color_){
		Color color=new Color(color_.getRed(),color_.getGreen(),color_.getBlue(),0);
		final Map<Synset,Float> map=new HashMap<Synset,Float>();
		PriorityQueue<Synset> pq=new PriorityQueue<Synset>(1, new Comparator<Synset>(){
			public int compare(Synset arg0, Synset arg1){
				if (map.get(arg0)>map.get(arg1))
					return -1;
				else if (map.get(arg0)==map.get(arg1))
					return 0;
				else
					return 1;
			}
		});
		map.put(synset, new Float(255));
		pq.offer(synset);
		while (!pq.isEmpty()){
			Synset u=pq.poll();
			Float ui=map.get(u);
			color.setA((char) ui.intValue());
			Colors tmp=DataManager.getSingleton().getColor(u);
			tmp.addColor(color);
			DataManager.getSingleton().setColor(u, tmp);
			SynsetPointer[] sp=u.getPointers();
			for (int i=0;i<sp.length;++i){
				Synset v=DataManager.getSingleton().getSynset(sp[i].getSynsetOffset(), sp[i].getPartOfSpeech());
				Float vi=ui.floatValue()*weight.get(sp[i].getPointerSymbol().getDescription());
				if (vi>=1){
					Float vi_=map.get(v);
					if (vi_==null || vi_<vi){
						map.put(v, vi);
						if (vi_==null)
							pq.offer(v);
					}
				}
			}
		}
		return true;
	}
	public Color getColor(Synset synset){
		float r=0;
		float g=0;
		float b=0;
		Colors colors=DataManager.getSingleton().getColor(synset);
		int totWeight=0;
		for (int i=0;i<STORENUM;++i)
			totWeight+=colors.getA(i);
		for (int i=0;i<STORENUM;++i){
			r+=colors.getR(i)/255.0*colors.getA(i)/totWeight;
			g+=colors.getG(i)/255.0*colors.getA(i)/totWeight;
			b+=colors.getB(i)/255.0*colors.getA(i)/totWeight;
		}
		return new Color(r,g,b,0);
	}
	static public void main(String[] args){
		Synset synset=DataManager.getSingleton().lookup("red", PartOfSpeech.forChar('n'))[0];
//		ColorManager.getSingleton().setColor(synset, new java.awt.Color(255,0,0,0));
		Colors colors=DataManager.getSingleton().getColor(synset);
		for (int i=0;i<STORENUM;++i)
			System.out.println((int)colors.getR(i)+","+(int)colors.getG(i)+","+(int)colors.getB(i));
		System.out.println(synset.hashCode());
		colors.setR(0, 128);
		colors.setR(3, 63);
		colors.setR(2, 25);
		colors.setB(1, 255);
		DataManager.getSingleton().setColor(synset, colors);
		DataManager.getSingleton().writeColor();
	}
}
