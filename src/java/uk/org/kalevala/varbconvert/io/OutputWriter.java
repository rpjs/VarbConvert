package uk.org.kalevala.varbconvert.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class OutputWriter {

	String m_fileOutput = null;
	FileWriter m_fileWriter = null; 

	public OutputWriter(String filePath) throws IOException {
		this.setFileOutput(filePath);
		m_fileWriter = new FileWriter(filePath);
	}

	public void writeFile(String outputText) {
		
		BufferedWriter bufferedWriter = null;
		PrintWriter out = null;

		bufferedWriter = new BufferedWriter(m_fileWriter);
		out = new PrintWriter(bufferedWriter);

		out.print(outputText);
		out.flush();
		out.close();
		
	}

	public String getFileOutput() {
		return m_fileOutput;
	}

	public void setFileOutput(String fileOutput) {
		this.m_fileOutput = fileOutput;
	}
}
