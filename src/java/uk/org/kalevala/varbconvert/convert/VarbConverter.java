package uk.org.kalevala.varbconvert.convert;

import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class VarbConverter {

	private JTextArea m_edInput = null;
	private JTextArea m_edOutput = null;
	//private HashMap m_fieldEntries = null;
	private TreeMap m_fieldSubs = null;
	private HashMap m_ignoreFields = null;
	
	public VarbConverter(JTextArea newInput, JTextArea newOutput) {
	
		m_edInput = newInput;
		m_edOutput = newOutput;

		//m_fieldEntries = new HashMap();
		m_fieldSubs = new TreeMap();
		m_ignoreFields = new HashMap();
		
	}
	
	public boolean convert() {

		boolean convertedOK = true;

		if (m_edInput == null || m_edInput == null || m_edInput.getText() == null || m_edInput.getText().equals("")) {
			
			convertedOK = false;
			
		} else {
				
			ArrayList groupNames = new ArrayList();
			
			StringTokenizer inputRecs = new StringTokenizer(m_edInput.getText(),"\n");
			
			boolean foundFields = false;
			boolean startedWriting = false;
			int tokenPos = -1;
			
			while (inputRecs.hasMoreTokens()) {

				String thisLine = inputRecs.nextToken();
				
				if (thisLine.contains("token") || thisLine.contains("Token")) {
					
					foundFields = true;
					//m_edOutput.append("[Data]\n");
					
					//Tokenise this line for field names
					StringTokenizer fieldNames = new StringTokenizer(thisLine,",");
					
					int i = 0;
					while(fieldNames.hasMoreTokens()) {
						
						String fieldName = fieldNames.nextToken().replace("\"",""); 
						groupNames.add(fieldName);
						
						if (fieldName.equalsIgnoreCase("token")) {
							tokenPos = i;
						}
						
						i++;
						
					}

				} else if (thisLine.substring(0,1).equalsIgnoreCase(";")) {
					
					// coment line
					m_edOutput.append(thisLine+"\n");
					
				} else if (foundFields == false) {
					
					// treat as coment line
					m_edOutput.append(";"+thisLine+"\n");
					
				} else {
					
					if (tokenPos < 0) {
						// treat as coment line
						m_edOutput.append(";"+thisLine+"\n");
					} else {

						//Tokenise this line for field content
						thisLine = thisLine.replace(",,",", ,");
						StringTokenizer lineContent = new StringTokenizer(thisLine,",");
	
						ArrayList theseFields = new ArrayList();
						while(lineContent.hasMoreTokens()) {
							theseFields.add(lineContent.nextToken().replace("\"",""));
						}
						String thisToken = (String) theseFields.get(tokenPos);
	
						if (thisToken == null || thisToken.equals("") || thisToken.equals(" ")) {
							// treat as coment line
							m_edOutput.append(";"+thisLine+"\n");
						} else {
						
							m_edOutput.append("(");
						
							for(int i = 0; i < theseFields.size(); i++) {
								
								String thisField = (String) theseFields.get(i);
								String groupName = (String) groupNames.get(i);
								if (groupName.equalsIgnoreCase("token")) {
									thisToken = thisField;
								} else {
									m_edOutput.append(validatedField(thisField,groupName,thisToken,startedWriting));
								}
							}
							
							m_edOutput.append("   "+thisToken+"\n");
							startedWriting = true;
							
						}

					}
					
				}
				
			}
			
			// Add list of substitutions
			if (m_fieldSubs != null && m_fieldSubs.keySet() != null) {
				for (Iterator i = m_fieldSubs.keySet().iterator(); i.hasNext();) {
					String fieldName = (String) i.next();
					TreeMap subsMap = (TreeMap) m_fieldSubs.get(fieldName);
					if (subsMap != null && subsMap.keySet() != null) {
						m_edOutput.append(";\n");
						m_edOutput.append(";Field '"+fieldName+"' has the following substitutions:\n");
						for (Iterator j = subsMap.keySet().iterator(); j.hasNext();) {
							String subName = (String) j.next();
							String subValue = (String) subsMap.get(subName);
							m_edOutput.append(";;"+subName+":"+subValue+"\n");
						}
					}
				}
			}
		
		}	
		
		return convertedOK;
		
	}
	
	private String validatedField(String fieldContent, String groupName, String tokenValue, boolean skipAskIgnore) {
		
		String retVal = " ";
		
		String tokenDValue = "";
		if (tokenValue != null && !tokenValue.equals("")) {
			tokenDValue=" for token '"+tokenValue+"'";
		}
		if (fieldContent.length() <= 5) {
			fieldContent = fieldContent.replace(" ","");
		}
		
		if (fieldContent.equals("")) {
			
			retVal = " ";
			
		} else if (m_ignoreFields.get(groupName) != null) {
			
			retVal = "";
			
		} else if (fieldContent.length() == 1) {
			
			retVal = fieldContent; 
			
		} else {
			
			TreeMap subsList = null;

			//First see if we already have an subst list for this field
			if (m_fieldSubs.get(groupName) != null) {
				
				// see if we have a sub value for this item
				subsList = (TreeMap) m_fieldSubs.get(groupName);
				
				// see if we have a sub entry for this value
				if (subsList.get(fieldContent) != null) {
					
					return (String) subsList.get(fieldContent);
					
				}
				
			} else {
				
				subsList = new TreeMap();
				
			}

			//Second ask if we're to ignore this field
			if (!skipAskIgnore) {
			
				int ignoreResult = JOptionPane.showConfirmDialog(null,"The field value '"+fieldContent+"'"+tokenDValue+" is too long to be a Goldvarb group value.  Should field '"+groupName+"' be skipped?", "Field too long", JOptionPane.YES_NO_OPTION);
				
				if (ignoreResult == JOptionPane.YES_OPTION) {
					m_ignoreFields.put(groupName, "ignore");
					return "";
				}
				
			}
			
			//Third ask for a substitution value
			String subValue = validatedField(JOptionPane.showInputDialog("The field value '"+fieldContent+"'"+tokenDValue+" is too long to be a Goldvarb group value.  Please enter a ONE CHARACTER substitution value"), groupName, tokenValue, true);
		
			subsList.put(fieldContent, subValue);
			
			m_fieldSubs.put(groupName, subsList);
			
			retVal = subValue;
			
		}
		
		return retVal;
		
	}
	 
	public JTextArea getEdInput() {
		return m_edInput;
	}
	
	public void setEdInput(JTextArea edInput) {
		this.m_edInput = edInput;
	}
	
	public JTextArea getEdOutput() {
		return m_edOutput;
	}
	
	public void setEdOutput(JTextArea edOutput) {
		this.m_edOutput = edOutput;
	}

}
