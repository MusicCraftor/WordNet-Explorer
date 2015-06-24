package browser;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.JFrame;
import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class MenuButton extends JToggleButton{
    private JPopupMenu menu;
    public MenuButton(){
        this("");
    }
    public MenuButton(final String label){
        super(label);
        this.setHorizontalTextPosition(SwingConstants.RIGHT );
        addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    if(isSelected()){
                        setSelected(false);
                        if (menu != null)
                            menu.show(MenuButton.this, 0, MenuButton.this.getHeight());
                    }else{
                        if (menu != null)
                            menu.setVisible(false);
                    }
                }
            });
    }
    public void setMenu(JPopupMenu menu){
        this.menu=menu;
    }

    public static void main(String[] argv) {
        JPopupMenu optionMenu = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("Test1");
        optionMenu.add(menuItem);
        menuItem = new JMenuItem("Test2");
        optionMenu.add(menuItem);

        MenuButton b = new MenuButton("option");
        b.setMenu(optionMenu);
        JFrame j = new JFrame();
        j.add(b);
        j.pack();
        j.setVisible(true);
    }
}
