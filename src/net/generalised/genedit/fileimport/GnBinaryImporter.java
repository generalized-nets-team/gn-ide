package net.generalised.genedit.fileimport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.generalised.genedit.model.gn.GeneralizedNet;

public abstract class GnBinaryImporter implements GnImporter {

	private List<Pair> nameValuePairs;
	
	private String readString(InputStream input, boolean isValue) throws IllegalArgumentException, IOException {
		int length = input.read();
		if (isValue && length > 4)
			length = input.read(); //TODO: ?
		else if (isValue)
			--length;
//		else if (!isValue && length == 0) {
//			length = input.read();
//			if (length == 0)//?
//				length = input.read();
//		}
		else if (!isValue)
			while(length == 0)
				length = input.read();
		
		if (length <= 0)
			return null;//TODO: or ""?
		
		byte[] buffer = new byte[length];
		int bytesRead;

		bytesRead = input.read(buffer, 0, length);
		if (bytesRead < length)
			throw new IllegalArgumentException("File broken or invalid format");
		
		return new String(buffer);
	}
	
	private String readMatrix(InputStream input) throws IOException, IllegalArgumentException {
		//Predicates 01 06 { 00 OR 05 wL1L2} 06 { 00 OR 05 wL1L2} ... 00
		StringBuilder result = new StringBuilder();
		input.read(); //01
		int b = input.read();
		while (b == 6) {
			int length = input.read();
			byte[] buffer = new byte[length];
			int bytesRead = input.read(buffer, 0, length);
			if (bytesRead < length)
				throw new IllegalArgumentException("File broken or invalid format");
			result.append(new String(buffer));
			result.append(';');
			
			b = input.read();
		}
		return result.toString();
	}
	
	private void readNameValuePairs(InputStream input) throws IOException, IllegalArgumentException {
		nameValuePairs = new ArrayList<Pair>();
		
		byte[] buffer = new byte[4];
		int bytesRead = input.read(buffer, 0, 4);
		if (bytesRead != 4 || buffer[0] != 'T' || buffer[1] != 'P' || buffer[2] != 'F' || buffer[3] != '0')
			throw new IllegalArgumentException("File is not created by Gennete");
		
		String key, value = null;
		
		while (true) {
			key = readString(input, false);
			value = null;
			if (key != null) {
				if (key.equals("Outlined") || key.equals("Arrowed")) {
					//value = null;
					input.read();
				} else if (key.equals("TimeFunctions"))
					;//value = null;
				else if (key.equals("Predicates") || key.equals("Capacities")
						|| key.equals("CharFn") || key.equals("InIndex") || key.equals("OutIndex")) {
					value = readMatrix(input);
				} else
					value = readString(input, true);
			}
			if (key == null && value == null)
				break;
			System.out.println(key + ": \"" + value + "\"");//TODO: Debug only
			nameValuePairs.add(new Pair(key, value));
		}
		
	}
	
	public void importFileToGn(String fileName, GeneralizedNet gn) throws IOException, IllegalArgumentException {
		File file = new File(fileName);
		if (file.exists() && file.length() > 0) {
			InputStream input =  new FileInputStream(file);
			try {
				readNameValuePairs(input);
				importToGn(nameValuePairs, gn);
			} finally {
				input.close();
			}
		} else throw new FileNotFoundException(fileName);	}
	
	protected abstract void importToGn(List<Pair> pairs, GeneralizedNet gn);

	protected class Pair {
		
		private String name;
		
		private String value;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Pair(String name, String value) {
			super();
			this.name = name;
			this.value = value;
		}
		
	}
}
