package net.generalised.genedit.fileexport;

import java.io.File;
import java.io.IOException;

import net.generalised.genedit.model.gn.GeneralizedNet;

public interface FileExporter {

	public void export(GeneralizedNet gn, File outputFile) throws IOException;
}
