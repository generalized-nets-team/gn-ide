package net.generalised.genedit.fileimport;

import net.generalised.genedit.baseapp.controller.ControllerRegistry;
import net.generalised.genedit.baseapp.plugins.Plugin;
import net.generalised.genedit.fileimport.controller.ImportController;

public class FileImportPlugin extends Plugin {

	@Override
	public void initialize() {
		ControllerRegistry.registerControllers(new ImportController());
	}

}
