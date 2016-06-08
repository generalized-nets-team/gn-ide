package net.generalised.genedit.main;

import java.io.File;
import java.io.IOException;

import net.generalised.genedit.baseapp.SettingsManager;
import net.generalised.genedit.baseapp.plugins.PluginsRegistry;
import net.generalised.genedit.baseapp.view.BaseView;
import net.generalised.genedit.controller.ViewUtil;
import net.generalised.genedit.demo.DemoMain;
import net.generalised.genedit.fileexport.FileExportPlugin;
import net.generalised.genedit.fileimport.FileImportPlugin;
import net.generalised.genedit.simulation.SimulationPlugin;
import net.generalised.genedit.view.Constants;
import net.generalised.genedit.view.MainForm;
import net.generalised.genedit.view.MainMenu.NewFileEvent;
import net.generalised.genedit.view.MainMenu.OpenFileEvent;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


/**
 * Entry point of this application.
 */
public class Main {

//	private static void loadSwtJar() {
//	String swtFileName = "";
//    try {
//        String osName = System.getProperty("os.name").toLowerCase();
//        String osArch = System.getProperty("os.arch").toLowerCase();
//        URLClassLoader classLoader = (URLClassLoader) new Object().getClass().getClassLoader();
//        Method addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
//        addUrlMethod.setAccessible(true);
//
//        String swtFileNameOsPart = 
//            osName.contains("win") ? "win32" :
//            osName.contains("mac") ? "macosx" :
//            osName.contains("linux") || osName.contains("nix") ? "linux_gtk" :
//            ""; // throw new RuntimeException("Unknown OS name: "+osName)
//
//        String swtFileNameArchPart = osArch.contains("64") ? "x64" : "x86";
//        swtFileName = "swt_"+swtFileNameOsPart+"_"+swtFileNameArchPart+".jar";
//        URL swtFileUrl = new URL("rsrc:"+swtFileName); // I am using Jar-in-Jar class loader which understands this URL; adjust accordingly if you don't
//        addUrlMethod.invoke(classLoader, swtFileUrl);
//    }
//    catch(Exception e) {
//        System.out.println("Unable to add the swt jar to the class path: " + swtFileName);
//        e.printStackTrace();
//    }
//}

	/**
	 * @param view
	 *            UI component of the view must be Control and it must not be
	 *            initialized by
	 *            {@link BaseView#initUIComponent(org.eclipse.swt.widgets.Widget)}.
	 */
	protected void createMainWindow(BaseView view) {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		view.initUIComponent(shell);
		Point size = ((Control) view.getUIComponent()).getSize();
		shell.setLayout(new FillLayout());
		shell.setText(Constants.APPLICATION_NAME);
		shell.layout();
		if (size.x == 0 && size.y == 0) {
			((Control) view.getUIComponent()).pack();
			shell.pack();
		} else {
			Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
			shell.setSize(shellBounds.width, shellBounds.height);
		}
		shell.open();
	}
	
	protected void registerPlugins() {
		// TODO some modules are needed for demos too!
		PluginsRegistry.registerPlugin(new MainPlugin());
		PluginsRegistry.registerPlugin(new FileExportPlugin());
		PluginsRegistry.registerPlugin(new FileImportPlugin());
		PluginsRegistry.registerPlugin(new SimulationPlugin());
		// TODO EmbeddedSimulation, JavaScript...
		// TODO check for JAR plugins too
	}
	
	protected void loadConfigurationSettings() {
		try {
			SettingsManager.getInstance().load();
		} catch (IOException e1) {
			SettingsManager.getInstance().reset();
			e1.printStackTrace();
		}
	}
	
	protected void loadInitialGn(MainForm inst, File fileToOpen) {
		if (fileToOpen != null) {
			inst.dispatchEvent(new OpenFileEvent(fileToOpen));
		} else {
			inst.dispatchEvent(NewFileEvent.class);
		}
	}
	
	protected void runApplication(final Shell shell, boolean showExceptionDialogs) {
		Display display = shell.getDisplay(); 
		while (!shell.isDisposed()) {
			try {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			} catch (final Exception e) {
				e.printStackTrace();
				if (showExceptionDialogs) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							ViewUtil.showExceptionMessageBox(shell, e);
						}
					});
				}
			}
		}
		display.dispose();
	}
	
	protected boolean shouldDisplayExceptionDialogs() {
		return true;
	}
	
	public void run(String[] args) {
		// TODO: add swt-xxx.jar as argument?
		// TODO: iterate over all swt-*jar files; catch exceptions until started
//		System.out.println("start");
		// loadSwtJar();
//		try {
//			URLClassLoader classLoader = (URLClassLoader) new Object().getClass().getClassLoader();
//	        Method addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
//	        addUrlMethod.setAccessible(true);
//	        String swtFileName = "swt-linux32.jar";
//	        URL swtFileUrl = new java.io.File(swtFileName).toURI().toURL();
//	        addUrlMethod.invoke(classLoader, swtFileUrl);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return;
//		}
		
		// TODO, especially for demos: GCJ! http://vertis.github.com/2007/06/24/native-java-with-gcj-and-swt.html
		
		MainForm inst = new MainForm();
		createMainWindow(inst);

		registerPlugins();
		
		loadConfigurationSettings();

		// command line arguments
		File fileToOpen = null;
		if (args.length > 0) {
			fileToOpen = new File(args[0]);
		}
		loadInitialGn(inst, fileToOpen);

		runApplication(inst.getUIComponent().getShell(), shouldDisplayExceptionDialogs());
	}
	
	public static void main(String[] args) {
		// TODO: disable this method if in demo mode!
		// if (DemoMain.inDemoMode) does not work!
		if (DemoMain.inDemoMode)
			return;
		
		new Main().run(args);
	}
	
}
