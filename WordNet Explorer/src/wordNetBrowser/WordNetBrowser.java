package wordNetBrowser;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import wordNetBrowser.WBrowser;

public class WordNetBrowser
{
	public static void main(String[] args)
	{
		WBrowser browser = new WBrowser("WordNet Browser", new Dimension(1380, 840));
		browser.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
	}
}
