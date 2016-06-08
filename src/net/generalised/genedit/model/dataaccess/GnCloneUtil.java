package net.generalised.genedit.model.dataaccess;

import net.generalised.genedit.model.dataaccess.xmlread.GnXmlReader;
import net.generalised.genedit.model.dataaccess.xmlwrite.GnXmlWriter;
import net.generalised.genedit.model.gn.GeneralizedNet;

public class GnCloneUtil {

	private GnCloneUtil() {
	}
	
	public static GeneralizedNet cloneValidGn(GeneralizedNet gn) throws GnParseException {
		
		String gnSerialized = GnXmlWriter.getInstance().gnToXml(gn, false, true);
		GeneralizedNet result = GnXmlReader.getInstance().xmlStringToGn(gnSerialized);
		return result;
		
	}
}
