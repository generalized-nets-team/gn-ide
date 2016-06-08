package net.generalised.genedit.fileimport;

import java.io.IOException;
import java.util.List;

import net.generalised.genedit.model.gn.GeneralizedNet;

class Test extends GnBinaryImporter {
	@Override protected void importToGn(List<Pair> pairs, GeneralizedNet gn) {
	}
	
	public static void main(String[] args) throws IllegalArgumentException, IOException {//delete this :)
		GnBinaryImporter test = new Test();
		//OK: test.fileToGn("M:\\MYDOX\\Computers\\GN\\GenneteImportSamples\\two-transitions.vgn", null);
		//OK: test.fileToGn("C:\\My Documents\\driveQuantum5\\GN\\old\\~Gennete\\hospital_pm.vgn", null);
		OK: test.importFileToGn("M:\\MYDOX\\Computers\\GN\\GenneteImportSamples\\g.vgn", null);
		//OK: test.fileToGn("M:\\MYDOX\\Computers\\GN\\GenneteImportSamples\\mini-transition-2-places-predicate.vgn", null);
	}

}

