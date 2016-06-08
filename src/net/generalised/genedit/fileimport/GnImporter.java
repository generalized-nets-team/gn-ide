package net.generalised.genedit.fileimport;

import java.io.IOException;

import net.generalised.genedit.model.gn.GeneralizedNet;

public interface GnImporter {

	/**
	 * @param fileName
	 * @param gn - GeneralizedNet to import data to. Cannot be null.
	 * @throws IOException
	 */
	void importFileToGn(String fileName, GeneralizedNet gn) throws IOException, IllegalArgumentException;
	//old: Can be null or non-empty GN model.
	
}
