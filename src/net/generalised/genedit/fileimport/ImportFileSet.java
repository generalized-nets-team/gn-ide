package net.generalised.genedit.fileimport;

public class ImportFileSet {

	private String vgnFileName;

	private String tknFileName;

	private String chrFileName;

	private String xmlFileName;

	private String texGFileName;

	private boolean importToCurrentFile;

	public String getVgnFileName() {
		return vgnFileName;
	}

	public void setVgnFileName(String vgnFileName) {
		this.vgnFileName = vgnFileName;
	}

	public String getTknFileName() {
		return tknFileName;
	}

	public void setTknFileName(String tknFileName) {
		this.tknFileName = tknFileName;
	}

	public String getChrFileName() {
		return chrFileName;
	}

	public void setChrFileName(String chrFileName) {
		this.chrFileName = chrFileName;
	}

	public String getXmlFileName() {
		return xmlFileName;
	}

	public void setXmlFileName(String xmlFileName) {
		this.xmlFileName = xmlFileName;
	}

	public String getTexGFileName() {
		return texGFileName;
	}

	public void setTexGFileName(String texGFileName) {
		this.texGFileName = texGFileName;
	}

	public boolean isImportToCurrentFile() {
		return importToCurrentFile;
	}

	public void setImportToCurrentFile(boolean importToCurrentFile) {
		this.importToCurrentFile = importToCurrentFile;
	}

}
