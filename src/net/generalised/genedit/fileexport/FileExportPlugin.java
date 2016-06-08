package net.generalised.genedit.fileexport;

import net.generalised.genedit.baseapp.controller.ControllerRegistry;
import net.generalised.genedit.baseapp.plugins.Plugin;

public class FileExportPlugin extends Plugin {

	@Override
	public void initialize() {
		ControllerRegistry.registerControllers(new FileExportController());
	}

}
