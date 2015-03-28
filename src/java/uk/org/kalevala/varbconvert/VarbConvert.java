/**
 * 
 */
package uk.org.kalevala.varbconvert;

import uk.org.kalevala.varbconvert.display.*;

import javax.swing.UIManager;

/**
 * @author Roy Stilling
 *
 */
public class VarbConvert {
	
	public final String NAME = "VarbConvert";
	public final String VER_MAJOR = "1";
	public final String VER_MINOR = "0";
	public final String VER_REVISION = "3";
	public final String AUTHOR = "Roy Stilling";
	public final String PM = "Suzanne LaBelle";
	
	private String m_inFilePath, m_inFileName;
	private String m_outFilePath, m_outFileName;
	
	private VarbDisplay m_varbDisplay = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			VarbConvert varbConvert = new VarbConvert();
		}

	}
	
	public VarbConvert() {
		
		m_varbDisplay = new VarbDisplay(this);
		
	}
	
	public String getInFileName() {
		return m_inFileName;
	}
	
	public String getInFilePath() {
		return m_inFilePath;
	}
	
	public String getOutFileName() {
		return m_outFileName;
	}
	
	public String getOutFilePath() {
		return m_outFilePath;
	}
	
	public void setInFileName(String newValue) {
		m_inFileName = newValue;
	}
	
	public void setInFilePath(String newValue) {
		m_inFilePath = newValue;
	}
	
	public void setOutFileName(String newValue) {
		m_outFileName = newValue;
	}

	public void setOutFilePath(String newValue) {
		m_outFilePath = newValue;
	}

	public void shutDown() {
		System.exit(0);
	}

}
