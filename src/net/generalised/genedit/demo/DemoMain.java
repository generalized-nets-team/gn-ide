package net.generalised.genedit.demo;

import java.io.File;
import java.io.StringReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.generalised.genedit.baseapp.plugins.PluginsRegistry;
import net.generalised.genedit.controller.MainController;
import net.generalised.genedit.main.Main;
import net.generalised.genedit.main.MainPlugin;
import net.generalised.genedit.model.GnDocument;
import net.generalised.genedit.model.dataaccess.xmlread.GnHandler;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.simulation.SimulationPlugin;
import net.generalised.genedit.view.MainForm;

import org.xml.sax.InputSource;
import org.xml.sax.helpers.ParserAdapter;

public class DemoMain extends Main {

	// XXX
	public static boolean inDemoMode = false;
	
	public static Demo demo;
	
	@Override
	protected void registerPlugins() {
		PluginsRegistry.registerPlugin(new MainPlugin());
		PluginsRegistry.registerPlugin(new SimulationPlugin());
	}
	
	@Override
	protected void loadInitialGn(MainForm inst, File fileToOpen) {
		
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			ParserAdapter pa = new ParserAdapter(sp.getParser());
			GnHandler handler = new GnHandler();
			pa.setContentHandler(handler);
			InputSource src = new InputSource(new StringReader(demo.getGnXml()));
			pa.parse(src);
			
			GeneralizedNet gn = handler.getGn();
			
			GnDocument document = new GnDocument(gn, true);
			inst.setDocument(document);
			MainController.openTab(inst, document.getModel());
			inst.getCurrentGnView().setZoom(90);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	@Override
	protected void loadConfigurationSettings() {
		// do nothing
	}
	
	@Override
	protected boolean shouldDisplayExceptionDialogs() {
		return false;
	}
	
	public static void main(String[] args) {
		
		inDemoMode = true;

		if (args.length < 2) {
			System.err.println("Please provide a class name for the demo.");
			return;
		}
		
		String demoClassName = args[1];
		try {
			demo = (Demo) Class.forName(demoClassName).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		new DemoMain().run(args);
	}
}
