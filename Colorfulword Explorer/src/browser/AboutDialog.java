package browser;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;


public class AboutDialog extends JDialog{
	private JTextArea text=new JTextArea("About: ColorfulWord\nAuthor: 刘智猷  张泰之  徐源盛  安传凯\nVersion：0.999\n");
	private JButton button=new JButton("Confirm");
	AboutDialog(Frame owner){
		super(owner,"About",true);
		setPreferredSize(new Dimension(280,200));
		text.setEditable(false);
		
		add(text);
		add(button,BorderLayout.SOUTH);
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dispose();
			}

		});
		pack();
		setVisible(true);
	}
	public static void main(String[] argv){
		new AboutDialog(null);
	}
}
