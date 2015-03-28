package uk.org.kalevala.varbconvert.io;

import java.io.File;
import java.io.FilenameFilter;

public class TokenFilter implements FilenameFilter {

	public boolean accept(File dir, String name) {
		return name.endsWith(".tkn");
	}

}