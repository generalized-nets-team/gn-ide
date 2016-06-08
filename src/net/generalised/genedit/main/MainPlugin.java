package net.generalised.genedit.main;

import net.generalised.genedit.baseapp.controller.ControllerRegistry;
import net.generalised.genedit.baseapp.plugins.Plugin;
import net.generalised.genedit.controller.EditGnController;
import net.generalised.genedit.controller.MainController;
import net.generalised.genedit.model.gn.FunctionFactory;
import net.generalised.genedit.model.gn.GntcflFunctionFactory;
import net.generalised.genedit.model.gn.JavaScriptFunctionFactory;

public class MainPlugin extends Plugin {

	@Override
	public void initialize() {

		ControllerRegistry.registerControllers(new MainController());
		ControllerRegistry.registerControllers(new EditGnController());
		
		FunctionFactory.addFactory(new GntcflFunctionFactory());
		FunctionFactory.addFactory(new JavaScriptFunctionFactory());

		setDefaultProperty(MainConfigProperties.NEW_GN_NAME, "Untitled");
		setDefaultProperty(MainConfigProperties.DEFAULT_FUNCTION_LANGUAGE, JavaScriptFunctionFactory.LANGUAGE);
		setDefaultProperty(MainConfigProperties.DEFAULT_TRANSITION_PREFIX, "Z");
		setDefaultProperty(MainConfigProperties.DEFAULT_PLACE_PREFIX, "l");
		setDefaultProperty(MainConfigProperties.DEFAULT_TOKEN_PREFIX, "alpha");
	}
}
