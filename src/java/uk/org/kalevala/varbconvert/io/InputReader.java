package uk.org.kalevala.varbconvert.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class InputReader {

	String m_fileInput = null;
	FileReader m_fileReader = null; 

	public InputReader(String filePath) throws FileNotFoundException {
		this.setFileInput(filePath);
		m_fileReader = new FileReader(filePath);
	}

	public String readFile() throws IOException, Exception {
		
		BufferedReader inputReader = null;
		
		StringBuffer readFile = new StringBuffer();
		
		inputReader = new BufferedReader(m_fileReader);

		String inputLine = null;
		while ((inputLine = inputReader.readLine()) != null) {
			readFile.append(inputLine+"\n");
		}
		inputReader.close();

		return readFile.toString();
		
	}

	public String getFileInput() {
		return m_fileInput;
	}

	public void setFileInput(String fileInput) {
		this.m_fileInput = fileInput;
	}
}
