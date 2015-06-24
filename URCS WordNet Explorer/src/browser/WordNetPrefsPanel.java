/*
 * File: edu/cs/rochester/WordNet/browser/WordNetPanel.java
 * Creator: George Ferguson
 * Created: Tue Jun  8 15:51:31 2010
 * Time-stamp: <Tue Jun 15 18:48:52 EDT 2010 ferguson>
 */

package browser;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.*;

import wordNetManager.*;

/**
 * A PrefsPanel for manipulating WordNet preferences (in particular,
 * where WordNet is installed).
 */
public class WordNetPrefsPanel extends PrefsPanel implements ActionListener, HierarchyListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected JLabel currentWNLocation;
    protected JLabel currentWNMissing;
    protected JRadioButton radioWNChoose;
    protected JButton chooseButton;
    protected JRadioButton radioWNEnvvar;
    protected JLabel currentWNEnvvar;
    protected JRadioButton radioWNDefault;
    protected JLabel currentWNDefault;

    /**
     * Construct and return a new WordNetPrefsPanel for manipulating
     * WordNet-related preferences.
     */
    public WordNetPrefsPanel(Prefs prefs) {
	super(prefs);
	createUI();
	syncUIWithEnvironment();
	syncUIWithPrefs();
	addHierarchyListener(this);
    }

    /**
     * Create and layout this WordNetPrefsPanel's UI elements.
     */
    protected void createUI() {

	setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

	currentWNLocation = new JLabel("None");
	currentWNMissing = new JLabel(createWarningIcon());
	currentWNMissing.setToolTipText("Current WordNet location does not exist!");
	radioWNChoose = new JRadioButton("Choose WordNet location:");
	chooseButton = new JButton("Choose");
	radioWNEnvvar = new JRadioButton("Use WNHOME/WNSEARCHDIR setting:");
	currentWNEnvvar = new JLabel("WNHOME/WNSEARCHDIR not set in environment");
	radioWNDefault = new JRadioButton("Use default WordNet location:");
	currentWNDefault = new JLabel(WordNetManager.WNHOME_DEFAULT);

	ButtonGroup group = new ButtonGroup();
	group.add(radioWNChoose);
	group.add(radioWNEnvvar);
	group.add(radioWNDefault);

	chooseButton.addActionListener(this);
	radioWNChoose.setActionCommand("radio");
	radioWNChoose.addActionListener(this);
	radioWNEnvvar.setActionCommand("radio");
	radioWNEnvvar.addActionListener(this);
	radioWNDefault.setActionCommand("radio");
	radioWNDefault.addActionListener(this);

	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	JPanel subpanel;

	subpanel = new JPanel();
	subpanel.setLayout(new BoxLayout(subpanel, BoxLayout.X_AXIS));
	subpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
	JLabel label1 = new JLabel("Current WordNet installation:");
	subpanel.add(label1);
	add(subpanel);

	subpanel = new JPanel();
	subpanel.setLayout(new BoxLayout(subpanel, BoxLayout.X_AXIS));
	subpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
	subpanel.add(Box.createRigidArea(new Dimension(20,0)));
	subpanel.add(currentWNLocation);
	subpanel.add(Box.createRigidArea(new Dimension(20,0)));
	subpanel.add(currentWNMissing);
	add(subpanel);

	add(Box.createRigidArea(new Dimension(0,10)));

	subpanel = new JPanel();
	subpanel.setLayout(new BoxLayout(subpanel, BoxLayout.X_AXIS));
	subpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
	subpanel.add(radioWNChoose);
	subpanel.add(chooseButton);
	add(subpanel);

	add(radioWNEnvvar);
	radioWNDefault.setAlignmentX(Component.LEFT_ALIGNMENT);

	subpanel = new JPanel();
	subpanel.setLayout(new BoxLayout(subpanel, BoxLayout.X_AXIS));
	subpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
	subpanel.add(Box.createRigidArea(new Dimension(40,0)));
	subpanel.add(currentWNEnvvar);
	add(subpanel);

	add(radioWNDefault);
	radioWNDefault.setAlignmentX(Component.LEFT_ALIGNMENT);

	subpanel = new JPanel();
	subpanel.setLayout(new BoxLayout(subpanel, BoxLayout.X_AXIS));
	subpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
	subpanel.add(Box.createRigidArea(new Dimension(40,0)));
	subpanel.add(currentWNDefault);
	add(subpanel);
    }

    /**
     * Use the UIManager's OptionPane.warningIcon, but scale it
     * to a reasonable size.
     */
    protected Icon createWarningIcon() {
	// Lookup the icon
	Icon icon = UIManager.getIcon("OptionPane.warningIcon");
	// Convert it to a BufferedImage
	int width = icon.getIconWidth();
	int height = icon.getIconHeight();
	BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	Graphics g = img.createGraphics();
	icon.paintIcon(this, g, 0, 0);
	// Resize that image
	float aspect = (float)width / (float)height;
	height = 24;
	width = Math.round(aspect * height);
	BufferedImage img2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	g = img2.createGraphics();
	g.drawImage(img, 0, 0, width, height, null);
	// Finally return an ImageIcon made from the resized image
	return new ImageIcon(img2);
    }

    /**
     * Set this WordNetPrefsPanel's UI elements to reflect the current
     * environment variables settings.
     */
    protected void syncUIWithEnvironment() {
	String location = null;
	String s = System.getenv("WNSEARCHDIR");
	if (s != null) {
	    location = s;
	} else if ((s = System.getenv("WNHOME")) != null) {
	    location = s + File.separator + "dict";
	}
	if (location != null) {
	    radioWNEnvvar.setEnabled(true);
	    currentWNEnvvar.setText(location);
	} else {
	    radioWNEnvvar.setEnabled(false);
	}
    }

    /**
     * Set this WordNetPrefsPanel's UI elements to reflect the current
     * preferences settings.
     */
    protected void syncUIWithPrefs() {
	String source = prefs.getWNLocationSource();
	String location = null;
	if (source.equals("choose") &&
	    (location = prefs.getWNLocation()) != null) {
	    radioWNChoose.setSelected(true);
	    setCurrentLocation(location);
	} else if (source.equals("envvar") &&
		   (location = currentWNEnvvar.getText()) != null) {
	    radioWNEnvvar.setSelected(true);
	    setCurrentLocation(location);
	} else {
	    radioWNDefault.setSelected(true);
	    setCurrentLocation(currentWNDefault.getText());
	}
	syncUIWithRadioButtons();
    }

    /**
     * Adjust this WordNetPrefsPanel's UI elements based on the
     * currently selected radio button.
     */
    protected void syncUIWithRadioButtons() {
	String location = null;
	if (radioWNChoose.isSelected()) {
	    location = currentWNLocation.getText();
	    chooseButton.setEnabled(true);
	    currentWNEnvvar.setEnabled(false);
	    currentWNDefault.setEnabled(false);
	} else if (radioWNEnvvar.isSelected()) {
	    location = currentWNEnvvar.getText();
	    chooseButton.setEnabled(false);
	    currentWNEnvvar.setEnabled(true);
	    currentWNDefault.setEnabled(false);
	} else {
	    location = currentWNDefault.getText();
	    chooseButton.setEnabled(false);
	    currentWNEnvvar.setEnabled(false);
	    currentWNDefault.setEnabled(true);
	}
	setCurrentLocation(location);
    }

    /**
     * Set the currentWNLocation label and update the currentWNMissing
     * icon.
     */
    protected void setCurrentLocation(String location) {
	currentWNLocation.setText(location);
	syncWNMissingIcon();
    }

    /**
     * Make the currentWNMissingIcon visible or not depending
     * on whether the current WN location exists or not.
     */
    protected void syncWNMissingIcon() {
	String location = currentWNLocation.getText();
	if (location != null) {
	    File file = new File(location);
	    currentWNMissing.setVisible(!file.exists());
	} else {
	    currentWNMissing.setVisible(false);
	}
    }

    /**
     * ActionListener callback for radio and "Choose" buttons.
     */
    public void actionPerformed(ActionEvent evt) {
	String cmd = evt.getActionCommand();  
	if (cmd.equals("radio")) {
	    syncUIWithRadioButtons();
	} else if (cmd.equals("Choose")) {
	    JFileChooser fc = new JFileChooser(currentWNLocation.getText());
	    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
		File file = fc.getSelectedFile();
		String path = file.getPath();
		setCurrentLocation(path);
	    }
	}
    }

    /**
     * Save current state of UI elements in prefs.
     */
    public void save() {
	String source;
	String location = "";
	if (radioWNChoose.isSelected()) {
	    source = Prefs.WN_LOCATION_SOURCE_CHOOSE;
	    location = currentWNLocation.getText();
	} else if (radioWNEnvvar.isSelected()) {
	    source = Prefs.WN_LOCATION_SOURCE_ENVVAR;
	} else {
	    source = Prefs.WN_LOCATION_SOURCE_DEFAULT;
	}
	prefs.setWNLocationSource(source);
	prefs.setWNLocation(location);
    }

    /**
     * HierarchyListener method called whenever something happens
     * in this window's hierarchy. This is much more reliable than
     * ComponentListener, whose semantics is so not what you need.
     */
    public void hierarchyChanged(HierarchyEvent e) {
	if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
	    if (isShowing()) {
		syncUIWithPrefs();
	    }
	}
    }
}
