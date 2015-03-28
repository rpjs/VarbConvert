/**
 * 
 */
package uk.org.kalevala.varbconvert.display;

import java.awt.Event;
import java.awt.FileDialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import uk.org.kalevala.varbconvert.*;

import uk.org.kalevala.varbconvert.convert.*;

import uk.org.kalevala.varbconvert.io.*;

/**
 * @author Roy Stilling
 *
 */
public class VarbDisplay extends JFrame {

	private VarbConvert m_varbConvert;
	private VarbDisplay m_varbDisplay;
	private JSplitPane m_mainContainer = null;
	private JTextArea m_edInput = null;
	private JTextArea m_edOutput = null;
	
	public VarbDisplay(VarbConvert varbConvert) {

		m_varbConvert = varbConvert;
		m_varbDisplay = this;
		
		initialize();
		
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		
		
		//Add a window listener
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				m_varbDisplay.shutDown();
			}
		});

		this.setSize(640,480);
		this.setContentPane(getMainContainer());
		this.setTitle(m_varbConvert.NAME+" "+m_varbConvert.VER_MAJOR+"."+m_varbConvert.VER_MINOR);
		this.setLocation(50,50);
		buildMenus();
		this.setVisible(true);

	}

	private void buildMenus() {

		Action actionExit = new AbstractAction("Exit") {
			public void actionPerformed(ActionEvent e) {
				m_varbDisplay.shutDown();
			}
		};

		Action actionLoad = new AbstractAction("Open Input") {
			public void actionPerformed(ActionEvent e) {
				m_varbDisplay.loadFile();
			}
		};

		Action actionSave = new AbstractAction("Save Output") {
			public void actionPerformed(ActionEvent e) {
				m_varbDisplay.saveFile();
			}
		};

		Action actionConvert = new AbstractAction("CSV -> TKN") {
			public void actionPerformed(ActionEvent e) {
				try {
					m_edOutput.setSelectionStart(0);
					m_edOutput.setSelectionEnd(m_edOutput.getText().length());
					m_edOutput.cut();
				} catch (Exception ex) {
				}
				VarbConverter varbConverter = new VarbConverter(m_edInput, m_edOutput);
				boolean convertedOK = varbConverter.convert();
				if (convertedOK) {
					JOptionPane.showMessageDialog(m_varbDisplay, "File conversion complete.","Information",JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(m_varbDisplay, "Faled to convert file!","Error",JOptionPane.ERROR_MESSAGE);
				}
			}
		};

		Action actionAbout = new AbstractAction("About") {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(m_varbDisplay, "VarbConvert v"+m_varbConvert.VER_MAJOR+"."+m_varbConvert.VER_MINOR+"."+m_varbConvert.VER_REVISION+"\n"+"Software development by "+m_varbConvert.AUTHOR+"\nProject design by "+m_varbConvert.PM, "About", JOptionPane.INFORMATION_MESSAGE);		
			}
		};

		KeyStroke altA = KeyStroke.getKeyStroke(KeyEvent.VK_A,Event.CTRL_MASK);
		KeyStroke altC = KeyStroke.getKeyStroke(KeyEvent.VK_C,Event.CTRL_MASK);
		KeyStroke altO = KeyStroke.getKeyStroke(KeyEvent.VK_O,Event.CTRL_MASK);
		KeyStroke altS = KeyStroke.getKeyStroke(KeyEvent.VK_S,Event.CTRL_MASK);
		KeyStroke altX = KeyStroke.getKeyStroke(KeyEvent.VK_X,Event.CTRL_MASK);

		JMenuBar mB = new JMenuBar();

		JMenu menuFile = new JMenu("File");
		menuFile.setMnemonic('f');
		menuFile.add(actionLoad).setAccelerator(altO);
		menuFile.add(actionSave).setAccelerator(altS);
		menuFile.addSeparator();
		menuFile.add(actionExit).setAccelerator(altX);
		
		JMenu menuConvert = new JMenu("Convert");
		menuConvert.setMnemonic('c');
		menuConvert.add(actionConvert).setAccelerator(altC);
		
		JMenu menuHelp = new JMenu("Help");
		menuHelp.setMnemonic('h');
		menuHelp.add(actionAbout).setAccelerator(altA);
		
		this.setJMenuBar(mB);
		mB.add(menuFile);
		mB.add(menuConvert);
		mB.add(menuHelp);
	}

	private void loadFile() {
		//Pop up a dialog and let the user select the file to load
		FileDialog fileDialogue = new FileDialog(this,"Open input file",FileDialog.LOAD);
		FilenameFilter textFilter = new TextFilter();
		fileDialogue.setFilenameFilter(textFilter);
		fileDialogue.setVisible(true);
		m_varbConvert.setInFilePath(fileDialogue.getDirectory());
		m_varbConvert.setInFileName(fileDialogue.getFile());
		if (m_varbConvert.getInFilePath() != null && m_varbConvert.getInFileName() != null) {
			loadFile(m_varbConvert.getInFilePath()+m_varbConvert.getInFileName());
		}
	}
	
	private void loadFile(String filePath) {
		if (filePath != null && !filePath.equals("")) {
			try {
				m_edInput.setText(new InputReader(filePath).readFile());
				m_edInput.moveCaretPosition(0);
				m_edInput.setSelectionStart(0);
				m_edInput.setSelectionEnd(0);
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(m_varbDisplay, "Cannot find file '"+filePath+"'","Error",JOptionPane.ERROR_MESSAGE);
			} catch (IOException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(m_varbDisplay, "Cannot open file '"+filePath+"'","Error",JOptionPane.ERROR_MESSAGE);
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(m_varbDisplay, "Error handling file '"+filePath+"'","Error",JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void saveFile() {
		//Pop up a dialog and let the user select the file to load
		if (m_varbConvert.getInFilePath() != null && m_varbConvert.getInFileName() != null) {
			FileDialog fileDialogue = new FileDialog(this,"Save out file as",FileDialog.SAVE);
			FilenameFilter tokenFilter = new TokenFilter();
			fileDialogue.setFilenameFilter(tokenFilter);
			fileDialogue.setVisible(true);
			m_varbConvert.setOutFilePath(fileDialogue.getDirectory());
			m_varbConvert.setOutFileName(fileDialogue.getFile());
			if (m_varbConvert.getInFilePath() != null && m_varbConvert.getInFileName() != null) {
				saveFile(m_varbConvert.getOutFilePath()+m_varbConvert.getOutFileName());
			}
		} else {
			JOptionPane.showMessageDialog(m_varbDisplay, "You cannot save an output file until you've opened and converted an input file.","Error",JOptionPane.ERROR_MESSAGE);
		}
	}

	private void saveFile(String filePath) {
		if (filePath != null && !filePath.equals("")) {
			try {
				OutputWriter outputWriter = new OutputWriter(filePath);
				outputWriter.writeFile(m_edOutput.getText());
			} catch (IOException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(m_varbDisplay, "Cannot open file '"+filePath+"'","Error",JOptionPane.ERROR_MESSAGE);
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(m_varbDisplay, "Error handling file '"+filePath+"'","Error",JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public void shutDown() {
		int answer = JOptionPane.NO_OPTION;
		if ((m_varbConvert.getInFilePath() != null && m_varbConvert.getInFileName() != null) && (m_varbConvert.getOutFilePath() == null || m_varbConvert.getOutFileName() == null)) {
			answer = JOptionPane.showConfirmDialog(m_varbDisplay,"Output file not saved - do you want to save it now?","Confirm",JOptionPane.YES_NO_OPTION);
		}
		if (answer == JOptionPane.YES_OPTION) {
			saveFile();
		}
		m_varbConvert.shutDown();
	}

	/**
	 * This method initializes m_mainContainer	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getMainContainer() {
		if (m_mainContainer == null) {
			m_mainContainer = new JSplitPane();
			JScrollPane scrollL = new JScrollPane(getEdInput());
			JScrollPane scrollR = new JScrollPane(getEdOutput());
			m_mainContainer.setDividerLocation(this.getWidth() / 2);
			m_mainContainer.setLeftComponent(scrollL);
			m_mainContainer.setRightComponent(scrollR);
		
		}
		return m_mainContainer;
	}

	/**
	 * This method initializes m_edInput	
	 * 	
	 * @return javax.swing.JEditorPane	
	 */
	private JTextArea getEdInput() {
		if (m_edInput == null) {
			m_edInput = new JTextArea();
		}
		return m_edInput;
	}

	/**
	 * This method initializes m_edOutput	
	 * 	
	 * @return javax.swing.JEditorPane	
	 */
	private JTextArea getEdOutput() {
		if (m_edOutput == null) {
			m_edOutput = new JTextArea();
		}
		return m_edOutput;
	}

}
